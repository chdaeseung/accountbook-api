package chdaeseung.accountbook.category.dto;

import chdaeseung.accountbook.transaction.entity.TransactionType;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CategoryCreateDto {

    @NotBlank(message = "카테고리 이름을 입력해주세요.")
    private String name;
}
