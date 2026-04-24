package chdaeseung.accountbook.transaction.controller;

import chdaeseung.accountbook.global.dto.ApiResponseDto;
import chdaeseung.accountbook.transaction.dto.*;
import chdaeseung.accountbook.transaction.service.TransactionService;
import chdaeseung.accountbook.user.service.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Transaction API", description = "거래 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/transactions")
public class TransactionApiController {

    private final TransactionService transactionService;

    @Operation(
            summary = "거래 상세 조회",
            description = "특정 거래의 상세 정보 조회"
    )
    @ApiResponse(responseCode = "201", description = "조회 성공")
    @ApiResponse(responseCode = "404", description = "거래 없음")
    @GetMapping("/{transactionId}")
    public ResponseEntity<TransactionDetailResponseDto> getTransactionDetail(@PathVariable Long transactionId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        TransactionDetailResponseDto response = transactionService.getTransactionDetail(userDetails.getUserId(), transactionId);

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "거래 생성",
            description = "새로운 거래 생성, 계좌 잔액 반영"
    )
    @ApiResponse(responseCode = "201", description = "거래 생성")
    @ApiResponse(responseCode = "400", description = "요청값 검증 실패")
    @PostMapping
    public ResponseEntity<ApiResponseDto<TransactionCreateResponseDto>> createTransaction(@Valid @RequestBody TransactionCreateRequestDto requestDto, @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long transactionId = transactionService.createTransaction(requestDto, userDetails.getUserId());

        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponseDto<>(
                new TransactionCreateResponseDto(transactionId), "거래가 생성되었습니다."
        ));
    }

    @Operation(
            summary = "거래 수정",
            description = "거래 정보 수정, 잔액 재반영"
    )
    @ApiResponse(responseCode = "201", description = "수정 성공")
    @ApiResponse(responseCode = "400", description = "잔액 부족 또는 요청 오류")
    @PutMapping("/{transactionId}")
    public ResponseEntity<ApiResponseDto<TransactionUpdateResponseDto>> updateTransaction(@PathVariable Long transactionId, @Valid @RequestBody TransactionUpdateRequestDto requestDto, @AuthenticationPrincipal CustomUserDetails userDetails) {
        transactionService.updateTransaction(transactionId, userDetails.getUserId(), requestDto);

        return ResponseEntity.ok(new ApiResponseDto<>(
                new TransactionUpdateResponseDto(transactionId),
                "거래가 수정되었습니다."
        ));
    }

    @Operation(
            summary = "거래 삭제",
            description = "거래 삭제, 잔액 원복"
    )
    @ApiResponse(responseCode = "204", description = "삭제 성공")
    @DeleteMapping("/{transactionId}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long transactionId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        transactionService.deleteTransaction(transactionId, userDetails.getUserId());

        return ResponseEntity.noContent().build();
    }

    @Operation(
            summary = "거래 목록 조회",
            description = "검색 조건에 따라 거래 목록 조회"
    )
    @ApiResponse(responseCode = "200", description = "조회 성공")
    @GetMapping
    public ResponseEntity<Page<TransactionListResponseDto>> getTransactions(@ModelAttribute TransactionSearchDto searchDto, @AuthenticationPrincipal CustomUserDetails userDetails) {
        if(searchDto.getPage() < 0) {
            searchDto.setPage(0);
        }

        if(searchDto.getSize() <= 0) {
            searchDto.setSize(10);
        }

        Page<TransactionListResponseDto> response = transactionService.getTransactions(userDetails.getUserId(), searchDto);

        return ResponseEntity.ok(response);
    }

    @Operation(
            summary = "거래 부분 수정",
            description = "거래 일부 필드만 수정"
    )
    @ApiResponse(responseCode = "200", description = "일부 수정 성공")
    @PatchMapping("/{transactionId}")
    public ResponseEntity<ApiResponseDto<TransactionUpdateResponseDto>> patchTransaction(@PathVariable Long transactionId, @Valid @RequestBody TransactionPatchRequestDto dto, @AuthenticationPrincipal CustomUserDetails userDetails) {
        transactionService.patchTransaction(transactionId, userDetails.getUserId(), dto);

        return ResponseEntity.ok(new ApiResponseDto<>(
                new TransactionUpdateResponseDto(transactionId),
                "거래가 부분 수정되었습니다."
        ));
    }

}
