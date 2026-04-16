package chdaeseung.accountbook.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class DashboardRecurringTransactionDto {
    private Long id;

    private String memo;

    private Long amount;

    private Integer dayOfMonth;

    private String bankAccountName;
}
