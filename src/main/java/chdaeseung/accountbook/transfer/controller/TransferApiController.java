package chdaeseung.accountbook.transfer.controller;

import chdaeseung.accountbook.global.dto.ApiResponseDto;
import chdaeseung.accountbook.transfer.dto.TransferCreateRequestDto;
import chdaeseung.accountbook.transfer.dto.TransferCreateResponseDto;
import chdaeseung.accountbook.transfer.dto.TransferUpdateRequestDto;
import chdaeseung.accountbook.transfer.dto.TransferUpdateResponseDto;
import chdaeseung.accountbook.transfer.service.TransferService;
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

@Tag(name = "Transfer API", description = "이체 관리 API")
@RestController
@RequestMapping("/api/transfers")
@RequiredArgsConstructor
public class TransferApiController {

    private final TransferService transferService;

    @Operation(
            summary = "이체 생성",
            description = "출금, 입금 거래를 동시에 생성, 잔액 반영"
    )
    @ApiResponse(responseCode = "201", description = "이체 성공")
    @PostMapping
    public ResponseEntity<ApiResponseDto<String>> createTransfer(@Valid @RequestBody TransferCreateRequestDto requestDto, @AuthenticationPrincipal CustomUserDetails userDetails) {
        String transferGroupKey = transferService.createTransfer(requestDto, userDetails.getUserId());

        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponseDto<>(
                transferGroupKey,
                "이체가 완료되었습니다."
        ));
    }

    @Operation(
            summary = "이체 수정",
            description = "이체를 수정, 잔액 원복 후 재반영"
    )
    @ApiResponse(responseCode = "200", description = "이체 수정 성공")
    @PutMapping("/{transactionId}")
    public ResponseEntity<ApiResponseDto<TransferUpdateResponseDto>> updateTransfer(@PathVariable Long transactionId, @Valid @RequestBody TransferUpdateRequestDto requestDto, @AuthenticationPrincipal CustomUserDetails userDetails) {
        transferService.updateTransfer(transactionId, userDetails.getUserId(), requestDto);

        return ResponseEntity.ok(new ApiResponseDto<>(
                new TransferUpdateResponseDto(transactionId),
                "이체가 수정되었습니다."
        ));
    }
}
