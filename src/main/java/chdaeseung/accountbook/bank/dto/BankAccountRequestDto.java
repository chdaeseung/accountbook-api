package chdaeseung.accountbook.bank.dto;

import chdaeseung.accountbook.bank.entity.BankAccountType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class BankAccountRequestDto {
    @NotBlank(message = "은행 이름을 입력해주세요.")
    private String bankName;

    @NotNull(message = "계좌 이름을 입력해주세요.")
    private String accountName;

    @NotNull(message = "잔액을 입력해주세요.")
    private Long balance;

    @NotNull(message = "계좌 종류를 선택해주세요.")
    private BankAccountType type;

    private boolean negativeBalanceAllowed;

    public BankAccountRequestDto(String bankName, String accountName, Long balance, BankAccountType type, boolean negativeBalanceAllowed) {
        this.bankName = bankName;
        this.accountName = accountName;
        this.balance = balance;
        this.type = type;
        this.negativeBalanceAllowed = negativeBalanceAllowed;
    }
}
