package chdaeseung.accountbook.transaction.dto;

import chdaeseung.accountbook.transaction.entity.ExpenseType;
import chdaeseung.accountbook.transaction.entity.Transaction;
import chdaeseung.accountbook.transaction.entity.TransactionType;
import lombok.Getter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
public class TransactionResponseDto {
    private Long id;

    private TransactionType type;

    private ExpenseType expenseType;

    private Long amount;

    private String category;

    private String memo;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    private Long bankAccountId;

    private String bankAccountName;

    private boolean transfer;

    private boolean recurring;

    public TransactionResponseDto(Transaction transaction) {
        this.id = transaction.getId();
        this.type = transaction.getType();
        this.expenseType = transaction.getExpenseType();
        this.amount = transaction.getAmount();
        this.memo = transaction.getMemo();
        this.date = transaction.getDate();
        this.transfer = transaction.isTransfer();
        this.recurring = transaction.getRecurringTransaction() != null;

        if(transaction.isTransfer()) {
            this.category = "이체";
        } else if(transaction.getCategory() != null) {
            this.category = transaction.getCategory().getName();
        } else {
            this.category = "-";
        }

        if(transaction.getBankAccount() != null) {
            this.bankAccountId = transaction.getBankAccount().getId();
            this.bankAccountName = transaction.getBankAccount().getAccountName();
        }
    }
}
