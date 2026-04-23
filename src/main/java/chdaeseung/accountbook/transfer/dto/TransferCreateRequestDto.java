package chdaeseung.accountbook.transfer.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class TransferCreateRequestDto {
    @NotNull(message = "이체 날짜를 입력해주세요.")
    private LocalDate date;

    @NotNull(message = "출금 계좌를 선택해주세요.")
    private Long fromBankAccountId;

    @NotNull(message = "입금 계좌를 선택해주세요.")
    private Long toBankAccountId;

    @NotNull(message = "금액을 입력해주세요.")
    @Min(value = 1, message = "금액은 1원 이상이어야 합니다.")
    private Long amount;

    private String memo;
}
