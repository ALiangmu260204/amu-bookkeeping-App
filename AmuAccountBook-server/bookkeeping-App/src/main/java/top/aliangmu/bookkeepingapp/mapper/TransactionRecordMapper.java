package top.aliangmu.bookkeepingapp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import top.aliangmu.bookkeepingapp.domain.entity.TransactionRecord;

@Mapper
public interface TransactionRecordMapper extends BaseMapper<TransactionRecord> {
}
