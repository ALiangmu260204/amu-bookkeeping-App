package top.aliangmu.bookkeepingapp.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.http.HtmlUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import top.aliangmu.bookkeepingapp.domain.dto.AddTransactionRecordDTO;
import top.aliangmu.bookkeepingapp.domain.dto.UpdateTransactionRecordDTO;
import top.aliangmu.bookkeepingapp.domain.entity.Category;
import top.aliangmu.bookkeepingapp.domain.entity.TransactionRecord;
import top.aliangmu.bookkeepingapp.domain.vo.AddTransactionRecordVO;
import top.aliangmu.bookkeepingapp.domain.vo.PageResponseVO;
import top.aliangmu.bookkeepingapp.domain.vo.TransactionRecordVO;
import top.aliangmu.bookkeepingapp.domain.vo.TransactionStatsVO;
import top.aliangmu.bookkeepingapp.exception.BusinessException;
import top.aliangmu.bookkeepingapp.mapper.CategoryMapper;
import top.aliangmu.bookkeepingapp.mapper.TransactionRecordMapper;
import top.aliangmu.bookkeepingapp.service.TransactionRecordService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionRecordServiceImpl extends ServiceImpl<TransactionRecordMapper, TransactionRecord>
        implements TransactionRecordService {

    private final CategoryMapper categoryMapper;

    /**
     * 每次登录默认返回 7天 交易记录数据
     * @param userId
     * @return
     */
    @Override
    public List<TransactionRecord> list(Long userId) {
        LambdaQueryWrapper<TransactionRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TransactionRecord::getUserId, userId)
                .orderByDesc(TransactionRecord::getTransactionTime)
                .last("LIMIT 25");

        return this.list(queryWrapper);
    }

    @Override
    public AddTransactionRecordVO add(AddTransactionRecordDTO dto) {
        TransactionRecord transactionRecord = new TransactionRecord();
        transactionRecord.setUserId(StpUtil.getLoginIdAsLong());
        transactionRecord.setAmount(dto.getAmount());
        transactionRecord.setCategoryId(dto.getCategoryId());
        transactionRecord.setIsType(dto.getIsType());
        transactionRecord.setRemark(HtmlUtil.escape(dto.getRemark()));
        transactionRecord.setTransactionTime(dto.getTransactionTime());
        boolean saved = this.save(transactionRecord);

        if (!saved) throw new BusinessException("交易记录保存失败");

        AddTransactionRecordVO vo = new AddTransactionRecordVO();
        vo.setCategoryName(dto.getCategoryName());
        BeanUtil.copyProperties(transactionRecord, vo);

        return vo;
    }

    @Override
    public void update(UpdateTransactionRecordDTO dto) {
        TransactionRecord transactionRecord = this.getOne(
                new LambdaQueryWrapper<TransactionRecord>()
                        .eq(TransactionRecord::getId, dto.getId())
                        .eq(TransactionRecord::getUserId, StpUtil.getLoginIdAsLong())

        );

        if (transactionRecord == null)  throw new BusinessException("该流水不存在，更新失败");

        if (dto.getAmount() == null || dto.getCategoryId() == null || dto.getTransactionTime() == null) {
            throw new BusinessException("请按照规填写必要的数据");
        }

        LambdaUpdateWrapper<TransactionRecord> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper
                .set(TransactionRecord::getAmount, dto.getAmount())
                .set(TransactionRecord::getCategoryId, dto.getCategoryId())
                .set(TransactionRecord::getIsType, dto.getIsType())
                .set(TransactionRecord::getRemark, HtmlUtil.escape(dto.getRemark()))
                .set(TransactionRecord::getTransactionTime, dto.getTransactionTime());
        updateWrapper
                .eq(TransactionRecord::getId, dto.getId())
                .eq(TransactionRecord::getUserId, StpUtil.getLoginIdAsLong());

        boolean success = this.update(updateWrapper);

        if (!success) throw new BusinessException("更新交易记录失败，请重新尝试");
    }

    @Override
    public void delete(Long id) {
        LambdaQueryWrapper<TransactionRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .eq(TransactionRecord::getId, id)
                .eq(TransactionRecord::getUserId, StpUtil.getLoginIdAsLong());

        long deleteCount = this.count(queryWrapper);
        if (deleteCount == 0) throw new BusinessException("未找到该条交易记录");

        boolean removed = this.remove(queryWrapper);
        if (!removed) throw new BusinessException("删除失败");
    }

    @Override
    public PageResponseVO<TransactionRecordVO> loadMore(Integer page, Integer pageSize) {
        if (page == null || pageSize == null || page < 1 || pageSize <= 0 || pageSize > 26) {
            throw new BusinessException("数据暂时无法加载，请再次上滑试试");
        }

        LambdaQueryWrapper<Category> categoryWrapper = new LambdaQueryWrapper<>();
        categoryWrapper
                .eq(Category::getUserId, 0)
                .or()
                .eq(Category::getUserId, StpUtil.getLoginIdAsLong());
        List<Category> categories = categoryMapper.selectList(categoryWrapper);
        Map<Long, String> categoryMap = categories.stream().collect(
                Collectors.toMap(Category::getId, Category::getName)
        );

        PageHelper.startPage(page, pageSize);

        LambdaQueryWrapper<TransactionRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .eq(TransactionRecord::getUserId, StpUtil.getLoginIdAsLong())
                .orderByDesc(TransactionRecord::getTransactionTime);

        List<TransactionRecord> records = this.list(queryWrapper);

        long total = ((com.github.pagehelper.Page<TransactionRecord>) records).getTotal();

        List<TransactionRecordVO> voList = records.stream()
                .map(record -> {
                    TransactionRecordVO vo = new TransactionRecordVO();
                    vo.setCategoryName(categoryMap.getOrDefault(record.getCategoryId(), "未知分类"));
                    BeanUtil.copyProperties(record, vo);
                    return vo;
                }).toList();

        PageResponseVO<TransactionRecordVO> response = new PageResponseVO<>();
        response.setData(voList);
        response.setTotal(total);

        return response;
    }

    @Override
    public void clearAll(Long userId) {
        if (userId != null && userId.equals(StpUtil.getLoginIdAsLong())) {
            LambdaQueryWrapper<TransactionRecord> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(TransactionRecord::getUserId, userId);
            this.remove(queryWrapper);
        } else {
            throw new BusinessException("用户身份不匹配，请重新尝试");
        }
    }

    @Override
    public List<TransactionRecordVO> getMonthRecord(Integer year, Integer month) {
        if (year == null || month == null || month < 1 || month > 12) {
            throw new BusinessException("年份或月份参数无效，请重试");
        }

        LocalDateTime startTime = LocalDateTime.of(year, month, 1, 0, 0, 0);
        LocalDateTime endTime = LocalDateTime.of(year + 1, month, 1, 0, 0, 0);

        LambdaQueryWrapper<TransactionRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .eq(TransactionRecord::getUserId, StpUtil.getLoginIdAsLong())
                .ge(TransactionRecord::getTransactionTime, startTime)
                .le(TransactionRecord::getTransactionTime, endTime);
        List<TransactionRecord> records = this.list(queryWrapper);

        LambdaQueryWrapper<Category> categoryWrapper = new LambdaQueryWrapper<>();
        categoryWrapper
                .eq(Category::getUserId, 0)
                .or()
                .eq(Category::getUserId, StpUtil.getLoginIdAsLong());
        List<Category> categories = categoryMapper.selectList(categoryWrapper);
        Map<Long, String> categoryMap = categories.stream().collect(
                Collectors.toMap(Category::getId, Category::getName)
        );

        return records.stream()
                .map(record -> {
                    TransactionRecordVO vo = new TransactionRecordVO();
                    vo.setCategoryName(categoryMap.getOrDefault(record.getCategoryId(), "未知分类"));
                    BeanUtil.copyProperties(record, vo);
                    return vo;
                }).toList();
    }

    @Override
    public TransactionStatsVO getStats(Integer year) {
        if (year == null) throw new BusinessException("年份不能为空");

        LocalDateTime startTime = LocalDateTime.of(year, 1, 1, 0, 0, 0);
        LocalDateTime endTime = LocalDateTime.of(year + 1, 1, 1, 0, 0, 0);

        LambdaQueryWrapper<TransactionRecord> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .eq(TransactionRecord::getUserId, StpUtil.getLoginIdAsLong())
                .ge(TransactionRecord::getTransactionTime, startTime)
                .le(TransactionRecord::getTransactionTime, endTime);

        List<TransactionRecord> records = this.list(queryWrapper);

        HashMap<Integer, BigDecimal> expenseMap = new HashMap<>();
        HashMap<Integer, BigDecimal> incomeMap = new HashMap<>();

        for (int i = 1; i <= 12; i++) {
            expenseMap.put(i, BigDecimal.ZERO);
            incomeMap.put(i, BigDecimal.ZERO);
        }

        for (TransactionRecord record : records) {
            int month = record.getTransactionTime().getMonthValue();

            if (record.getIsType() == 0) {
                expenseMap.merge(month, record.getAmount(), BigDecimal::add);
            } else if (record.getIsType() == 1) {
                incomeMap.merge(month, record.getAmount(), BigDecimal::add);
            }
        }

        TransactionStatsVO vo = new TransactionStatsVO();

        List<BigDecimal> monthExpenses = new ArrayList<>();
        List<BigDecimal> monthIncomes = new ArrayList<>();

        for (int i = 1; i <= 12; i++) {
            monthExpenses.add(expenseMap.get(i));
            monthIncomes.add(incomeMap.get(i));
        }

        vo.setMonthExpense(monthExpenses);
        vo.setMonthIncome(monthIncomes);

        return vo;
    }
}
