package chdaeseung.accountbook.dashboard.service;

import chdaeseung.accountbook.bank.entity.BankAccount;
import chdaeseung.accountbook.bank.entity.BankAccountType;
import chdaeseung.accountbook.bank.repository.BankAccountRepository;
import chdaeseung.accountbook.category.dto.CategoryExpenseDto;
import chdaeseung.accountbook.dashboard.dto.*;
import chdaeseung.accountbook.recurring.entity.RecurringTransaction;
import chdaeseung.accountbook.recurring.repository.RecurringTransactionRepository;
import chdaeseung.accountbook.transaction.dto.TransactionResponseDto;
import chdaeseung.accountbook.transaction.entity.Transaction;
import chdaeseung.accountbook.transaction.entity.TransactionType;
import chdaeseung.accountbook.transaction.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DashboardService {

    private final TransactionRepository transactionRepository;
    private final RecurringTransactionRepository recurringTransactionRepository;
    private final BankAccountRepository bankAccountRepository;

    public List<AssetTrendPointDto> getAssetTrend(Long userId, int year, int month) {
        List<DailyCashFlowDto> dailyCashFlows = transactionRepository.findDailyCashFlow(userId, year, month);

        Map<Integer, Long> cashFlowMap = dailyCashFlows.stream()
                .collect(Collectors.toMap(
                        DailyCashFlowDto::getDay,
                        DailyCashFlowDto::getAmountFlow
                ));
        YearMonth targetMonth = YearMonth.of(year, month);
        int endDay = targetMonth.lengthOfMonth();

        LocalDate today = LocalDate.now();
        YearMonth curMonth = YearMonth.from(today);

        long totalAmount = 0L;
        List<AssetTrendPointDto> result = new ArrayList<>();

        for(int day = 1; day <= endDay; day++) {
            LocalDate curDate = targetMonth.atDay(day);

            if(targetMonth.isAfter(curMonth)) {
                result.add(new AssetTrendPointDto(day + "일", null));
                continue;
            }

            if(targetMonth.equals(curMonth) && curDate.isAfter(today)) {
                result.add(new AssetTrendPointDto(day + "일", null));
                continue;
            }

            totalAmount += cashFlowMap.getOrDefault(day, 0L);
            result.add(new AssetTrendPointDto(day + "일", totalAmount));
        }

        return result;
    }

    public DashboardResponseDto getDashboard(Long userId) {
        LocalDate today = LocalDate.now();

        return getDashboard(userId, today.getYear(), today.getMonthValue());
    }

    public DashboardResponseDto getDashboard(Long userId, int year, int month) {
        LocalDate startDayOfMonth = LocalDate.of(year, month, 1);
        LocalDate endDayOfMonth = startDayOfMonth.withDayOfMonth(startDayOfMonth.lengthOfMonth());

        List<Transaction> monthlyTransactions = transactionRepository.findAllByUserIdAndDateBetween(userId, startDayOfMonth, endDayOfMonth);

        long totalIncome = 0L;
        long totalExpense = 0L;

        for(Transaction transaction : monthlyTransactions) {
            if(transaction.isTransfer()) {
                continue;
            }

            if (transaction.getType() == TransactionType.INCOME) {
                totalIncome += transaction.getAmount();
            } else if (transaction.getType() == TransactionType.EXPENSE) {
                totalExpense += transaction.getAmount();
            }
        }

        long balance = totalIncome - totalExpense;

        List<TransactionResponseDto> recentTransactions = transactionRepository
                .findTop5ByUserIdAndDateBetweenOrderByDateDescIdDesc(userId, startDayOfMonth, endDayOfMonth)
                .stream()
                .map(TransactionResponseDto::new)
                .toList();

        List<RecurringTransaction> recurringTransactions = recurringTransactionRepository.findAllByUserIdOrderByDayOfMonthAsc(userId);

        List<DashboardRecurringTransactionDto> recurringTransactionDtos = recurringTransactions.stream()
                .map(recurringTransaction -> new DashboardRecurringTransactionDto(
                        recurringTransaction.getId(),
                        recurringTransaction.getMemo(),
                        recurringTransaction.getAmount(),
                        recurringTransaction.getDayOfMonth(),
                        recurringTransaction.getBankAccount().getAccountName()
                )).toList();

        long monthlyRecurringExpenseTotal = recurringTransactions.stream()
                .mapToLong(RecurringTransaction::getAmount)
                .sum();

        List<BankAccount> bankAccountList = bankAccountRepository.findAllByUserId(userId);

        long totalBankAmount = bankAccountList.stream()
                .mapToLong(BankAccount::getBalance)
                .sum();

        List<DashboardBankAccountDto> bankAccounts = bankAccountRepository
                .findTop5ByUserIdOrderByBalanceDesc(userId)
                .stream()
                .map(bankAccount -> new DashboardBankAccountDto(
                        bankAccount.getId(),
                        bankAccount.getBankName(),
                        bankAccount.getAccountName(),
                        bankAccount.getBalance(),
                        getBankAccountTypeLabel(bankAccount.getType())
                ))
                .toList();

        return new DashboardResponseDto(year, month, totalIncome, totalExpense, balance, recentTransactions, recurringTransactionDtos, monthlyRecurringExpenseTotal, totalBankAmount, bankAccounts);
    }

    private String getBankAccountTypeLabel(BankAccountType type) {
        if(type == BankAccountType.CHECKING) return "입출금";
        if(type == BankAccountType.SAVING) return "적금";
        if(type == BankAccountType.DEPOSIT) return "예금";
        if(type == BankAccountType.CASH) return "현금";
        return "";
    }
}