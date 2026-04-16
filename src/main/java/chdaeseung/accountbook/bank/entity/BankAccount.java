package chdaeseung.accountbook.bank.entity;

import chdaeseung.accountbook.global.exception.CustomException;
import chdaeseung.accountbook.global.exception.ErrorCode;
import chdaeseung.accountbook.user.entity.User;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class BankAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String bankName;

    private String accountName;

    private Long balance;

    @Enumerated(EnumType.STRING)
    private BankAccountType type;

    private boolean negativeBalanceAllowed;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;


    @Builder
    public BankAccount(String bankName, String accountName, Long balance, BankAccountType type, boolean negativeBalanceAllowed, User user) {
        this.bankName = bankName;
        this.accountName = accountName;
        this.balance = balance;
        this.type = type;
        this.negativeBalanceAllowed = negativeBalanceAllowed;
        this.user = user;
    }

    public void update(String bankName, String accountName, Long balance, BankAccountType type, boolean negativeBalanceAllowed) {
        this.bankName = bankName;
        this.accountName = accountName;
        this.balance = balance;
        this.type = type;
        this.negativeBalanceAllowed = negativeBalanceAllowed;
    }

    public void increaseBalance(Long amount) {
        this.balance += amount;
    }

    public void decreaseBalance(Long amount) {
        long newBalance = this.balance - amount;

        if(!this.negativeBalanceAllowed && newBalance < 0) {
            throw new CustomException(ErrorCode.INSUFFICIENT_BALANCE);
        }
        this.balance = newBalance;
    }
}
