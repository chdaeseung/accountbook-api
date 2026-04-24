package chdaeseung.accountbook.recurring.dto;

import chdaeseung.accountbook.transaction.entity.TransactionType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
public class RecurringTransactionCreateDto {
    private String memo;

    @NotNull(message = "금액을 입력해주세요.")
    @Min(value = 1, message = "금액은 1원 이상이어야 합니다.")
    private Long amount;

    @NotNull(message = "결제일을 입력해주세요.")
    private Integer dayOfMonth;

    @NotNull(message = "계좌를 선택해주세요.")
    private Long bankAccountId;
}
