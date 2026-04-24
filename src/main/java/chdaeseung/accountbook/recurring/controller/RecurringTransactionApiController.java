package chdaeseung.accountbook.recurring.controller;

import chdaeseung.accountbook.global.dto.ApiResponseDto;
import chdaeseung.accountbook.recurring.dto.RecurringTransactionCreateDto;
import chdaeseung.accountbook.recurring.dto.RecurringTransactionResponseDto;
import chdaeseung.accountbook.recurring.service.RecurringSchedulerService;
import chdaeseung.accountbook.recurring.service.RecurringTransactionService;
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
@RequestMapping("/api/recurrings")
public class RecurringTransactionApiController {

    private final RecurringTransactionService recurringTransactionService;
    private final RecurringSchedulerService recurringSchedulerService;

    @GetMapping
    public ResponseEntity<List<RecurringTransactionResponseDto>> getRecurring(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(recurringTransactionService.getRecurringTransactions(userDetails.getUserId()));
    }

    @GetMapping("/{recurringId}")
    public ResponseEntity<RecurringTransactionResponseDto> getRecurring(@PathVariable Long recurringId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(recurringTransactionService.getRecurringTransaction(userDetails.getUserId(), recurringId));
    }

    @PostMapping
    public ResponseEntity<ApiResponseDto<Long>> createRecurring(@Valid @RequestBody RecurringTransactionCreateDto requestDto, @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long id = recurringTransactionService.createRecurringTransaction(userDetails.getUserId(), requestDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponseDto<>(id, "정기 결제가 생성되었습니다."));
    }

    @PutMapping("/{recurringId}")
    public ResponseEntity<ApiResponseDto<Long>> updateRecurring(@PathVariable Long recurringId, @Valid @RequestBody RecurringTransactionCreateDto requestDto, @AuthenticationPrincipal CustomUserDetails userDetails) {
        recurringTransactionService.updateRecurringTransaction(userDetails.getUserId(), recurringId, requestDto);

        return ResponseEntity.ok(new ApiResponseDto<>(recurringId, "정기 결제가 수정 되었습니다."));
    }

    @DeleteMapping("/{recurringId}")
    public ResponseEntity<Void> deleteRecurring(@PathVariable Long recurringId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        recurringTransactionService.deleteRecurringTransaction(userDetails.getUserId(), recurringId);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/generate-today")
    public ResponseEntity<ApiResponseDto<String>> generateToday() {
        recurringSchedulerService.generateTodayRecurringTransactions();

        return ResponseEntity.ok(new ApiResponseDto<>("OK", "오늘의 정기 결제 생성이 완료되었습니다."));
    }
}
