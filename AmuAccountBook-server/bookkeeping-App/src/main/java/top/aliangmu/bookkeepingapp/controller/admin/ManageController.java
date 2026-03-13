package top.aliangmu.bookkeepingapp.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import top.aliangmu.bookkeepingapp.domain.dto.AddCategoryDTO;
import top.aliangmu.bookkeepingapp.domain.dto.UpdateCategoryDTO;
import top.aliangmu.bookkeepingapp.domain.dto.UpdateUserInfoDTO;
import top.aliangmu.bookkeepingapp.domain.vo.CategoryListInfoVO;
import top.aliangmu.bookkeepingapp.domain.vo.CategoryVO;
import top.aliangmu.bookkeepingapp.domain.vo.CustomerListVO;
import top.aliangmu.bookkeepingapp.response.Result;
import top.aliangmu.bookkeepingapp.service.ManageService;

import java.util.List;

@RestController
@RequestMapping("/admin/api")
@Tag(name = "后台管理接口")
@RequiredArgsConstructor
public class ManageController {

    private final ManageService manageService;

    @GetMapping("/customerlist")
    @Operation(summary = "用户基础信息列表")
    public Result<CustomerListVO> getCustomerList(@RequestParam Integer page, @RequestParam Integer size) {
        CustomerListVO vo = manageService.getCustomerList(page, size);
        return Result.data(vo);
    }

    @PostMapping("/update/customerinfo")
    @Operation(summary = "更新用户基础信息")
    public Result<Void> updateCustomerInfo(@RequestBody UpdateUserInfoDTO dto) {
        manageService.updateCustomerInfo(dto);
        return Result.success();
    }


    @DeleteMapping("/delete/customerinfo/{userId}")
    @Operation(summary = "删除用户账号信息")
    public Result<Void> deleteCustomerInfo(@PathVariable Long userId){
        System.out.println(userId);
        manageService.deleteCustomerInfo(userId);
        return Result.success();
    }

    @GetMapping("/categorylist")
    @Operation(summary = "系统预置分类信息列表")
    public Result<CategoryListInfoVO> getCategoryList(
            @RequestParam Integer page, @RequestParam Integer size,
            @RequestParam(required = false) Integer type,
            @RequestParam(required = false) Integer system
    ) {
        CategoryListInfoVO vo = manageService.getCategoryList(page, size, type, system);
        return Result.data(vo);
    }

    @PostMapping("/add/category")
    @Operation(summary = "新增分类")
    public Result<Void> addCategory(@RequestBody AddCategoryDTO dto) {
        manageService.addCategory(dto);
        return Result.success();
    }

    @PostMapping("/update/category")
    @Operation(summary = "修改分类信息")
    public Result<Void> updateCategory(@RequestBody UpdateCategoryDTO dto) {
        manageService.updateCategory(dto);
        return Result.success();
    }

    @DeleteMapping("/delete/category")
    @Operation(summary = "删除分类")
    public Result<Void> deleteCategory(
            @RequestParam Long userId, @RequestParam String name, @RequestParam Integer isType) {
        manageService.deleteCategoryInfo(userId, name, isType);
        return Result.success();
    }
}
