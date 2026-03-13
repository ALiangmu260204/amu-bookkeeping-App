package top.aliangmu.bookkeepingapp.domain.vo;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CategoryVO {
    private Long id;

    private String name;

    private String icon;

    private Integer isType;

    private Boolean isSystem;
}
