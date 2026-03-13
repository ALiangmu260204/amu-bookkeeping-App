package top.aliangmu.bookkeepingapp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.aliangmu.bookkeepingapp.domain.entity.Category;
import top.aliangmu.bookkeepingapp.domain.vo.CategoryVO;

import java.util.List;

public interface CategoryService extends IService<Category> {

    List<Category> list(Long userId);

    CategoryVO add(String name, Integer isType);

    void delete(String name, Integer isType);
}
