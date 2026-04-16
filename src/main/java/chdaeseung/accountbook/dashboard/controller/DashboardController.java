package chdaeseung.accountbook.dashboard.controller;

import chdaeseung.accountbook.dashboard.dto.DashboardResponseDto;
import chdaeseung.accountbook.dashboard.service.DashboardService;
import chdaeseung.accountbook.user.dto.LoginUserDto;
import chdaeseung.accountbook.user.service.CustomUserDetails;
import chdaeseung.accountbook.user.service.UserService;
import chdaeseung.accountbook.weather.repository.WeatherRepository;
import chdaeseung.accountbook.weather.service.WeatherQueryService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDate;
import java.time.YearMonth;

@Controller
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;
    private final UserService userService;

    @GetMapping("/dashboard")
    public String dashboard(@RequestParam(required = false) Integer year,
                            @RequestParam(required = false) Integer month,
                            @AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        Long userId = userDetails.getUserId();

        LocalDate today = LocalDate.now();

        int targetYear = year != null ? year : today.getYear();
        int targetMonth = month != null ? month : today.getMonthValue();

        DashboardResponseDto dashboard = dashboardService.getDashboard(userId, targetYear, targetMonth);

        YearMonth curYearMonth = YearMonth.from(today);
        YearMonth targetYearMonth = YearMonth.of(targetYear, targetMonth);

        YearMonth prevYearMonth = targetYearMonth.minusMonths(1);
        YearMonth nextYearMonth = targetYearMonth.plusMonths(1);

        boolean isCurMonth = targetYearMonth.equals(curYearMonth);
        int displayDay = isCurMonth ? today.getDayOfMonth() : 0;
        boolean existNextMonth = targetYearMonth.isBefore(curYearMonth);

        model.addAttribute("dashboard", dashboard);
        model.addAttribute("prevYear", prevYearMonth.getYear());
        model.addAttribute("prevMonth", prevYearMonth.getMonthValue());
        model.addAttribute("nextYear", nextYearMonth.getYear());
        model.addAttribute("nextMonth", nextYearMonth.getMonthValue());
        model.addAttribute("displayDay", displayDay);
        model.addAttribute("existNextMonth", existNextMonth);
        model.addAttribute("assetTrend", dashboardService.getAssetTrend(userDetails.getUserId(), targetYear, targetMonth));

        return "dashboard/dashboard";
    }
}
