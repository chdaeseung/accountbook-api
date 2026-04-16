package chdaeseung.accountbook.recurring.service;

import chdaeseung.accountbook.bank.entity.BankAccount;
import chdaeseung.accountbook.bank.repository.BankAccountRepository;
import chdaeseung.accountbook.global.exception.CustomException;
import chdaeseung.accountbook.global.exception.ErrorCode;
import chdaeseung.accountbook.recurring.dto.RecurringDashboardResponseDto;
import chdaeseung.accountbook.recurring.dto.RecurringTransactionCreateDto;
import chdaeseung.accountbook.recurring.dto.RecurringTransactionListResponseDto;
import chdaeseung.accountbook.recurring.dto.RecurringTransactionResponseDto;
import chdaeseung.accountbook.recurring.entity.RecurringTransaction;
import chdaeseung.accountbook.recurring.repository.RecurringTransactionRepository;
import chdaeseung.accountbook.user.entity.User;
import chdaeseung.accountbook.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class RecurringTransactionService {

    private final RecurringTransactionRepository recurringTransactionRepository;
    private final UserRepository userRepository;
    private final BankAccountRepository bankAccountRepository;

    @Transactional(readOnly = true)
    public List<RecurringTransactionResponseDto> getRecurringTransactions(Long userId) {
        return recurringTransactionRepository.findAllByUserIdOrderByDayOfMonthAsc(userId).stream()
                .map(RecurringTransactionResponseDto::from)
                .toList();
    }

    public void createRecurringTransaction(Long userId, RecurringTransactionCreateDto createDto) {
        validateRecurring(createDto);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        BankAccount bankAccount = bankAccountRepository.findById(createDto.getBankAccountId())
                .orElseThrow(() -> new CustomException(ErrorCode.ACCOUNT_NOT_FOUND));

        validateBankAccount(userId, bankAccount);

        RecurringTransaction recurringTransaction = RecurringTransaction.builder()
                .memo(createDto.getMemo())
                .amount(createDto.getAmount())
                .dayOfMonth(createDto.getDayOfMonth())
                .bankAccount(bankAccount)
                .user(user)
                .build();

        recurringTransactionRepository.save(recurringTransaction);
    }

    @Transactional(readOnly = true)
    public RecurringTransactionResponseDto getRecurringTransaction(Long userId, Long recurringId) {
        RecurringTransaction recurringTransaction = recurringTransactionRepository.findByIdAndUserId(recurringId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.RECURRING_NOT_FOND));

        return RecurringTransactionResponseDto.from(recurringTransaction);
    }

    public void updateRecurringTransaction(Long userId, Long recurringId, RecurringTransactionCreateDto createDto) {
        validateRecurring(createDto);

        RecurringTransaction recurringTransaction = recurringTransactionRepository.findByIdAndUserId(recurringId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.RECURRING_NOT_FOND));

        BankAccount bankAccount = bankAccountRepository.findById(createDto.getBankAccountId())
                .orElseThrow(() -> new CustomException(ErrorCode.ACCOUNT_NOT_FOUND));

        validateBankAccount(userId, bankAccount);

        recurringTransaction.update(
                createDto.getMemo(),
                createDto.getAmount(),
                createDto.getDayOfMonth(),
                bankAccount
        );
    }

    public void deleteRecurringTransaction(Long userId, Long recurringId) {
        RecurringTransaction recurringTransaction = recurringTransactionRepository.findByIdAndUserId(recurringId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.RECURRING_NOT_FOND));

        recurringTransactionRepository.delete(recurringTransaction);
    }

    @Transactional(readOnly = true)
    public RecurringTransactionCreateDto getRecurringTransactionUpdate(Long userId, Long recurringId) {
        RecurringTransaction recurringTransaction = recurringTransactionRepository.findByIdAndUserId(recurringId, userId)
                .orElseThrow(() -> new CustomException(ErrorCode.RECURRING_NOT_FOND));

        RecurringTransactionCreateDto dto = new RecurringTransactionCreateDto();
        dto.setMemo(recurringTransaction.getMemo());
        dto.setAmount(recurringTransaction.getAmount());
        dto.setDayOfMonth(recurringTransaction.getDayOfMonth());
        dto.setBankAccountId(recurringTransaction.getBankAccount().getId());

        return dto;
    }

    @Transactional(readOnly = true)
    public RecurringDashboardResponseDto getRecurringDashboard(Long userId) {
        List<RecurringTransaction> recurringTransactions =
                recurringTransactionRepository.findAllByUserIdOrderByDayOfMonthAsc(userId);

        List<RecurringTransactionListResponseDto> recurringDtos = recurringTransactions.stream()
                .map(recurring -> new RecurringTransactionListResponseDto(
                        recurring.getId(),
                        recurring.getMemo(),
                        recurring.getDayOfMonth(),
                        recurring.getAmount(),
                        recurring.getBankAccount().getAccountName()
                ))
                .toList();

        long monthlyTotalAmount = recurringTransactions.stream()
                .mapToLong(RecurringTransaction::getAmount)
                .sum();

        return RecurringDashboardResponseDto.builder()
                .monthlyTotalAmount(monthlyTotalAmount)
                .recurringTransactions(recurringDtos)
                .build();
    }

    private void validateRecurring(RecurringTransactionCreateDto createDto) {
        if (createDto.getAmount() == null || createDto.getAmount() <= 0) {
            throw new CustomException(ErrorCode.MINIMUM_AMOUNT);
        }

        if (createDto.getDayOfMonth() == null || createDto.getDayOfMonth() < 1 || createDto.getDayOfMonth() > 31) {
            throw new CustomException(ErrorCode.INCORRECT_DAY);
        }

        if (createDto.getBankAccountId() == null) {
            throw new CustomException(ErrorCode.ACCOUNT_NOT_FOUND);
        }
    }

    private void validateBankAccount(Long userId, BankAccount bankAccount) {
        if (!bankAccount.getUser().getId().equals(userId)) {
            throw new CustomException(ErrorCode.FORBIDDEN_ACCESS);
        }
    }
}