package top.aliangmu.bookkeepingapp.domain.vo;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class CustomerInfoVO {
    private Long userId;

    private String openid;

    private LocalDateTime createAT;
}
