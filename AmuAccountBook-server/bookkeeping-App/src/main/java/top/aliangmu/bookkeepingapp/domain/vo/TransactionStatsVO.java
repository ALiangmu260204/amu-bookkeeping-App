package top.aliangmu.bookkeepingapp.domain.vo;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
public class TransactionStatsVO {
    private List<BigDecimal> monthExpense;

    private List<BigDecimal> monthIncome;
}
