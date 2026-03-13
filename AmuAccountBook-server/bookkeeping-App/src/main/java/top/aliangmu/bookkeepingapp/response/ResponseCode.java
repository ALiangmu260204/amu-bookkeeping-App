package top.aliangmu.bookkeepingapp.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum ResponseCode {
    SUCCESS(20000, "操作成功"),
    CREATED(20100, "创建成功"),
    INVALID_CREDENTIALS(40100, "未经授权请登录"),
    USERNAME_OR_PASSWORD_INCORRECT(10001, "账号或密码错误"),
    USER_ALREADY_EXISTS(10003, "用户已存在"),
    ACCOUNT_DISABLED(10004, "账号已被禁用"),
    CAPTCHA_GENERATE_FAILED(11001, "验证码生成失败"),
    CAPTCHA_INVALID(11002, "验证码内容错误"),
    CAPTCHA_EXPIRED(11003, "验证码已过期"),
    SERVICE_UNAVAILABLE(50300, "服务暂时不可用"),
    ERROR(50000, "操作失败");

    private final Integer code;
    private final String message;
}
