package top.aliangmu.bookkeepingapp.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
public class AddTransactionRecordDTO {
    private BigDecimal amount;

    private Long categoryId;

    private String categoryName;

    private Integer isType;

    private String remark;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm")
    private LocalDateTime transactionTime;
}
