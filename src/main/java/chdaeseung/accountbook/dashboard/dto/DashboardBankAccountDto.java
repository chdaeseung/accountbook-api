package chdaeseung.accountbook.dashboard.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class DashboardBankAccountDto {
    private Long id;

    private String bankName;

    private String accountName;

    private Long balance;

    private String typeLabel;
}
