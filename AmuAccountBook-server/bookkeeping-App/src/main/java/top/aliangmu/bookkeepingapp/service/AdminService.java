package top.aliangmu.bookkeepingapp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.aliangmu.bookkeepingapp.domain.dto.AdminLoginDTO;
import top.aliangmu.bookkeepingapp.domain.entity.Admin;
import top.aliangmu.bookkeepingapp.domain.vo.AdminInfoVO;
import top.aliangmu.bookkeepingapp.domain.vo.AdminLoginVO;

public interface AdminService extends IService<Admin> {

    AdminLoginVO login(AdminLoginDTO dto);

    AdminInfoVO getInfo();

}
