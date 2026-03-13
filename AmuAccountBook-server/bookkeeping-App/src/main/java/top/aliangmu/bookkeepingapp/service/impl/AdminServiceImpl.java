package top.aliangmu.bookkeepingapp.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import top.aliangmu.bookkeepingapp.domain.dto.AdminLoginDTO;
import top.aliangmu.bookkeepingapp.domain.entity.Admin;
import top.aliangmu.bookkeepingapp.domain.vo.AdminInfoVO;
import top.aliangmu.bookkeepingapp.domain.vo.AdminLoginVO;
import top.aliangmu.bookkeepingapp.exception.BusinessException;
import top.aliangmu.bookkeepingapp.mapper.AdminMapper;
import top.aliangmu.bookkeepingapp.service.AdminService;

@Service
public class AdminServiceImpl extends ServiceImpl<AdminMapper, Admin> implements AdminService {

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public AdminLoginVO login(AdminLoginDTO dto) {
        if (dto.getUsername() == null || dto.getPassword() == null) {
            throw new BusinessException(40100, "用户名和密码不能为空");
        }

        String redisKey = "captcha:"  + dto.getUuid();
        String captcha = redisTemplate.opsForValue().get(redisKey);
        if (captcha == null) {
            throw new BusinessException(40100, "验证码已过期，请刷新");
        } else if (!captcha.equals(dto.getCaptcha())) {
            throw new BusinessException(40100, "验证码错误");
        }

        Admin admin = lambdaQuery()
                .eq(Admin::getUsername, dto.getUsername())
                .one();
        if (admin == null || !passwordEncoder.matches(dto.getPassword(), admin.getPassword()))
            throw new BusinessException(40100, "用户名或密码错误");

        StpUtil.logout(dto.getUsername());
        if (dto.getRememberMe()) {
            StpUtil.login(dto.getUsername(), 604800);
        } else {
            StpUtil.login(dto.getUsername(), 7200);
        }

        AdminLoginVO adminLoginVO = new AdminLoginVO();
        adminLoginVO.setToken(StpUtil.getTokenValue());
        redisTemplate.delete(redisKey);

        return adminLoginVO;
    }

    @Override
    public AdminInfoVO getInfo() {
        Admin admin = lambdaQuery()
                .eq(Admin::getUsername, StpUtil.getLoginIdAsString())
                .one();
        if (admin == null) throw new BusinessException("数据加载失败，请重试");

        return AdminInfoVO.builder()
                .name(admin.getName())
                .username(admin.getUsername())
                .avatar(admin.getAvatar())
                .build();
    }
}
