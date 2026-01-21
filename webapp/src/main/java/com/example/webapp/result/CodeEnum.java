package com.example.webapp.result;

/**
 *
 */
public enum CodeEnum {
    SUCCESS(0,"SUCCESS"),
    FAILED(-1,"FAILED"),
    LOGIN_INVALID(-2,"Logininvalid");

    private Integer value;
    private String describing;

    CodeEnum(Integer value, String describing) {
        this.value = value;
        this.describing = describing;
    }

    public Integer getValue(){
        return this.value;
    }



//    public String getDescribing(Integer value){
//
//    }
}
