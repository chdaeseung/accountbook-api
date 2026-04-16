package chdaeseung.accountbook.category.dto;

import chdaeseung.accountbook.category.entity.Category;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
public class CategoryResponseDto {
    private final Long id;

    private final String name;

    public static CategoryResponseDto from(Category category) {
        return CategoryResponseDto.builder()
                .id(category.getId())
                .name(category.getName())
                .build();
    }
}
