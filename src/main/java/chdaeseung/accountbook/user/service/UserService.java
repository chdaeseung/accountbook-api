package chdaeseung.accountbook.user.service;

import chdaeseung.accountbook.global.exception.CustomException;
import chdaeseung.accountbook.global.exception.ErrorCode;
import chdaeseung.accountbook.user.dto.LoginRequestDto;
import chdaeseung.accountbook.user.dto.LoginUserDto;
import chdaeseung.accountbook.user.dto.SignupRequestDto;
import chdaeseung.accountbook.user.entity.User;
import chdaeseung.accountbook.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public void signup(SignupRequestDto requestDto) {
        existsUser(requestDto);

        String encodedPassword = passwordEncoder.encode(requestDto.getPassword());

        User user = new User(requestDto.getUsername(), encodedPassword, requestDto.getEmail());

        userRepository.save(user);
    }

    private void existsUser(SignupRequestDto requestDto) {
        if(userRepository.existsByUsername(requestDto.getUsername())) {
            throw new CustomException(ErrorCode.DUPLICATE_USERNAME);
        }

        if(userRepository.existsByEmail(requestDto.getEmail())) {
            throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
        }
    }

    public User login(LoginRequestDto requestDto) {
        User user = userRepository.findByUsername(requestDto.getUsername())
                .orElseThrow(() -> new CustomException(ErrorCode.INCORRECT_ACCOUNT));

//        if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
//            throw new CustomException(ErrorCode.INCORRECT_ACCOUNT);
//        }

        return user;
    }

    @Transactional(readOnly = true)
    public Long getUserIdByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND))
                .getId();
    }
}
