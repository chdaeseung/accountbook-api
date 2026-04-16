package chdaeseung.accountbook.category.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CategoryExpenseDto {
    private String categoryName;

    private Long amount;
}
