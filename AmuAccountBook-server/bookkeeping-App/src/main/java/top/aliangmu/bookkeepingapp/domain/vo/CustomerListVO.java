package top.aliangmu.bookkeepingapp.domain.vo;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class CustomerListVO {
    private List<CustomerInfoVO>  userList;

    private Long total;
}
