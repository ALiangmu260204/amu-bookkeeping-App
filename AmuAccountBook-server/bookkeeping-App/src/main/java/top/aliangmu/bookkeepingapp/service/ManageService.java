package top.aliangmu.bookkeepingapp.service;

import top.aliangmu.bookkeepingapp.domain.dto.AddCategoryDTO;
import top.aliangmu.bookkeepingapp.domain.dto.UpdateCategoryDTO;
import top.aliangmu.bookkeepingapp.domain.dto.UpdateUserInfoDTO;
import top.aliangmu.bookkeepingapp.domain.vo.CategoryListInfoVO;
import top.aliangmu.bookkeepingapp.domain.vo.CategoryVO;
import top.aliangmu.bookkeepingapp.domain.vo.CustomerListVO;

import java.util.List;

public interface ManageService {
    CustomerListVO getCustomerList(Integer page, Integer size);

    void updateCustomerInfo(UpdateUserInfoDTO dto);

    void deleteCustomerInfo(Long userId);

    CategoryListInfoVO getCategoryList(Integer page, Integer size, Integer type, Integer system);

    void addCategory(AddCategoryDTO dto);

    void updateCategory(UpdateCategoryDTO dto);

    void deleteCategoryInfo(Long userId, String name, Integer isType);
}
