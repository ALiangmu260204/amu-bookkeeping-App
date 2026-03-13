package top.aliangmu.bookkeepingapp.domain.vo;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CaptchaVO {
    private String uuid;
    private String image;
}
