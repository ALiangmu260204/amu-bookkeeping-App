package top.aliangmu.bookkeepingapp.exception;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.stp.StpUtil;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import top.aliangmu.bookkeepingapp.response.Result;

import java.io.IOException;

@Slf4j
@RestControllerAdvice
public class ControllerException {

    @ExceptionHandler
    public Result<Void> handleException(Exception e) {
        Object loginId = StpUtil.isLogin() ? StpUtil.getLoginIdDefaultNull() : "unauthenticated";
        log.info("user: {}",loginId);
        log.error("error: ", e);

        return Result.fail("服务器内部错误，请稍后重试或联系管理员。");
    }

    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusinessException(BusinessException e) {
        return Result.fail(e.getCode(), e.getMessage());
    }

    @ExceptionHandler(NotLoginException.class)
    public void handleNotLoginException(HttpServletResponse response) throws IOException {
        response.setStatus(401);
        response.setContentType("application/json;charset=UTF-8");

        String json = """
        {"code":40100,"message":"token 无效或已过期"}
        """;

        response.getWriter().write(json);
        response.getWriter().flush();
    }
}
