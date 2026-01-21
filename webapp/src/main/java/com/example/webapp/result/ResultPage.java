package com.example.webapp.result;

import java.io.Serializable;

public class ResultPage<T> implements Serializable {

    private static final long serialVersionUID = 1L;
    private Integer code;
    private T data;
    private Integer pageNo;
    private Integer pageSize;
    /**总条数*/
    private Long total;
    /**总页数*/
    private Integer pages;
    private String message;

    public static <T> ResultPage<T> ok(T data ,Integer pageNo, Integer pageSize, Long total) {
        return new ResultPage( data , pageNo,  pageSize,  total);
    }

    public static <T> ResultPage<T> fail() {
        return new ResultPage(CodeEnum.FAILED.getValue());
    }

    public static <T> ResultPage<T> fail(String message) {
        return new ResultPage(CodeEnum.FAILED.getValue(),message);
    }

    public ResultPage(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    public ResultPage() {
    }

    private ResultPage(Integer code){
        this.code=code;
    }
    public ResultPage(T data ,Integer pageNo, Integer pageSize, Long total) {
        this.code = CodeEnum.SUCCESS.getValue();
        this.data = data;
        this.pages=0;
        if(total!=0&&pageSize!=0){
            Long pages=total/pageSize;
            if(total%pageSize!=0){
                pages++;
            }
            this.pages=pages.intValue();
        }
        this.total=total;
        this.pageSize=pageSize;
        pageNo=pageNo>pages?pages:pageNo;
        pageNo=pageNo<1?1:pageNo;
        this.pageNo=pageNo;
        this.message = "";
    }


    public ResultPage<T> setCode(Integer code) {
        this.code = code;
        return this;
    }

    public ResultPage<T> setMessage(String message) {
        this.message = message;
        return this;
    }

    public ResultPage<T> setData(T obj) {
        this.data = obj;
        return this;
    }

}