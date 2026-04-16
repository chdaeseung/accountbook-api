package chdaeseung.accountbook.bank.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class MonthlyExpenseRawDto {
    private Integer year;

    private Integer month;

    private Long amount;
}
