package top.aliangmu.bookkeepingapp.domain.vo;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class UserLoginVO {
    private Long userId;

    private String accessToken;

    private Long recordTotal;

    private List<CategoryVO> categoryList;

    private  List<TransactionRecordVO> transactionRecordList;
}
