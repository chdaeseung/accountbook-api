package chdaeseung.accountbook.bank.controller;

import chdaeseung.accountbook.bank.dto.BankAccountDetailResponseDto;
import chdaeseung.accountbook.bank.dto.BankAccountRequestDto;
import chdaeseung.accountbook.bank.dto.BankAccountResponseDto;
import chdaeseung.accountbook.bank.dto.BankAccountSelectDto;
import chdaeseung.accountbook.bank.service.BankAccountService;
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
@RequestMapping("/api/bank-accounts")
public class BankAccountApiController {

    private final BankAccountService bankAccountService;

    @GetMapping
    public ResponseEntity<List<BankAccountResponseDto>> getBankAccounts(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(bankAccountService.findAll(userDetails.getUserId()));
    }

    @GetMapping("/{bankAccountId}")
    public ResponseEntity<BankAccountDetailResponseDto> getBankAccount(@PathVariable Long bankAccountId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(bankAccountService.getDetail(userDetails.getUserId(), bankAccountId));
    }

    @PostMapping
    public ResponseEntity<ApiResponseDto<Long>> createBankAccount(@Valid @RequestBody BankAccountRequestDto requestDto, @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long id = bankAccountService.create(userDetails.getUserId(), requestDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponseDto<>(id, "계좌가 생성되었습니다."));
    }

    @PutMapping("/{bankAccountId}")
    public ResponseEntity<ApiResponseDto<Long>> updateBankAccount(@PathVariable Long bankAccountId, @Valid @RequestBody BankAccountRequestDto requestDto, @AuthenticationPrincipal CustomUserDetails userDetails) {
        bankAccountService.update(userDetails.getUserId(), bankAccountId, requestDto);

        return ResponseEntity.ok(new ApiResponseDto<>(bankAccountId, "계좌가 수정되었습니다."));
    }

    @DeleteMapping("/{bankAccountId}")
    public ResponseEntity<Void> deleteBankAccount(@PathVariable Long bankAccountId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        bankAccountService.delete(userDetails.getUserId(), bankAccountId);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/options")
    public ResponseEntity<List<BankAccountSelectDto>> getOptions(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(bankAccountService.getBankAccountsForSelect(userDetails.getUserId()));
    }

    @GetMapping("/transfer-targets")
    public ResponseEntity<List<BankAccountSelectDto>> getTransferTargets(@RequestParam Long fromAccountId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(bankAccountService.getTransferTargetOptions(userDetails.getUserId(), fromAccountId));
    }
}
