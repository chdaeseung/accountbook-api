package chdaeseung.accountbook.transfer.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
public class TransferRequestDto {
    @NotNull(message = "금액을 입력해주세요.")
    private Long amount;

    private String memo;

    @NotNull(message = "이체 날짜를 입력해주세요.")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    @NotNull(message = "출금 계좌를 선택해주세요.")
    private Long fromAccountId;

    @NotNull(message = "입금 계좌를 선택해주세요.")
    private Long toAccountId;
}
