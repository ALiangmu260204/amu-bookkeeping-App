package top.aliangmu.bookkeepingapp.domain.dto;

import lombok.Getter;

@Getter
public class UpdateCategoryDTO {
    private Long userId;

    private String name;

    private String newName;

    private Integer isType;
}
