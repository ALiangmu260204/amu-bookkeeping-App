package top.aliangmu.bookkeepingapp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import top.aliangmu.bookkeepingapp.domain.entity.Category;
import top.aliangmu.bookkeepingapp.domain.vo.CategoryInfoVO;

import java.util.List;

@Mapper
public interface CategoryMapper extends BaseMapper<Category> {

    @Select("SELECT user_id, name, icon, is_type, is_system, create_at FROM category")
    List<CategoryInfoVO> selectAll();

    @Select("SELECT user_id, name, icon, is_type, is_system, create_at FROM category " +
            "WHERE is_type = #{is_type}")
    List<CategoryInfoVO> selectByIsType(Integer is_type);

    @Select("SELECT user_id, name, icon, is_type, is_system, create_at FROM category " +
            "WHERE is_system = #{is_system}")
    List<CategoryInfoVO> selectByIsSystem(Integer is_system);

    @Select("SELECT user_id, name, icon, is_type, is_system, create_at FROM category " +
            "WHERE is_type = #{is_type} AND is_system = #{is_system}")
    List<CategoryInfoVO> selectByTypeAndSystem(@Param("is_type")  Integer is_type,
                                               @Param("is_system") Integer is_system);
}
