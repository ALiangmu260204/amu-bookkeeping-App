package top.aliangmu.bookkeepingapp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.aliangmu.bookkeepingapp.domain.entity.User;
import top.aliangmu.bookkeepingapp.domain.vo.UserLoginVO;

public interface UserService extends IService<User> {
    UserLoginVO login(String code);
}
