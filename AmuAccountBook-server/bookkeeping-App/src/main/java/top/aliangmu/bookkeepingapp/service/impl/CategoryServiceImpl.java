package top.aliangmu.bookkeepingapp.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.http.HtmlUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import top.aliangmu.bookkeepingapp.domain.entity.Category;
import top.aliangmu.bookkeepingapp.domain.vo.CategoryVO;
import top.aliangmu.bookkeepingapp.exception.BusinessException;
import top.aliangmu.bookkeepingapp.mapper.CategoryMapper;
import top.aliangmu.bookkeepingapp.service.CategoryService;

import java.util.List;

@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Override
    public List<Category> list(Long userId) {
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .eq(Category::getUserId, 0L)
                .or()
                .eq(Category::getUserId, userId);

        return this.list(queryWrapper);
    }

    @Override
    public CategoryVO add(String name, Integer isType) {
        if (name.isEmpty()) throw new BusinessException("类别名称不能为空");
        if (name.length() > 4) throw new BusinessException("类别名称长度最多4个字符");
        if (isType == null || (isType != 0 && isType != 1)) throw new BusinessException("类型参数无效");

        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .eq(Category::getUserId, 0L)
                .eq(Category::getName, name);
        if (this.getOne(queryWrapper) != null) throw new BusinessException("自定义分类名不能与系统预置重名");

        try {
            Category category = new Category();
            category.setUserId(StpUtil.getLoginIdAsLong());
            category.setName(HtmlUtil.escape(name));
            category.setIcon(isType == 0 ? "other_spend" : "other_income");
            category.setIsType(isType);
            category.setIsSystem(false);
            this.save(category);

            return CategoryVO.builder()
                    .id(category.getId())
                    .name(category.getName())
                    .icon(category.getIcon())
                    .isType(category.getIsType())
                    .isSystem(category.getIsSystem())
                    .build();
        } catch (DuplicateKeyException e) {
            throw new BusinessException("改类别已存在，不可重复添加");
        } catch (Exception e) {
            log.error("添加类别异常：", e);
            throw new BusinessException("添加失败，请重试");
        }
    }

    @Override
    public void delete(String name, Integer isType) {
        Category category = this.getOne(
                new LambdaUpdateWrapper<Category>()
                        .eq(Category::getUserId, StpUtil.getLoginIdAsLong())
                        .eq(Category::getName, HtmlUtil.escape(name))
                        .eq(Category::getIsType, isType)
        );

        if (category == null) {
            throw new BusinessException("分类不存在或无权限删除");
        }

        try {
            this.removeById(category);
        } catch (DataIntegrityViolationException e) {
            log.error("删除类别异常：", e);
            throw new BusinessException("无法删除已被使用的分类");
        }
    }
}
