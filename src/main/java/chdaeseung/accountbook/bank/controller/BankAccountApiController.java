package chdaeseung.accountbook.bank.controller;

import chdaeseung.accountbook.bank.dto.BankAccountDetailResponseDto;
import chdaeseung.accountbook.bank.dto.BankAccountRequestDto;
import chdaeseung.accountbook.bank.dto.BankAccountResponseDto;
import chdaeseung.accountbook.bank.dto.BankAccountSelectDto;
import chdaeseung.accountbook.bank.service.BankAccountService;
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

@Tag(name = "BankAccount API", description = "계좌 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/bank-accounts")
public class BankAccountApiController {

    private final BankAccountService bankAccountService;

    @Operation(summary = "계좌 목록 조회", description = "사용자 계좌 목록 조회")
    @ApiResponse(responseCode = "200", description = "계좌 목록 조회 성공")
    @GetMapping
    public ResponseEntity<List<BankAccountResponseDto>> getBankAccounts(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(bankAccountService.findAll(userDetails.getUserId()));
    }

    @Operation(summary = "계좌 상세 조회", description = "계좌 ID로 계좌 상세 정보 조회")
    @ApiResponse(responseCode = "200", description = "계좌 상세 정보 조회 성공")
    @ApiResponse(responseCode = "404", description = "계좌를 찾을 수 없음")
    @GetMapping("/{bankAccountId}")
    public ResponseEntity<BankAccountDetailResponseDto> getBankAccount(@PathVariable Long bankAccountId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(bankAccountService.getDetail(userDetails.getUserId(), bankAccountId));
    }

    @Operation(summary = "계좌 생성", description = "새로운 계좌 생성")
    @ApiResponse(responseCode = "201", description = "계좌 생성 성공")
    @ApiResponse(responseCode = "400", description = "요청값 검증 실패")
    @PostMapping
    public ResponseEntity<ApiResponseDto<Long>> createBankAccount(@Valid @RequestBody BankAccountRequestDto requestDto, @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long id = bankAccountService.create(userDetails.getUserId(), requestDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponseDto<>(id, "계좌가 생성되었습니다."));
    }

    @Operation(summary = "계좌 수정", description = "계좌 정보 수정")
    @ApiResponse(responseCode = "200", description = "계좌 정보 수정 성공")
    @ApiResponse(responseCode = "400", description = "요청값 검증 실패")
    @PutMapping("/{bankAccountId}")
    public ResponseEntity<ApiResponseDto<Long>> updateBankAccount(@PathVariable Long bankAccountId, @Valid @RequestBody BankAccountRequestDto requestDto, @AuthenticationPrincipal CustomUserDetails userDetails) {
        bankAccountService.update(userDetails.getUserId(), bankAccountId, requestDto);

        return ResponseEntity.ok(new ApiResponseDto<>(bankAccountId, "계좌가 수정되었습니다."));
    }

    @Operation(summary = "계좌 삭제", description = "계좌 삭제")
    @ApiResponse(responseCode = "204", description = "계좌 삭제 성공")
    @DeleteMapping("/{bankAccountId}")
    public ResponseEntity<Void> deleteBankAccount(@PathVariable Long bankAccountId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        bankAccountService.delete(userDetails.getUserId(), bankAccountId);

        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "계좌 선택 옵션 조회", description = "계좌 생성/수정 화면에서 선택할 계좌 목록 조회")
    @ApiResponse(responseCode = "200", description = "계좌 선택 옵션 조회 성공")
    @GetMapping("/options")
    public ResponseEntity<List<BankAccountSelectDto>> getOptions(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(bankAccountService.getBankAccountsForSelect(userDetails.getUserId()));
    }

    @Operation(summary = "이체 대상 계좌 조회", description = "출금 계좌를 제외한 이체 대상 계좌 목록 조회")
    @ApiResponse(responseCode = "200", description = "이체 대상 계좌 조회 성공")
    @GetMapping("/transfer-targets")
    public ResponseEntity<List<BankAccountSelectDto>> getTransferTargets(@RequestParam Long fromAccountId, @AuthenticationPrincipal CustomUserDetails userDetails) {
        return ResponseEntity.ok(bankAccountService.getTransferTargetOptions(userDetails.getUserId(), fromAccountId));
    }
}
