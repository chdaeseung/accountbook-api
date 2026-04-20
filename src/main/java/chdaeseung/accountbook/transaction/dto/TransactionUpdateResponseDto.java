package chdaeseung.accountbook.transaction.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class TransactionUpdateResponseDto {
    private Long transactionId;
    private String message;
}
