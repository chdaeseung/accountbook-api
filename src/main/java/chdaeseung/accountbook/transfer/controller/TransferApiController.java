package chdaeseung.accountbook.transfer.controller;

import chdaeseung.accountbook.transfer.dto.TransferCreateRequestDto;
import chdaeseung.accountbook.transfer.dto.TransferCreateResponseDto;
import chdaeseung.accountbook.transfer.dto.TransferUpdateRequestDto;
import chdaeseung.accountbook.transfer.dto.TransferUpdateResponseDto;
import chdaeseung.accountbook.transfer.service.TransferService;
import chdaeseung.accountbook.user.service.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/transfers")
@RequiredArgsConstructor
public class TransferApiController {

    private final TransferService transferService;

    @PostMapping
    public ResponseEntity<TransferCreateResponseDto> createTransfer(@Valid @RequestBody TransferCreateRequestDto requestDto, @AuthenticationPrincipal CustomUserDetails userDetails) {
        String transferGroupKey = transferService.createTransfer(requestDto, userDetails.getUserId());

        TransferCreateResponseDto response = new TransferCreateResponseDto(transferGroupKey, "이체가 완료되었습니다.");

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{transactionId}")
    public ResponseEntity<TransferUpdateResponseDto> updateTransfer(@PathVariable Long transactionId, @Valid @RequestBody TransferUpdateRequestDto requestDto, @AuthenticationPrincipal CustomUserDetails userDetails) {
        transferService.updateTransfer(transactionId, userDetails.getUserId(), requestDto);

        TransferUpdateResponseDto response = new TransferUpdateResponseDto(transactionId, "이체가 수정되었습니다.");

        return ResponseEntity.ok(response);
    }
}
