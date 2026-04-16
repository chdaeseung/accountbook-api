package chdaeseung.accountbook.bank.dto;

import chdaeseung.accountbook.bank.entity.BankAccount;
import chdaeseung.accountbook.bank.entity.BankAccountType;
import lombok.Getter;

@Getter
public class BankAccountListResponseDto {
    private Long id;

    private String bankName;

    private String accountName;

    private Long balance;

    private BankAccountType type;

    private boolean negativeBalanceAllowed;

    public BankAccountListResponseDto(BankAccount bankAccount) {
        this.id = bankAccount.getId();
        this.bankName = bankAccount.getBankName();
        this.accountName = bankAccount.getAccountName();
        this.balance = bankAccount.getBalance();
        this.type = bankAccount.getType();
        this.negativeBalanceAllowed = bankAccount.isNegativeBalanceAllowed();
    }
}
