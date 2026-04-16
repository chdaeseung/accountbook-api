package chdaeseung.accountbook.global.controller;

import chdaeseung.accountbook.user.dto.LoginUserDto;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home(HttpSession session) {
        LoginUserDto loginUser = (LoginUserDto) session.getAttribute("loginUser");

        if(loginUser != null) {
            return "redirect:/users/login";
        }

        return "redirect:/dashboard";
    }
}
