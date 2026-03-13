package top.aliangmu.bookkeepingapp.domain.dto;

import lombok.Getter;

@Getter
public class AdminLoginDTO {
    private String username;

    private String password;

    private String uuid;

    private String captcha;

    private Boolean rememberMe;
}
