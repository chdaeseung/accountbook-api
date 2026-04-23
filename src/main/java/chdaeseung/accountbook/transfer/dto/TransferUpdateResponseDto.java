package chdaeseung.accountbook.transfer.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TransferUpdateResponseDto {
    private Long transactionId;
    private String message;
}
