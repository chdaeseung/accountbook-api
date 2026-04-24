package chdaeseung.accountbook.category.controller;

import chdaeseung.accountbook.category.dto.CategoryCreateDto;
import chdaeseung.accountbook.category.dto.CategoryResponseDto;
import chdaeseung.accountbook.category.service.CategoryService;
import chdaeseung.accountbook.global.dto.ApiResponseDto;
import chdaeseung.accountbook.user.service.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/categories")
public class CategoryApiController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<CategoryResponseDto>> getCategories(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(categoryService.getCategories(userDetails.getUserId()));
    }

    @PostMapping
    public ResponseEntity<ApiResponseDto<Long>> createCategory(@Valid @RequestBody CategoryCreateDto dto, @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long categoryId = categoryService.create(userDetails.getUserId(), dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponseDto<>(categoryId, "카테고리가 생성되었습니다."));
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long categoryId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        categoryService.delete(userDetails.getUserId(), categoryId);

        return ResponseEntity.noContent().build();
    }
}
