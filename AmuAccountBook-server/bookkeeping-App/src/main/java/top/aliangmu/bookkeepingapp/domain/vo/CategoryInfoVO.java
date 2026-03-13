package top.aliangmu.bookkeepingapp.domain.vo;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CategoryInfoVO {
    private Long userId;

    private String name;

    private String icon;

    private Integer isType;

    private Boolean isSystem;

    private LocalDateTime createAt;
}
