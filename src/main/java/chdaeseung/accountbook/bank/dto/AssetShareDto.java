package chdaeseung.accountbook.bank.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AssetShareDto {
    private Long id;

    private String accountName;

    private Long balance;

    private int percent;

    private String color;
}
