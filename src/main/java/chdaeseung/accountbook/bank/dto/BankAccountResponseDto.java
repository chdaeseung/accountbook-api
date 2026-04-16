package chdaeseung.accountbook.bank.dto;

import chdaeseung.accountbook.bank.entity.BankAccountType;
import chdaeseung.accountbook.bank.entity.BankAccount;
import lombok.Getter;

@Getter
public class BankAccountResponseDto {
    private final Long id;

    private final String bankName;

    private final String accountName;

    private final Long balance;

    private final BankAccountType type;

    private final String typeLabel;

    private final boolean negativeBalanceAllowed;

    public BankAccountResponseDto(BankAccount bankAccount) {
        this.id = bankAccount.getId();
        this.bankName = bankAccount.getBankName();
        this.accountName = bankAccount.getAccountName();
        this.balance = bankAccount.getBalance();
        this.type = bankAccount.getType();
        this.typeLabel = getTypeLabel(bankAccount.getType());
        this.negativeBalanceAllowed = bankAccount.isNegativeBalanceAllowed();
    }

    private String getTypeLabel(BankAccountType type) {
        if(type == BankAccountType.CHECKING) return "입출금";
        if(type == BankAccountType.SAVING) return "저축";
        if(type == BankAccountType.DEPOSIT) return "예금";
        if(type == BankAccountType.CASH) return "현금";
        return "";
    }
}
