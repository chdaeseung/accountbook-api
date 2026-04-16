package chdaeseung.accountbook.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DailyCashFlowDto {
    private Integer day;
    private Long amountFlow;
}
