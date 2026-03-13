package top.aliangmu.bookkeepingapp.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

@Getter
public class Result<T> {
    private final Integer code;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String message;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private T data;

    public Result(Integer code) {
        this.code = code;
    }

    public Result(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public Result(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public static <T> Result<T> success() {
        return new Result<>(ResponseCode.SUCCESS.getCode());
    }

    public static <T> Result<T> success(String message) {
        return new Result<>(ResponseCode.SUCCESS.getCode(), message);
    }

    public static <T> Result<T> success(ResponseCode responseCode) {
        return new Result<>(responseCode.getCode(), responseCode.getMessage());
    }

    public static <T> Result<T> data(T data) {
        return new Result<>(ResponseCode.SUCCESS.getCode(), ResponseCode.SUCCESS.getMessage(), data);
    }

    public static <T> Result<T> data(ResponseCode responseCode, T data) {
        return new Result<>(responseCode.getCode(), responseCode.getMessage(), data);
    }

    public static <T> Result<T> fail() {
        return new Result<>(ResponseCode.ERROR.getCode());
    }

    public static <T> Result<T> fail(String message) {
        return new Result<>(ResponseCode.ERROR.getCode(), message);
    }

    public static <T> Result<T> fail(ResponseCode responseCode) {
        return new Result<>(responseCode.getCode(), responseCode.getMessage());
    }

    public static <T> Result<T> fail(Integer code, String message) {
        return new Result<>(code, message);
    }
}
