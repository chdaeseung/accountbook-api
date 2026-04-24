package chdaeseung.accountbook.dashboard.controller;

import chdaeseung.accountbook.dashboard.dto.AssetTrendPointDto;
import chdaeseung.accountbook.dashboard.dto.DashboardResponseDto;
import chdaeseung.accountbook.dashboard.service.DashboardService;
import chdaeseung.accountbook.user.service.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "Dashboard API", description = "대시보드 요약 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/dashboard")
public class DashboardApiController {

    private final DashboardService dashboardService;

    @Operation(summary = "대시보드 조회", description = "월별 수입, 지출, 최근거래, 정기결제, 계좌 요약 정보 조회")
    @ApiResponse(responseCode = "200", description = "대시보드 정보 조회 성공")
    @GetMapping
    public ResponseEntity<DashboardResponseDto> getDashboard(@RequestParam(required = false) Integer year, @RequestParam(required = false) Integer month, @AuthenticationPrincipal CustomUserDetails userDetails) {
        LocalDate today = LocalDate.now();

        int targetYear = year != null ? year : today.getYear();
        int targetMonth = month != null ? month : today.getMonthValue();

        DashboardResponseDto response = dashboardService.getDashboard(userDetails.getUserId(), targetYear, targetMonth);

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "자산 흐름 조회", description = "당월의 일별 자산 흐름 데이터 조회")
    @ApiResponse(responseCode = "200", description = "자산 흐름 조회 성공")
    @GetMapping("/asset-trend")
    public ResponseEntity<List<AssetTrendPointDto>> getAssetTrend(@RequestParam(required = false) Integer year, @RequestParam(required = false) Integer month, @AuthenticationPrincipal CustomUserDetails userDetails) {
        LocalDate today = LocalDate.now();

        int targetYear = year != null ? year : today.getYear();
        int targetMonth = month != null ? month : today.getMonthValue();

        List<AssetTrendPointDto> response = dashboardService.getAssetTrend(userDetails.getUserId(), targetYear, targetMonth);

        return ResponseEntity.ok(response);
    }
}
