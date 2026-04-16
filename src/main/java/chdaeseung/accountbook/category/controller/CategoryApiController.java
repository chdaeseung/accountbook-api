package chdaeseung.accountbook.category.controller;

import chdaeseung.accountbook.category.dto.CategoryCreateDto;
import chdaeseung.accountbook.category.dto.CategoryResponseDto;
import chdaeseung.accountbook.category.service.CategoryService;
import chdaeseung.accountbook.global.exception.CustomException;
import chdaeseung.accountbook.global.exception.ErrorCode;
import chdaeseung.accountbook.user.dto.LoginUserDto;
import chdaeseung.accountbook.user.service.CustomUserDetails;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/categories/api")
public class CategoryApiController {

    private final CategoryService categoryService;

//    @PostMapping
//    public CategoryResponseDto createCategory(@RequestBody CategoryCreateDto createDto, @AuthenticationPrincipal CustomUserDetails userDetails) {
//
//        return categoryService.createCategory(userDetails.getUserId(), createDto);
//    }

    @GetMapping
    public List<CategoryResponseDto> getCategories(@AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getUserId();

        return categoryService.getCategories(userId);
    }

    @PostMapping
    public ResponseEntity<?> createCategory(@RequestBody CategoryCreateDto dto, @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getUserId();

        categoryService.create(userId, dto);

        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCategory(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getUserId();

        categoryService.delete(userId, id);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/{id}/delete-check")
    public ResponseEntity<?> checkDelete(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getUserId();

        categoryService.checkDelete(userId, id);

        return ResponseEntity.ok().build();
    }
}
