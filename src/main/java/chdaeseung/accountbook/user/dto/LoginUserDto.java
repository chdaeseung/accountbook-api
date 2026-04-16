package chdaeseung.accountbook.user.dto;

import chdaeseung.accountbook.user.entity.User;
import lombok.Getter;

@Getter
public class LoginUserDto {
    private final Long id;

    private final String username;

    public LoginUserDto(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
    }
}
