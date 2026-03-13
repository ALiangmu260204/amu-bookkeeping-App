package top.aliangmu.bookkeepingapp.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.RandomUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import top.aliangmu.bookkeepingapp.domain.dto.WechatLoginResponseDTO;
import top.aliangmu.bookkeepingapp.domain.entity.Category;
import top.aliangmu.bookkeepingapp.domain.entity.TransactionRecord;
import top.aliangmu.bookkeepingapp.domain.entity.User;
import top.aliangmu.bookkeepingapp.domain.vo.*;
import top.aliangmu.bookkeepingapp.exception.BusinessException;
import top.aliangmu.bookkeepingapp.mapper.TransactionRecordMapper;
import top.aliangmu.bookkeepingapp.mapper.UserMapper;
import top.aliangmu.bookkeepingapp.service.CategoryService;
import top.aliangmu.bookkeepingapp.service.TransactionRecordService;
import top.aliangmu.bookkeepingapp.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${aliangmu.wechat-mini-program.appid}")
    private String appid;

    @Value("${aliangmu.wechat-mini-program.secret}")
    private String secret;

    private final CategoryService categoryService;

    private final TransactionRecordService transactionRecordService;

    private final TransactionRecordMapper transactionRecordMapper;

    @Override
    public UserLoginVO login(String code) {
        String url = String.format("https://api.weixin.qq.com/sns/jscode2session" +
                "?appid=%s" +
                "&secret=%s" +
                "&js_code=%s" +
                "&grant_type=authorization_code", appid, secret, code);

        try {
            String response = restTemplate.getForObject(url, String.class);
            WechatLoginResponseDTO wechatResp = new ObjectMapper().readValue(response, WechatLoginResponseDTO.class);
            if (wechatResp == null) {
                log.warn("微信登录接口响应为空");
                throw new BusinessException("微信登录失败，请重新进入");
            } else if (wechatResp.getErrcode() != null && wechatResp.getErrcode() != 0 ) {
                throw new BusinessException("微信登录失败，请重新进入");
            }

            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getOpenid, wechatResp.getOpenid());
            User user = this.getOne(queryWrapper);

            if (user == null) {
                user = new User();
                Long userId = generateUniqueId();
                user.setOpenid(wechatResp.getOpenid());
                user.setUserId(userId);
                this.save(user);
            }

            StpUtil.logout(user.getUserId());
            StpUtil.login(user.getUserId());

            // 序列化 过滤掉一些不必要的数据
            List<Category> categoryList = categoryService.list(user.getUserId());
            List<CategoryVO> categoryVOList = categoryList
                    .stream().map(category -> {
                        CategoryVO categoryVO = new CategoryVO();
                        categoryVO.setId(category.getId());
                        categoryVO.setName(category.getName());
                        categoryVO.setIcon(category.getIcon());
                        categoryVO.setIsType(category.getIsType());
                        categoryVO.setIsSystem(category.getIsSystem());
                        return categoryVO;
                    }).toList();

            Map<Long, String> categoryMap = categoryVOList.stream()
                    .collect(Collectors.toMap(CategoryVO::getId, CategoryVO::getName));

            List<TransactionRecord> transactionRecordList =  transactionRecordService.list(user.getUserId());
            List<TransactionRecordVO> transactionRecordVOList = transactionRecordList
                    .stream().map(record -> {
                        TransactionRecordVO transactionVO = new TransactionRecordVO();
                        transactionVO.setId(record.getId());
                        transactionVO.setAmount(record.getAmount());
                        transactionVO.setCategoryId(record.getCategoryId());
                        transactionVO.setCategoryName(categoryMap.get(record.getCategoryId()));
                        transactionVO.setIsType(record.getIsType());
                        transactionVO.setRemark(record.getRemark());
                        transactionVO.setTransactionTime(record.getTransactionTime());
                        return transactionVO;
                    }).toList();

            LambdaQueryWrapper<TransactionRecord> transactionQueryWrapper = new LambdaQueryWrapper<>();
            transactionQueryWrapper.eq(TransactionRecord::getUserId, StpUtil.getLoginIdAsLong());

            return UserLoginVO
                    .builder()
                    .userId(user.getUserId())
                    .accessToken(StpUtil.getTokenValue())
                    .recordTotal(transactionRecordMapper.selectCount(transactionQueryWrapper))
                    .categoryList(categoryVOList)
                    .transactionRecordList(transactionRecordVOList)
                    .build();
        } catch (Exception e) {
            log.error(String.valueOf(e));
            throw new BusinessException("当前服务不可用，请稍后重试");
        }
    }

    private Long generateUniqueId() {
        int attempts = 0;

        while (attempts < 5) {
            attempts++;
            Long userId = Long.valueOf(RandomUtil.randomNumbers(13));

            if (!this.lambdaQuery().eq(User::getUserId, userId).exists()) {
                return userId;
            }
        }

        log.error("在5次尝试后未能生成唯一的用户ID");
        throw new BusinessException("系统繁忙，暂时不能登录");
    }
}