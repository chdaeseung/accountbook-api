package chdaeseung.accountbook.transaction.dto;

import chdaeseung.accountbook.transaction.entity.ExpenseType;
import chdaeseung.accountbook.transaction.entity.Transaction;
import chdaeseung.accountbook.transaction.entity.TransactionType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class TransactionListResponseDto {
    private Long id;

    private LocalDate date;

    private String categoryName;

    private TransactionType type;

    private ExpenseType expenseType;

    private Long amount;

    private String memo;

    private boolean recurring;

    private Long bankAccountId;

    private String bankAccountName;

    public static TransactionListResponseDto from(Transaction transaction) {
        String categoryName;

        if(transaction.isTransfer()) {
            categoryName = "이체";
        } else if(transaction.getCategory() != null) {
            categoryName = transaction.getCategory().getName();
        } else {
            categoryName = "-";
        }

        return TransactionListResponseDto.builder()
                .id(transaction.getId())
                .date(transaction.getDate())
                .categoryName(categoryName)
                .type(transaction.getType())
                .amount(transaction.getAmount())
                .memo(transaction.getMemo())
                .recurring(transaction.getRecurringTransaction() != null)
                .bankAccountId(transaction.getBankAccount() != null ? transaction.getBankAccount().getId() : null)
                .bankAccountName(transaction.getBankAccount() != null ? transaction.getBankAccount().getBankName() + " - " + transaction.getBankAccount().getAccountName() : null)
                .build();
    }
}
