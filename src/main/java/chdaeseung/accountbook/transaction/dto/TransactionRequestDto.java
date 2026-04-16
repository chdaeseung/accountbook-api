package chdaeseung.accountbook.transaction.dto;

import chdaeseung.accountbook.transaction.entity.ExpenseType;
import chdaeseung.accountbook.transaction.entity.TransactionType;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
public class TransactionRequestDto {
    @NotNull(message = "날짜를 입력해주세요.")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;

    @NotNull(message = "카테고리를 선택해주세요.")
    private Long categoryId;

    @NotNull(message = "거래 유형을 선택해주세요.")
    private TransactionType type;

    @NotNull(message = "금액을 입력해주세요.")
    @Min(value = 1, message = "금액은 1원 이상 입력해주세요.")
    private Long amount;

    private String memo;

    @NotNull(message = "계좌를 선택해주세요.")
    private Long bankAccountId;
}
