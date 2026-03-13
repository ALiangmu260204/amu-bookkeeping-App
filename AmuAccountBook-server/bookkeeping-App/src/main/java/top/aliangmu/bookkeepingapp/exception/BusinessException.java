package top.aliangmu.bookkeepingapp.exception;

import lombok.Getter;
import top.aliangmu.bookkeepingapp.response.ResponseCode;

@Getter
public class BusinessException extends RuntimeException {
    private Integer code;
    private String message;

    public BusinessException(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public BusinessException(String message) {
        this.message = message;
    }

    public BusinessException(ResponseCode responseCode) {
        this.code = responseCode.getCode();
        this.message = responseCode.getMessage();
    }
}
