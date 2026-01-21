package com.example.webapp.result;

import java.io.Serializable;

public class Result<T> implements Serializable {

    private static  final  long serialVersionUID = 1L;
    private Integer code;
    private String message;
    private T data;
    public static <T> Result<T> ok(T data){
        return new Result(CodeEnum.SUCCESS.getValue(),data);
    }

    private Result(Integer code, T data){
        this(code,"",data);
    }

    public Result(Integer code,String message,T data){
        this.code=code;
        this.message=message;
        this.data=data;
    }
}
