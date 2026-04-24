package chdaeseung.accountbook.recurring.controller;

import chdaeseung.accountbook.global.dto.ApiResponseDto;
import chdaeseung.accountbook.recurring.dto.RecurringTransactionCreateDto;
import chdaeseung.accountbook.recurring.dto.RecurringTransactionResponseDto;
import chdaeseung.accountbook.recurring.service.RecurringSchedulerService;
import chdaeseung.accountbook.recurring.service.RecurringTransactionService;
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

@Tag(name = "Recurring API", description = "정기거래 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/recurrings")
public class RecurringTransactionApiController {

    private final RecurringTransactionService recurringTransactionService;
    private final RecurringSchedulerService recurringSchedulerService;

    @Operation(summary = "정기결제 목록 조회", description = "사용자 정기결제 목록을 조회")
    @ApiResponse(responseCode = "200", description = "정기결제 목록 조회 성공")
    @GetMapping
    public ResponseEntity<List<RecurringTransactionResponseDto>> getRecurring(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(recurringTransactionService.getRecurringTransactions(userDetails.getUserId()));
    }

    @Operation(summary = "정기결제 상세 조회", description = "정기결제 ID로 상세 정보 조회")
    @ApiResponse(responseCode = "200", description = "정기결제 상세 조회 성공")
    @ApiResponse(responseCode = "404", description = "정기결제 찾을 수 없음")
    @GetMapping("/{recurringId}")
    public ResponseEntity<RecurringTransactionResponseDto> getRecurring(@PathVariable Long recurringId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(recurringTransactionService.getRecurringTransaction(userDetails.getUserId(), recurringId));
    }

    @Operation(summary = "정기결제 생성", description = "새로운 정기결제 생성")
    @ApiResponse(responseCode = "201", description = "정기결제 생성 성공")
    @ApiResponse(responseCode = "400", description = "요청값 검증 실패")
    @PostMapping
    public ResponseEntity<ApiResponseDto<Long>> createRecurring(@Valid @RequestBody RecurringTransactionCreateDto requestDto, @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long id = recurringTransactionService.createRecurringTransaction(userDetails.getUserId(), requestDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponseDto<>(id, "정기 결제가 생성되었습니다."));
    }

    @Operation(summary = "정기결제 수정", description = "정기결제 수정")
    @ApiResponse(responseCode = "200", description = "정기결제 수정 성공")
    @ApiResponse(responseCode = "400", description = "요청값 검증 실패")
    @PutMapping("/{recurringId}")
    public ResponseEntity<ApiResponseDto<Long>> updateRecurring(@PathVariable Long recurringId, @Valid @RequestBody RecurringTransactionCreateDto requestDto, @AuthenticationPrincipal CustomUserDetails userDetails) {
        recurringTransactionService.updateRecurringTransaction(userDetails.getUserId(), recurringId, requestDto);

        return ResponseEntity.ok(new ApiResponseDto<>(recurringId, "정기 결제가 수정 되었습니다."));
    }

    @Operation(summary = "정기결제 삭제", description = "정기결제 삭제")
    @ApiResponse(responseCode = "204", description = "정기결제 삭제 성공")
    @DeleteMapping("/{recurringId}")
    public ResponseEntity<Void> deleteRecurring(@PathVariable Long recurringId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        recurringTransactionService.deleteRecurringTransaction(userDetails.getUserId(), recurringId);

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "오늘 정기결제 수동 생성", description = "오늘 날짜에 해당하는 정기결제를 수동으로 생성")
    @ApiResponse(responseCode = "200", description = "오늘 날짜 정기결제 생성 성공")
    @PostMapping("/generate-today")
    public ResponseEntity<ApiResponseDto<String>> generateToday() {
        recurringSchedulerService.generateTodayRecurringTransactions();

        return ResponseEntity.ok(new ApiResponseDto<>("OK", "오늘의 정기 결제 생성이 완료되었습니다."));
    }
}
