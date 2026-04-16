package chdaeseung.accountbook.dashboard.dto;

import chdaeseung.accountbook.category.dto.CategoryExpenseDto;
import chdaeseung.accountbook.transaction.dto.TransactionResponseDto;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class DashboardResponseDto {
    private int year;

    private int month;

    private Long totalIncome;

    private Long totalExpense;

    private long balance;

    private List<TransactionResponseDto> recentTransactions;

    private List<DashboardRecurringTransactionDto> recurringTransactions;

    private Long monthlyRecurringExpenseTotal;

    private Long totalBankAmount;

    private List<DashboardBankAccountDto> bankAccounts;
}
