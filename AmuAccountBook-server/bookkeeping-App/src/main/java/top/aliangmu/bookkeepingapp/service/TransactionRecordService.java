package top.aliangmu.bookkeepingapp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import top.aliangmu.bookkeepingapp.domain.dto.AddTransactionRecordDTO;
import top.aliangmu.bookkeepingapp.domain.dto.UpdateTransactionRecordDTO;
import top.aliangmu.bookkeepingapp.domain.entity.TransactionRecord;
import top.aliangmu.bookkeepingapp.domain.vo.AddTransactionRecordVO;
import top.aliangmu.bookkeepingapp.domain.vo.PageResponseVO;
import top.aliangmu.bookkeepingapp.domain.vo.TransactionRecordVO;
import top.aliangmu.bookkeepingapp.domain.vo.TransactionStatsVO;

import java.time.LocalDate;
import java.util.List;

public interface TransactionRecordService extends IService<TransactionRecord> {

    List<TransactionRecord> list(Long userId);

    AddTransactionRecordVO add(AddTransactionRecordDTO dto);

    void update(UpdateTransactionRecordDTO dto);

    void delete(Long id);

    PageResponseVO<TransactionRecordVO> loadMore(Integer page, Integer pageSize);

    void clearAll(Long userId);

    List<TransactionRecordVO> getMonthRecord(Integer year, Integer month);

    TransactionStatsVO getStats(Integer year);
}
