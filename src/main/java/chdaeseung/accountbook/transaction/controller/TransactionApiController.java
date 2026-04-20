package chdaeseung.accountbook.transaction.controller;

import chdaeseung.accountbook.transaction.dto.*;
import chdaeseung.accountbook.transaction.service.TransactionService;
import chdaeseung.accountbook.user.service.CustomUserDetails;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/transactions")
public class TransactionApiController {

    private final TransactionService transactionService;

    @GetMapping("/{transactionId}")
    public ResponseEntity<TransactionDetailResponseDto> getTransactionDetail(@PathVariable Long transactionId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getUserId();

        TransactionDetailResponseDto response = transactionService.getTransactionDetail(userId, transactionId);

        return ResponseEntity.ok(response);
    }

    @PostMapping
    public ResponseEntity<TransactionCreateResponseDto> createTransaction(@Valid @RequestBody TransactionCreateRequestDto requestDto, @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getUserId();
        Long transactionId = transactionService.createTransaction(requestDto, userId);

        TransactionCreateResponseDto response = new TransactionCreateResponseDto(transactionId);

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/{transactionId}")
    public ResponseEntity<TransactionUpdateResponseDto> updateTransaction(@PathVariable Long transactionId, @Valid @RequestBody TransactionUpdateRequestDto requestDto, @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getUserId();

        transactionService.updateTransaction(transactionId, userId, requestDto);

        TransactionUpdateResponseDto response = new TransactionUpdateResponseDto(transactionId, "거래가 수정되었습니다.");

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{transactionId}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long transactionId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getUserId();

        transactionService.deleteTransaction(transactionId, userId);

        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<Page<TransactionListResponseDto>> getTransactions(@ModelAttribute TransactionSearchDto searchDto, @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getUserId();

        if(searchDto.getPage() < 0) {
            searchDto.setPage(0);
        }

        if(searchDto.getSize() <= 0) {
            searchDto.setSize(10);
        }

        Page<TransactionListResponseDto> response = transactionService.getTransactions(userId, searchDto);

        return ResponseEntity.ok(response);
    }

}
