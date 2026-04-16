package chdaeseung.accountbook.transfer.entity;

import chdaeseung.accountbook.bank.entity.BankAccount;
import chdaeseung.accountbook.transaction.entity.Transaction;
import chdaeseung.accountbook.user.entity.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "transfers")
public class Transfer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long amount;

    private String memo;

    private LocalDate date;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_account_id", nullable = false)
    private BankAccount fromAccount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_account_id", nullable = false)
    private BankAccount toAccount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Builder
    public Transfer(Long amount, String memo, LocalDate date, BankAccount fromAccount, BankAccount toAccount, User user) {
        this.amount = amount;
        this.memo = memo;
        this.date = date;
        this.fromAccount = fromAccount;
        this.toAccount = toAccount;
        this.user = user;
    }
}
