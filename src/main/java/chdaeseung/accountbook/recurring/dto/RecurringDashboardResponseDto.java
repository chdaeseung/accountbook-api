package chdaeseung.accountbook.recurring.dto;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class RecurringDashboardResponseDto {
    private Long monthlyTotalAmount;

    private List<RecurringTransactionListResponseDto> recurringTransactions;
}
