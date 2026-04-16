package chdaeseung.accountbook.transfer.controller;

import chdaeseung.accountbook.bank.service.BankAccountService;
import chdaeseung.accountbook.transfer.dto.TransferRequestDto;
import chdaeseung.accountbook.transfer.service.TransferService;
import chdaeseung.accountbook.user.service.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequiredArgsConstructor
@RequestMapping("/transfers")
public class TransferController {

    private final TransferService transferService;
    private final BankAccountService bankAccountService;

    @GetMapping("/create")
    public String createTransfer(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        Long userId = userDetails.getUserId();

        model.addAttribute("transferRequestDto", new TransferRequestDto());
        model.addAttribute("bankAccounts", bankAccountService.getUsedOptions(userId));

        return "transfers/create";
    }

    @PostMapping("/create")
    public String createTransfer(@AuthenticationPrincipal CustomUserDetails userDetails, @ModelAttribute TransferRequestDto transferRequestDto) {
        Long userId = userDetails.getUserId();

        transferService.createTransfer(userId, transferRequestDto);
        return "redirect:/dashboard";
    }
}
