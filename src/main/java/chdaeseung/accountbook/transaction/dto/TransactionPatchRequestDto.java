package chdaeseung.accountbook.transaction.dto;

import chdaeseung.accountbook.transaction.entity.ExpenseType;
import chdaeseung.accountbook.transaction.entity.TransactionType;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class TransactionPatchRequestDto {
    private LocalDate date;
    private Long categoryId;
    private TransactionType type;
    private ExpenseType expenseType;
    @Min(value = 1, message = "금액은 1원 이상 입력해주세요.")
    private Long amount;
    private String memo;
    private Long bankAccountId;

    public boolean hasNoChanges() {
        return date == null &&
                categoryId == null &&
                type == null &&
                amount == null &&
                memo == null &&
                bankAccountId == null;
    }
}
