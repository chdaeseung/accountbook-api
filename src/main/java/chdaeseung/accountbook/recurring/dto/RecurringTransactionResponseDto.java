package chdaeseung.accountbook.recurring.dto;

import chdaeseung.accountbook.recurring.entity.RecurringTransaction;
import chdaeseung.accountbook.transaction.entity.TransactionType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@Builder
public class RecurringTransactionResponseDto {
    private Long id;

    private String memo;

    private Long amount;

    private Integer dayOfMonth;

    private Long bankAccountId;

    private String bankAccountName;

    private String bankName;

    public static RecurringTransactionResponseDto from(RecurringTransaction recurringTransaction) {
        return RecurringTransactionResponseDto.builder()
                .id(recurringTransaction.getId())
                .memo(recurringTransaction.getMemo())
                .amount(recurringTransaction.getAmount())
                .dayOfMonth(recurringTransaction.getDayOfMonth())
                .bankAccountId(recurringTransaction.getBankAccount().getId())
                .bankAccountName(recurringTransaction.getBankAccount().getAccountName())
                .bankName(recurringTransaction.getBankAccount().getBankName())
                .build();
    }
}
