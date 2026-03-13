package top.aliangmu.bookkeepingapp.domain.vo;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class AdminInfoVO {
    private String name;

    private String username;

    private String avatar;
}
