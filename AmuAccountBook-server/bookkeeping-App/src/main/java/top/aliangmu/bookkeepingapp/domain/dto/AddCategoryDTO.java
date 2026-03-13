package top.aliangmu.bookkeepingapp.domain.dto;

import lombok.Getter;

@Getter
public class AddCategoryDTO {
    private Long userId;

    private String name;

    private Integer isType;
}
