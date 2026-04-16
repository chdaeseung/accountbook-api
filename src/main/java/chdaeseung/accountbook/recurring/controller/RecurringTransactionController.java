package chdaeseung.accountbook.recurring.controller;

import chdaeseung.accountbook.bank.entity.BankAccount;
import chdaeseung.accountbook.bank.service.BankAccountService;
import chdaeseung.accountbook.category.service.CategoryService;
import chdaeseung.accountbook.global.exception.CustomException;
import chdaeseung.accountbook.recurring.dto.RecurringDashboardResponseDto;
import chdaeseung.accountbook.recurring.dto.RecurringTransactionCreateDto;
import chdaeseung.accountbook.recurring.dto.RecurringTransactionResponseDto;
import chdaeseung.accountbook.recurring.entity.RecurringTransaction;
import chdaeseung.accountbook.recurring.service.RecurringSchedulerService;
import chdaeseung.accountbook.recurring.service.RecurringTransactionService;
import chdaeseung.accountbook.user.dto.LoginUserDto;
import chdaeseung.accountbook.user.service.CustomUserDetails;
import chdaeseung.accountbook.user.service.UserService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/recurring")
public class RecurringTransactionController {

    private final RecurringTransactionService recurringTransactionService;
    private final CategoryService categoryService;
    private final RecurringSchedulerService recurringSchedulerService;
    private final BankAccountService bankAccountService;

    @GetMapping
    public String list(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        Long userId = userDetails.getUserId();

        RecurringDashboardResponseDto response = recurringTransactionService.getRecurringDashboard(userId);

        model.addAttribute("monthlyTotalAmount", response.getMonthlyTotalAmount());
        model.addAttribute("recurringTransactions", response.getRecurringTransactions());

        return "/recurring/list";
    }

    @GetMapping("/create")
    public String create(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        Long userId = userDetails.getUserId();

        model.addAttribute("recurringTransactionCreateDto", new RecurringTransactionCreateDto());
        model.addAttribute("bankAccounts", bankAccountService.getBankAccountsForSelect(userId));

        return "/recurring/create";
    }

    @PostMapping
    public String create(@ModelAttribute RecurringTransactionCreateDto recurringTransactionCreateDto, @AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        Long userId = userDetails.getUserId();
        try {
            recurringTransactionService.createRecurringTransaction(userId, recurringTransactionCreateDto);
            return "redirect:/recurring";
        } catch (CustomException e) {
            model.addAttribute("recurringTransactionCreateDto", recurringTransactionCreateDto);
            model.addAttribute("bankAccounts", bankAccountService.getBankAccountsForSelect(userId));
            model.addAttribute("errorMessage", e.getMessage());
            return "/recurring/create";
        }
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        Long userId = userDetails.getUserId();

        RecurringTransactionResponseDto recurringTransaction = recurringTransactionService.getRecurringTransaction(userId, id);

        model.addAttribute("recurringTransaction", recurringTransaction);

        return "/recurring/detail";
    }

    @GetMapping("/{id}/update")
    public String update(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        Long userId = userDetails.getUserId();

        RecurringTransactionCreateDto dto = recurringTransactionService.getRecurringTransactionUpdate(userId, id);

        model.addAttribute("recurringTransactionCreateDto", dto);
        model.addAttribute("bankAccounts", bankAccountService.getBankAccountsForSelect(userId));
        model.addAttribute("recurringId", id);

        return "/recurring/update";
    }

    @PostMapping("/{id}/update")
    public String update(@PathVariable Long id, @ModelAttribute RecurringTransactionCreateDto createDto, @AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        Long userId = userDetails.getUserId();

        try {
            recurringTransactionService.updateRecurringTransaction(userId, id, createDto);
            return "redirect:/recurring/" + id;
        } catch (CustomException e) {
            model.addAttribute("recurringTransactionCreateDto", createDto);
            model.addAttribute("bankAccounts", bankAccountService.getBankAccountsForSelect(userId));
            model.addAttribute("recurringId", id);
            model.addAttribute("errorMessage", e.getMessage());
            return "/recurring/update";
        }
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getUserId();

        recurringTransactionService.deleteRecurringTransaction(userId, id);

        return "redirect:/recurring";
    }

    @PostMapping("/generate")
    public String generate() {
        recurringSchedulerService.generateTodayRecurringTransactions();
        return "redirect:/recurring";
    }

}
