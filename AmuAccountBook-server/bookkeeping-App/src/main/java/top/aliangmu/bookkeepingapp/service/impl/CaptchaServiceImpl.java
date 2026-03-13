package top.aliangmu.bookkeepingapp.service.impl;

import cn.hutool.captcha.CaptchaUtil;
import cn.hutool.captcha.ShearCaptcha;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import top.aliangmu.bookkeepingapp.domain.vo.CaptchaVO;
import top.aliangmu.bookkeepingapp.service.CaptchaService;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class CaptchaServiceImpl implements CaptchaService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Override
    public CaptchaVO create() {
        String uuid = UUID.randomUUID().toString();
        String redisKey = "captcha:" + uuid;

        ShearCaptcha captcha = CaptchaUtil.createShearCaptcha(130, 48, 4, 2);
        captcha.createCode();

        redisTemplate.opsForValue().set(redisKey, captcha.getCode(),
                60, TimeUnit.SECONDS);

        return CaptchaVO.builder()
                .uuid(uuid)
                .image(captcha.getImageBase64())
                .build();
    }
}
