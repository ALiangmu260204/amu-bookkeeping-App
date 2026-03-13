package top.aliangmu.bookkeepingapp.domain.dto;

import lombok.Getter;

@Getter
public class WechatLoginResponseDTO {
    private String session_key;

    private String unionid;

    private String openid;

    private Integer errcode;

    private String errmsg;
}
