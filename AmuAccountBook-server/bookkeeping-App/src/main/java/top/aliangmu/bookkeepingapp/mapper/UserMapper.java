package top.aliangmu.bookkeepingapp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;
import top.aliangmu.bookkeepingapp.domain.entity.User;
import top.aliangmu.bookkeepingapp.domain.vo.CustomerInfoVO;

import java.util.List;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    @Select("SELECT user_id, openid, create_at FROM wechat_user")
    List<CustomerInfoVO> selectCustomerList();
}
