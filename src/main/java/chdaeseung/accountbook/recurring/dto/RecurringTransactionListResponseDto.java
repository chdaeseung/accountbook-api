package chdaeseung.accountbook.recurring.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class RecurringTransactionListResponseDto {
    private Long id;

    private String memo;

    private Integer dayOfMonth;

    private Long amount;

    private String bankAccountName;
}
