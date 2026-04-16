package chdaeseung.accountbook.bank.controller;

import chdaeseung.accountbook.bank.dto.AssetDashboardResponseDto;
import chdaeseung.accountbook.bank.dto.BankAccountDetailResponseDto;
import chdaeseung.accountbook.bank.dto.BankAccountRequestDto;
import chdaeseung.accountbook.bank.dto.BankAccountResponseDto;
import chdaeseung.accountbook.bank.entity.BankAccountType;
import chdaeseung.accountbook.bank.service.BankAccountService;
import chdaeseung.accountbook.transfer.dto.TransferRequestDto;
import chdaeseung.accountbook.user.dto.LoginUserDto;
import chdaeseung.accountbook.user.service.CustomUserDetails;
import chdaeseung.accountbook.user.service.UserService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

@Controller
@RequiredArgsConstructor
@RequestMapping("/bank-account")
public class BankAccountController {

    private final BankAccountService bankAccountService;
    private final UserService userService;

    @GetMapping
    public String list(@RequestParam(required = false) Integer year, @RequestParam(required = false) Integer month, @AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        Long userId = userDetails.getUserId();

        AssetDashboardResponseDto response = bankAccountService.getAssetDashboard(userId, year, month);

        model.addAttribute("totalAsset", response.getTotalAsset());
        model.addAttribute("monthAssetIncrease", response.getMonthAssetIncrease());
        model.addAttribute("monthAssetDecrease", response.getMonthAssetDecrease());

        model.addAttribute("negativeAllowedCount", response.getNegativeAllowedCount());
        model.addAttribute("largestAccountName", response.getLargestAccountName());

        model.addAttribute("bankAccounts", response.getBankAccounts());
        model.addAttribute("assetTrend", response.getAssetTrend());
        model.addAttribute("assetShare", response.getAssetShare());
        return "/bank-account/list";
    }

    @GetMapping("/create")
    public String create(Model model) {
        model.addAttribute("bankAccountRequestDto", new BankAccountRequestDto());
        model.addAttribute("types", BankAccountType.values());

        return "/bank-account/create";
    }

    @PostMapping("/create")
    public String create(@Valid @ModelAttribute BankAccountRequestDto bankAccountRequestDto, BindingResult bindingResult, @AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("types", BankAccountType.values());
            return "bank-account/create";
        }

        Long userId = userDetails.getUserId();

        bankAccountService.create(userId, bankAccountRequestDto);

        return "redirect:/bank-account";
    }

    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        Long userId = userDetails.getUserId();

        BankAccountDetailResponseDto bankAccount = bankAccountService.getDetail(userId, id);

        TransferRequestDto transferRequestDto = new TransferRequestDto();
        transferRequestDto.setFromAccountId(id);
        transferRequestDto.setDate(LocalDate.now());

        model.addAttribute("bankAccount", bankAccount);
        model.addAttribute("transferRequestDto", transferRequestDto);
        model.addAttribute("transferAccounts", bankAccountService.getTransferTargetOptions(userId, id));

        return "/bank-account/detail";
    }

    @GetMapping("/{id}/update")
    public String update(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        Long userId = userDetails.getUserId();

        BankAccountResponseDto bankAccount = bankAccountService.findById(userId, id);

        BankAccountRequestDto requestDto = new BankAccountRequestDto(
                bankAccount.getBankName(),
                bankAccount.getAccountName(),
                bankAccount.getBalance(),
                bankAccount.getType(),
                bankAccount.isNegativeBalanceAllowed()
        );

        model.addAttribute("bankAccountId", id);
        model.addAttribute("bankAccountRequestDto", requestDto);
        model.addAttribute("types", BankAccountType.values());

        return "/bank-account/update";
    }

    @PostMapping("/{id}/update")
    public String update(@PathVariable Long id, @Valid @ModelAttribute BankAccountRequestDto requestDto, BindingResult bindingResult, @AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        if(bindingResult.hasErrors()) {
            model.addAttribute("bankAccountId", id);
            model.addAttribute("types", BankAccountType.values());
            return "/bank-account/update";
        }

        Long userId = userDetails.getUserId();

        bankAccountService.update(userId, id, requestDto);

        return "redirect:/bank-account/" + id;
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getUserId();

        bankAccountService.delete(userId, id);

        return "redirect:/bank-account";
    }
}
