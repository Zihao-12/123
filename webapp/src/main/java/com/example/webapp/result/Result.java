package com.example.webapp.result;

import java.io.Serializable;

public class Result<T> implements Serializable {

    private static  final  long serialVersionUID = 1L;
    private Integer code;
    private String message;
    private T data;

    public static <T> Result<T> ok(T data) {
        return new Result(CodeEnum.SUCCESS.getValue(),data);
    }

    public static <T> Result<T> fail() {
        return new Result(CodeEnum.FAILED.getValue());
    }

    public static <T> Result<T> fail(String errorMsg) {
        return new Result(CodeEnum.FAILED.getValue(), errorMsg, null);
    }

    public static <T> Result<T> loginInvalid(String errorMsg) {
        return new Result(CodeEnum.LOGIN_INVALID.getValue(), errorMsg, null);
    }

    public static <T> Result<T> fail(Integer code,String errorMsg) {
        return new Result(code, errorMsg, null);
    }

    public static <T> Result<T> fail(Integer code,String errorMsg,T data) {
        return new Result(code, errorMsg, data);
    }

    private Result(Integer code) {
        this.code = code;
    }
    private Result() {
    }

    private Result(Integer code, T data) {
        this(code, "", data);
    }

    public Result(Integer code, String message, T data) {
        this.code = code;
        this.message = message;
        this.data = data;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public T getData() {
        return data;
    }

    public Result<T> setCode(Integer code) {
        this.code = code;
        return this;
    }

    public Result<T> setMessage(String message) {
        this.message = message;
        return this;
    }

    public Result<T> setData(T data) {
        this.data = data;
        return this;
    }
}
