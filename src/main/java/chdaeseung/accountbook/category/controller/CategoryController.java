package chdaeseung.accountbook.category.controller;

import chdaeseung.accountbook.category.dto.CategoryCreateDto;
import chdaeseung.accountbook.category.dto.CategoryResponseDto;
import chdaeseung.accountbook.category.service.CategoryService;
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

@Controller
@RequiredArgsConstructor
@RequestMapping("/categories")
public class CategoryController {

    private final CategoryService categoryService;
    private final UserService userService;

    @GetMapping
    public String getCategories(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        Long userId = userDetails.getUserId();

        model.addAttribute("categories", categoryService.getCategories(userId));
        model.addAttribute("categoryForm", new CategoryCreateDto());

        return "/categories/list";
    }

    @PostMapping
    public String createCategory(@ModelAttribute("categoryForm") CategoryCreateDto createDto,
                                              BindingResult bindingResult,
                                              @AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        Long userId = userDetails.getUserId();

        if(bindingResult.hasErrors()) {
            model.addAttribute("categories", categoryService.getCategories(userId));
            return "/categories/list";
        }

        categoryService.createCategory(userId, createDto);

        return "redirect:/categories";
    }

    @PostMapping("/{id}/delete")
    public String deleteCategory(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails userDetails) {
        Long userId = userDetails.getUserId();

        categoryService.deleteCategory(id, userId);

        return "redirect:/categories";
    }
}
