package top.aliangmu.bookkeepingapp.controller.user;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import top.aliangmu.bookkeepingapp.domain.dto.CategoryDTO;
import top.aliangmu.bookkeepingapp.domain.vo.CategoryVO;
import top.aliangmu.bookkeepingapp.response.Result;
import top.aliangmu.bookkeepingapp.service.CategoryService;

@RestController
@RequestMapping("/user")
@Tag(name = "分类想过")
@RequiredArgsConstructor
@CrossOrigin
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping("/category/add")
    @Operation(summary = "新增分类")
    public Result<CategoryVO> add(@RequestBody CategoryDTO dto) {
        CategoryVO vo = categoryService.add(dto.getName(), dto.getIsType());
        return Result.data(vo);
    }

    @PostMapping("/category/delete")
    @Operation(summary = "删除分类")
    public Result<Void> delete(@RequestBody CategoryDTO dto) {
        categoryService.delete(dto.getName(), dto.getIsType());
        return Result.success();
    }
}
