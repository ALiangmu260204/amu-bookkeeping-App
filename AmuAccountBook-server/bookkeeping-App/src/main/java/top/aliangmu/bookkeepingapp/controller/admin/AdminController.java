package top.aliangmu.bookkeepingapp.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import top.aliangmu.bookkeepingapp.domain.dto.AdminLoginDTO;
import top.aliangmu.bookkeepingapp.domain.vo.AdminInfoVO;
import top.aliangmu.bookkeepingapp.domain.vo.AdminLoginVO;
import top.aliangmu.bookkeepingapp.response.Result;
import top.aliangmu.bookkeepingapp.service.AdminService;

@RestController
@RequestMapping("/admin/api")
@Tag(name = "管理端相关")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @PostMapping("/login")
    @Operation(summary = "管理员登录")
    public Result<AdminLoginVO> login(@RequestBody AdminLoginDTO dto) {
        AdminLoginVO vo = adminService.login(dto);
        return Result.data(vo);
    }

    @GetMapping("/admininfo")
    @Operation(summary = "管理员信息")
    public Result<AdminInfoVO> getAdminInfo(){
        AdminInfoVO vo = adminService.getInfo();
        return Result.data(vo);
    }
}
