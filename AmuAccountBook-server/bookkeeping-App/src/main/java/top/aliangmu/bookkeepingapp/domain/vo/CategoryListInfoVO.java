package top.aliangmu.bookkeepingapp.domain.vo;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class CategoryListInfoVO {
    private List<CategoryInfoVO>  categoryList;

    private Long total;
}
