package chdaeseung.accountbook.bank.dto;

import chdaeseung.accountbook.bank.entity.BankAccount;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BankAccountSelectDto {
    private Long id;

    private String accountName;

    public static BankAccountSelectDto from(BankAccount bankAccount) {
        return new BankAccountSelectDto(
                bankAccount.getId(),
                bankAccount.getAccountName()
        );
    }
}
