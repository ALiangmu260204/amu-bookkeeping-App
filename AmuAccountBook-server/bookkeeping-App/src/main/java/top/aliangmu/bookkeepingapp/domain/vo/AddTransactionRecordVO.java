package top.aliangmu.bookkeepingapp.domain.vo;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class AddTransactionRecordVO {
    private Long id;

    private BigDecimal amount;

    private Long categoryId;

    private String categoryName;

    private Integer isType;

    private String remark;

    private LocalDateTime transactionTime;
}
