package chdaeseung.accountbook.transaction.dto;

import chdaeseung.accountbook.transaction.entity.ExpenseType;
import chdaeseung.accountbook.transaction.entity.TransactionType;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
public class TransactionSearchDto {
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    private Long categoryId;

    private TransactionType type;

    private ExpenseType expenseType;

    private Long bankAccountId;

    private String memoKeyword;

    private boolean recurring;

    private int page = 0;

    private int size = 10;
}
