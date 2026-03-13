package top.aliangmu.bookkeepingapp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import top.aliangmu.bookkeepingapp.domain.dto.AddCategoryDTO;
import top.aliangmu.bookkeepingapp.domain.dto.UpdateCategoryDTO;
import top.aliangmu.bookkeepingapp.domain.dto.UpdateUserInfoDTO;
import top.aliangmu.bookkeepingapp.domain.entity.Category;
import top.aliangmu.bookkeepingapp.domain.entity.User;
import top.aliangmu.bookkeepingapp.domain.vo.*;
import top.aliangmu.bookkeepingapp.exception.BusinessException;
import top.aliangmu.bookkeepingapp.mapper.CategoryMapper;
import top.aliangmu.bookkeepingapp.mapper.UserMapper;
import top.aliangmu.bookkeepingapp.service.ManageService;

import java.util.ArrayList;
import java.util.List;

@Service
public class ManageServiceImpl implements ManageService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Override
    public CustomerListVO getCustomerList(Integer page, Integer size) {
        if (page == null || size == null) throw new BusinessException("缺少必要参数，请刷新重试");

        PageHelper.startPage(page, size);

        List<CustomerInfoVO> customerInfoVOList = userMapper.selectCustomerList();
        PageInfo<CustomerInfoVO> pageInfo = new PageInfo<>(customerInfoVOList);

        return CustomerListVO.builder()
                .userList(customerInfoVOList)
                .total(pageInfo.getTotal())
                .build();
    }

    @Override
    public void updateCustomerInfo(UpdateUserInfoDTO dto) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        User user = userMapper.selectOne(queryWrapper.eq(User::getOpenid, dto.getOpenid()));
        if(user == null) throw new BusinessException("修改失败，检索不到此用户");

        LambdaUpdateWrapper<User> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper
                .eq(User::getOpenid, dto.getOpenid())
                .set(User::getUserId, dto.getUserId());

        userMapper.update(null, updateWrapper);
    }

    @Override
    public void deleteCustomerInfo(Long userId) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        User user = userMapper.selectOne(queryWrapper.eq(User::getUserId, userId));
        if(user == null) throw new BusinessException("删除失败，检索不到此用户");

        userMapper.delete(queryWrapper);
    }

    @Override
    public CategoryListInfoVO getCategoryList(Integer page, Integer size, Integer type, Integer system) {
        if (page == null || size == null) {
            throw new BusinessException("缺少必要参数，请刷新重试");
        }

        if (type < 0 || type > 3 || system < 0 || system > 3) {
            throw new BusinessException("参数错误，type 和 system 只能是 0、1 或 2");
        }

        PageHelper.startPage(page, size);

        List<CategoryInfoVO> categoryInfoVO;
        if (type != 2 && system != 2) {
            categoryInfoVO = categoryMapper.selectByTypeAndSystem(type, system);
        } else if (type == 2 && system != 2) {
            categoryInfoVO = categoryMapper.selectByIsSystem(system);
        } else if (type != 2) {
            categoryInfoVO = categoryMapper.selectByIsType(type);
        } else {
            categoryInfoVO = categoryMapper.selectAll();
        }
        PageInfo<CategoryInfoVO> pageInfo = new PageInfo<>(categoryInfoVO);

        return CategoryListInfoVO
                .builder()
                .categoryList(categoryInfoVO)
                .total(pageInfo.getTotal())
                .build();
    }

    @Override
    public void addCategory(AddCategoryDTO dto) {
        if (dto.getUserId() == null || dto.getName() == null || dto.getIsType() == null) {
            throw new BusinessException("缺少必要参数，新增失败");
        }

        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .eq(Category::getUserId, dto.getUserId())
                .eq(Category::getIsSystem, dto.getIsType())
                .eq(Category::getName, dto.getName());
        Category queryCategory = categoryMapper.selectOne(queryWrapper);
        if (queryCategory != null) throw new BusinessException("此分类已存在，不可重复添加");

        Category category = new Category();
        category.setUserId(dto.getUserId());
        category.setName(dto.getName());
        category.setIsType(dto.getIsType());

        categoryMapper.insert(category);
    }

    @Override
    public void updateCategory(UpdateCategoryDTO dto) {
        if (dto.getUserId() == null || dto.getName() == null || dto.getIsType() == null) {
            throw new BusinessException("缺少必要参数，新增失败");
        }

        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .eq(Category::getUserId, dto.getUserId())
                .eq(Category::getName, dto.getName())
                .eq(Category::getIsType, dto.getIsType());
        Category queryCategory = categoryMapper.selectOne(queryWrapper);
        if (queryCategory == null) throw new BusinessException("检索不到此分类，更新失败");

        Category category = new Category();
        category.setUserId(dto.getUserId());
        category.setName(dto.getNewName());
        category.setIsType(dto.getIsType());

        categoryMapper.update(category, queryWrapper);
    }

    @Override
    public void deleteCategoryInfo(Long userId, String name, Integer isType) {
        if (userId == null || name == null || isType == null) throw new BusinessException("删除失败，缺少必要参数");

        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .eq(Category::getUserId, userId)
                .eq(Category::getName, name)
                .eq(Category::getIsType, isType);
        int queryCategory = categoryMapper.delete(queryWrapper);
        if (queryCategory == 0) throw new BusinessException("删除失败，分类不存在");

    }
}
