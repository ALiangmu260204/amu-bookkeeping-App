package top.aliangmu.bookkeepingapp.domain.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@TableName("transaction_record")
public class TransactionRecord {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long userId;

    private BigDecimal amount;

    private Long categoryId;

    private Integer isType;

    private String remark;

    private LocalDateTime transactionTime;
}
