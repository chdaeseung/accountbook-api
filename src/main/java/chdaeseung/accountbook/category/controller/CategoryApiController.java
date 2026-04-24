package chdaeseung.accountbook.category.controller;

import chdaeseung.accountbook.category.dto.CategoryCreateDto;
import chdaeseung.accountbook.category.dto.CategoryResponseDto;
import chdaeseung.accountbook.category.service.CategoryService;
import chdaeseung.accountbook.global.dto.ApiResponseDto;
import chdaeseung.accountbook.user.service.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Category API", description = "카테고리 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/categories")
public class CategoryApiController {

    private final CategoryService categoryService;

    @Operation(summary = "카테고리 목록 조회", description = "사용자가 사용 중인 카테고리 목록 조회")
    @ApiResponse(responseCode = "200", description = "카테고리 목록 조회 성공")
    @GetMapping
    public ResponseEntity<List<CategoryResponseDto>> getCategories(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(categoryService.getCategories(userDetails.getUserId()));
    }

    @Operation(summary = "카테고리 생성", description = "새로운 카테고리 생성")
    @ApiResponse(responseCode = "201", description = "카테고리 생성 성공")
    @ApiResponse(responseCode = "400", description = "요청값 검증 실패 또는 중복된 카테고리")
    @PostMapping
    public ResponseEntity<ApiResponseDto<Long>> createCategory(@Valid @RequestBody CategoryCreateDto dto, @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long categoryId = categoryService.create(userDetails.getUserId(), dto);

        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponseDto<>(categoryId, "카테고리가 생성되었습니다."));
    }

    @Operation(summary = "카테고리 삭제", description = "카테고리 삭제, 사용 중 또는 이체 카테고리는 삭제할 수 없음")
    @ApiResponse(responseCode = "204", description = "카테고리 삭제 성공")
    @ApiResponse(responseCode = "400", description = "삭제할 수 없는 카테고리")
    @DeleteMapping("/{categoryId}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long categoryId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        categoryService.delete(userDetails.getUserId(), categoryId);

        return ResponseEntity.noContent().build();
    }
}
