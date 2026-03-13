package top.aliangmu.bookkeepingapp.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import top.aliangmu.bookkeepingapp.domain.vo.CaptchaVO;
import top.aliangmu.bookkeepingapp.response.Result;
import top.aliangmu.bookkeepingapp.service.CaptchaService;

@RequestMapping("/admin/api")
@RestController
@Tag(name = "常见信息接口")
@RequiredArgsConstructor
public class CommonController {

    private final CaptchaService captchaService;

    @GetMapping("/captcha")
    @Operation(summary = "验证码")
    public Result<CaptchaVO>  getCaptcha(){
        CaptchaVO vo = captchaService.create();
        return Result.data(vo);
    }
}
