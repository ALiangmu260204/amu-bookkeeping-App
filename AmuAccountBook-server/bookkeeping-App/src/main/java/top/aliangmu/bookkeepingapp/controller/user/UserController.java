package top.aliangmu.bookkeepingapp.controller.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import top.aliangmu.bookkeepingapp.domain.dto.UserLoginDTO;
import top.aliangmu.bookkeepingapp.domain.vo.UserLoginVO;
import top.aliangmu.bookkeepingapp.response.Result;
import top.aliangmu.bookkeepingapp.service.UserService;

@RestController
@RequestMapping("/user")
@Tag(name = "用户登录相关")
@RequiredArgsConstructor
@CrossOrigin
public class UserController {

    private final UserService userService;

    @PostMapping("/login")
    @Operation(summary = "用户登录")
    public Result<UserLoginVO> login(@RequestBody UserLoginDTO dto) {
        UserLoginVO vo = userService.login(dto.getCode());
        return Result.data(vo);
    }
}
