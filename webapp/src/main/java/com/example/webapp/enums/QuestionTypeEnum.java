package com.example.webapp.enums;

import com.zhihuiedu.business.vo.EnumsVO;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ghs
 * @description
 */
public enum QuestionTypeEnum {
    /**
     * 题目类型 1.单选 2.多选 3.判断
     */
    SINGLE_CHOICE(1,"单选题"),
    MULTIPLE_CHOICE(2,"多选题"),
    JUDGE(3,"判断题"),
    NULL(-1,"");
    private Integer value;
    private String name;

    QuestionTypeEnum(Integer value, String name) {
        this.value = value;
        this.name = name;
    }

    public static String getTypeName(Integer value){
        QuestionTypeEnum typeEnum = getTypeEnum(value);
        return typeEnum==null?"":typeEnum.name;
    }

    public static QuestionTypeEnum getTypeEnum(Integer value){
        QuestionTypeEnum[] values = QuestionTypeEnum.values();
        for (QuestionTypeEnum typeEnum : values) {
            if (typeEnum.value.equals(value)) {
                return typeEnum;
            }
        }
        return QuestionTypeEnum.NULL;
    }

    public static List<EnumsVO> getAllTypeEnum(){
        List<EnumsVO> list = new ArrayList<>();
        QuestionTypeEnum[] values = QuestionTypeEnum.values();
        for (QuestionTypeEnum typeEnum : values) {
            EnumsVO vo = new EnumsVO();
            vo.setName(typeEnum.name);
            vo.setValue(typeEnum.value);
            list.add(vo);
        }
        return list;
    }

    public Integer getValue() {
        return value;
    }

    public void setValue(Integer value) {
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
