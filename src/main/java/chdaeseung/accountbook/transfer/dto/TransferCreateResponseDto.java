package chdaeseung.accountbook.transfer.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TransferCreateResponseDto {
    private String transferGroupKey;
    private String message;
}
