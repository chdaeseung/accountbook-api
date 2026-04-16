package chdaeseung.accountbook.recurring.dto;

import chdaeseung.accountbook.transaction.entity.TransactionType;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
public class RecurringTransactionCreateDto {
    private String memo;

    private Long amount;

    private Integer dayOfMonth;

    private Long bankAccountId;
}
