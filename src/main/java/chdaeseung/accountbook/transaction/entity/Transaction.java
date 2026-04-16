package chdaeseung.accountbook.transaction.entity;

import chdaeseung.accountbook.bank.entity.BankAccount;
import chdaeseung.accountbook.category.entity.Category;
import chdaeseung.accountbook.recurring.entity.RecurringTransaction;
import chdaeseung.accountbook.user.entity.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "transactions")
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    private TransactionType type;

    @Enumerated(EnumType.STRING)
    private ExpenseType expenseType;

    private Long amount;

    private String memo;

    private LocalDate date;

    private boolean transfer;

    private String transferGroupKey;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recurring_transaction_id")
    private RecurringTransaction recurringTransaction;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bank_account_id")
    private BankAccount bankAccount;

    @Builder
    public Transaction(TransactionType type, ExpenseType expenseType, Long amount, Category category, String memo, LocalDate date, User user, RecurringTransaction recurringTransaction, BankAccount bankAccount, boolean transfer, String transferGroupKey) {
        this.type = type;
        this.expenseType = expenseType;
        this.amount = amount;
        this.category = category;
        this.memo = memo;
        this.date = date;
        this.user = user;
        this.recurringTransaction = recurringTransaction;
        this.bankAccount = bankAccount;
        this.transfer = transfer;
        this.transferGroupKey = transferGroupKey;
    }

    public void update(TransactionType type, ExpenseType expenseType, Long amount, Category category, String memo, LocalDate date, BankAccount bankAccount) {
        this.type = type;
        this.expenseType = expenseType;
        this.amount = amount;
        this.category = category;
        this.memo = memo;
        this.date = date;
        this.bankAccount = bankAccount;
    }
}
