package chdaeseung.accountbook.bank.service;

import chdaeseung.accountbook.bank.dto.*;
import chdaeseung.accountbook.bank.entity.BankAccount;
import chdaeseung.accountbook.bank.repository.BankAccountRepository;
import chdaeseung.accountbook.dashboard.dto.AssetTrendPointDto;
import chdaeseung.accountbook.global.exception.CustomException;
import chdaeseung.accountbook.global.exception.ErrorCode;
import chdaeseung.accountbook.transaction.repository.TransactionRepository;
import chdaeseung.accountbook.user.entity.User;
import chdaeseung.accountbook.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BankAccountService {

    private final BankAccountRepository bankAccountRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    @Transactional
    public Long create(Long userId, BankAccountRequestDto requestDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        BankAccount bankAccount = BankAccount.builder()
                .bankName(requestDto.getBankName())
                .accountName(requestDto.getAccountName())
                .balance(requestDto.getBalance())
                .type(requestDto.getType())
                .negativeBalanceAllowed(requestDto.isNegativeBalanceAllowed())
                .user(user)
                .build();

        return bankAccountRepository.save(bankAccount).getId();
    }

    public List<BankAccountResponseDto> findAll(Long userId) {
        return bankAccountRepository.findAllByUserIdOrderByIdDesc(userId)
                .stream()
                .map(BankAccountResponseDto::new)
                .toList();
    }

    public BankAccountResponseDto findById(Long userId, Long id) {
        BankAccount bankAccount = bankAccountRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.ACCOUNT_NOT_FOUND));

        return new BankAccountResponseDto(bankAccount);
    }

    @Transactional
    public void update(Long userId, Long id, BankAccountRequestDto requestDto) {
        BankAccount bankAccount = bankAccountRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.ACCOUNT_NOT_FOUND));

        bankAccount.update(
                requestDto.getBankName(),
                requestDto.getAccountName(),
                requestDto.getBalance(),
                requestDto.getType(),
                requestDto.isNegativeBalanceAllowed()
        );
    }

    @Transactional
    public void delete(Long userId, Long id) {
        BankAccount bankAccount = bankAccountRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.ACCOUNT_NOT_FOUND));

        bankAccountRepository.delete(bankAccount);
    }

    @Transactional(readOnly = true)
    public List<BankAccountOptionDto> getUsedOptions(Long userId) {
        return bankAccountRepository.findAllByUserIdOrderByIdDesc(userId)
                .stream()
                .map(account -> new BankAccountOptionDto(
                        account.getId(), account.getBankName() + " - " + account.getAccountName()
                ))
                .toList();
    }

    public AssetDashboardResponseDto getAssetDashboard(Long userId, Integer year, Integer month) {
        LocalDate today = LocalDate.now();

        int targetYear = (year != null) ? year : today.getYear();
        int targetMonth = (month != null) ? month : today.getMonthValue();

        YearMonth targetYearMonth = YearMonth.of(targetYear, targetMonth);

        List<BankAccount> bankAccounts = bankAccountRepository.findAllByUserId(userId);

        List<BankAccountListResponseDto> bankAccountDtos = bankAccounts.stream()
                .map(BankAccountListResponseDto::new)
                .toList();

        long totalAsset = bankAccounts.stream()
                .mapToLong(BankAccount::getBalance)
                .sum();

        int negativeAllowedCount = (int) bankAccounts.stream()
                .filter(BankAccount::isNegativeBalanceAllowed)
                .count();

        String largestAccountName = bankAccounts.stream()
                .max(Comparator.comparingLong(BankAccount::getBalance))
                .map(BankAccount::getAccountName)
                .orElse("-");

        List<AssetShareDto> assetShare = createAssetShare(bankAccounts, totalAsset);
        List<AssetTrendPointDto> assetTrend = createDummyMonthlyAssetTrend(targetYearMonth, totalAsset);

        return AssetDashboardResponseDto.builder()
                .totalAsset(totalAsset)
                .monthAssetIncrease(0L)
                .monthAssetDecrease(0L)
                .negativeAllowedCount(negativeAllowedCount)
                .largestAccountName(largestAccountName)
                .bankAccounts(bankAccountDtos)
                .assetTrend(assetTrend)
                .assetShare(assetShare)
                .build();
    }

    private List<AssetShareDto> createAssetShare(List<BankAccount> bankAccounts, long totalAsset) {
        String[] colors = {
                "#2563eb",
                "#14b8a6",
                "#f59e0b",
                "#8b5cf6",
                "#ef4444",
                "#94a3b8"
        };

        List<BankAccount> sortedAccounts = bankAccounts.stream()
                .sorted((a, b) -> Long.compare(b.getBalance(), a.getBalance()))
                .toList();

        List<AssetShareDto> result = new ArrayList<>();

        long othersBalance = 0L;

        for(int i = 0; i < sortedAccounts.size(); i++) {
            BankAccount account = sortedAccounts.get(i);

            if(i < 5) {
                int percent = 0;

                if (totalAsset != 0) {
                    percent = (int) Math.round((account.getBalance() * 100.0) / totalAsset);
                }

                result.add(new AssetShareDto(
                        account.getId(),
                        account.getAccountName(),
                        account.getBalance(),
                        percent,
                        colors[i]
                ));
            } else {
                othersBalance += account.getBalance();
            }
        }

        if(sortedAccounts.size() > 5) {
            int othersPercent = 0;

            if(totalAsset != 0) {
                othersPercent = (int) Math.round((othersBalance * 100.0) / totalAsset);
            }

            result.add(new AssetShareDto(
                    null,
                    "기타",
                    othersBalance,
                    othersPercent,
                    colors[5]
            ));
        }

        return result;
    }

    private List<AssetTrendPointDto> createDummyMonthlyAssetTrend(YearMonth targetYearMonth, long totalAsset) {
        List<AssetTrendPointDto> result = new ArrayList<>();

        result.add(new AssetTrendPointDto(
                targetYearMonth.minusMonths(5).getMonthValue() + "월",
                totalAsset - 900000
        ));

        result.add(new AssetTrendPointDto(
                targetYearMonth.minusMonths(4).getMonthValue() + "월",
                totalAsset - 650000
        ));

        result.add(new AssetTrendPointDto(
                targetYearMonth.minusMonths(3).getMonthValue() + "월",
                totalAsset - 300000
        ));

        result.add(new AssetTrendPointDto(
                targetYearMonth.minusMonths(2).getMonthValue() + "월",
                totalAsset - 120000
        ));

        result.add(new AssetTrendPointDto(
                targetYearMonth.minusMonths(1).getMonthValue() + "월",
                totalAsset + 80000
        ));

        result.add(new AssetTrendPointDto(
                targetYearMonth.getMonthValue() + "월",
                totalAsset
        ));

        return result;
    }

    @Transactional(readOnly = true)
    public BankAccountDetailResponseDto getDetail(Long userId, Long bankAccountId) {
        BankAccount bankAccount = bankAccountRepository.findByIdAndUserId(bankAccountId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.ACCOUNT_NOT_FOUND));

        List<MonthlyExpenseRawDto> rawDtos = transactionRepository.getMonthlyExpenseByBankAccount(userId, bankAccountId);

        List<AssetTrendPointDto> monthlyExpenses = rawDtos.stream()
                .map(dto -> new AssetTrendPointDto(dto.getMonth() + "월", dto.getAmount()))
                .toList();

        List<AssetTrendPointDto> expenseTopCategories =
                transactionRepository.getExpenseTopCategoriesByBankAccount(userId, bankAccountId);

        return BankAccountDetailResponseDto.of(bankAccount, monthlyExpenses, expenseTopCategories);
    }

    @Transactional(readOnly = true)
    public List<BankAccountSelectDto> getBankAccountsForSelect(Long userId) {
        return bankAccountRepository.findAllByUserId(userId).stream()
                .map(BankAccountSelectDto::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<BankAccountSelectDto> getTransferTargetOptions(Long userId, Long id) {
        return bankAccountRepository.findAllByUserId(userId).stream()
                .filter(account -> !account.getId().equals(id))
                .map(account -> new BankAccountSelectDto(
                        account.getId(),
                        account.getAccountName()
                ))
                .toList();
    }
}
