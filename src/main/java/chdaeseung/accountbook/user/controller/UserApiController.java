package chdaeseung.accountbook.user.controller;

import chdaeseung.accountbook.global.dto.ApiResponseDto;
import chdaeseung.accountbook.user.dto.SignupRequestDto;
import chdaeseung.accountbook.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserApiController {

    private final UserService userService;

    @PostMapping("/signup")
    public ResponseEntity<ApiResponseDto<Long>> signup(@Valid @RequestBody SignupRequestDto requestDto) {
        Long userId = userService.signup(requestDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponseDto<>(userId, "회원가입이 완료되었습니다."));
    }
}
