package chdaeseung.accountbook.transaction.controller;

import chdaeseung.accountbook.bank.service.BankAccountService;
import chdaeseung.accountbook.category.service.CategoryService;
import chdaeseung.accountbook.transaction.dto.*;
import chdaeseung.accountbook.transaction.service.TransactionService;
import chdaeseung.accountbook.user.dto.LoginUserDto;
import chdaeseung.accountbook.user.service.CustomUserDetails;
import chdaeseung.accountbook.user.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@RequestMapping("/transactions")
public class TransactionController {

    private final TransactionService transactionService;
    private final CategoryService categoryService;
    private final BankAccountService bankAccountService;
    private final UserService userService;

    @GetMapping
    public String getTransactionsList(@ModelAttribute TransactionSearchDto searchDto, @AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        Long userId = userDetails.getUserId();

        if(searchDto.getPage() < 0) {
           searchDto.setPage(0);
        }

        if(searchDto.getSize() <= 0) {
            searchDto.setSize(10);
        }

        Page<TransactionListResponseDto> transactionPage = transactionService.getTransactions(userId, searchDto);

        if(transactionPage.getTotalPages() > 0 && searchDto.getPage() >= transactionPage.getTotalPages()) {
            searchDto.setPage(transactionPage.getTotalPages() - 1);
            transactionPage = transactionService.getTransactions(userId, searchDto);
        }

        model.addAttribute("transactionPage", transactionPage);
        model.addAttribute("searchDto", searchDto);
        model.addAttribute("categories", categoryService.getCategories(userId));
        model.addAttribute("bankAccounts", bankAccountService.getUsedOptions(userId));

        return "/transactions/list";
    }

    @PostMapping
    public String createTransaction(@Valid @ModelAttribute TransactionRequestDto transactionRequestDto, BindingResult bindingResult, @AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        Long userId = userDetails.getUserId();

        if(bindingResult.hasErrors()) {
            model.addAttribute("categories", categoryService.getCategories(userId));
            model.addAttribute("bankAccounts", bankAccountService.getUsedOptions(userId));
            return "transactions/create";
        }

        transactionService.createTransaction(transactionRequestDto, userId);

        return "redirect:/transactions";
    }

    @GetMapping("/create")
    public String create(Model model, @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getUserId();

        model.addAttribute("transactionRequestDto", new TransactionRequestDto());
        model.addAttribute("categories", categoryService.getCategories(userId));
        model.addAttribute("bankAccounts", bankAccountService.getUsedOptions(userId));


        return "/transactions/create";
    }

    @GetMapping("/{transactionId}")
    public String getTransactionDetail(@PathVariable Long transactionId, @AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        Long userId = userDetails.getUserId();

        TransactionDetailResponseDto transaction = transactionService.getTransactionDetail(userId, transactionId);

        model.addAttribute("transaction", transaction);

        return "/transactions/detail";
    }

    @GetMapping("/{id}/update")
    public String updateTransaction(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        Long userId = userDetails.getUserId();

        TransactionRequestDto transaction = transactionService.transactionUpdate(id, userId);

        model.addAttribute("transactionId", id);
        model.addAttribute("transactionRequestDto", transaction);
        model.addAttribute("categories", categoryService.getCategories(userId));
        model.addAttribute("bankAccounts", bankAccountService.getUsedOptions(userId));

        return "/transactions/update";
    }

    @PostMapping("/{id}/update")
    public String updateTransaction(@PathVariable Long id, @Valid @ModelAttribute TransactionRequestDto transactionRequestDto, BindingResult bindingResult, @AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        Long userId = userDetails.getUserId();

        if(bindingResult.hasErrors()) {
            model.addAttribute("transactionId", id);
            model.addAttribute("categories", categoryService.getCategories(userId));
            model.addAttribute("bankAccounts", bankAccountService.getUsedOptions(userId));
            return "transactions/update";
        }

        transactionService.updateTransaction(id, userId, transactionRequestDto);

        return "redirect:/transactions/" + id;
    }

    @PostMapping("/{id}/delete")
    public String deleteTransaction(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getUserId();

        transactionService.deleteTransaction(id, userId);

        return "redirect:/dashboard";
    }
}
