package chdaeseung.accountbook.recurring.entity;

import chdaeseung.accountbook.bank.entity.BankAccount;
import chdaeseung.accountbook.category.entity.Category;
import chdaeseung.accountbook.transaction.entity.TransactionType;
import chdaeseung.accountbook.user.entity.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class RecurringTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String memo;

    private Long amount;

    private Integer dayOfMonth;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bank_account_id", nullable = false)
    private BankAccount bankAccount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @Builder
    public RecurringTransaction(String memo, Long amount, Integer dayOfMonth, BankAccount bankAccount, User user) {
        this.memo = memo;
        this.amount = amount;
        this.dayOfMonth = dayOfMonth;
        this.bankAccount = bankAccount;
        this.user = user;
    }

    public void update(String memo, Long amount, Integer dayOfMonth, BankAccount bankAccount) {
        this.memo = memo;
        this.amount = amount;
        this.dayOfMonth = dayOfMonth;
        this.bankAccount = bankAccount;
    }
}

