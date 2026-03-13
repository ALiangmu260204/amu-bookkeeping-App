package top.aliangmu.bookkeepingapp.domain.vo;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class PageResponseVO<T> {
    private List<T> data;

    private Long total;
}
