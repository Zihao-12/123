package com.example.webapp.enums;



import com.example.webapp.VO.EnumsVO;

import java.util.ArrayList;
import java.util.List;

public enum QuestionEditDelEnum {
    /**
     * 编辑删除 0正常  1编辑删除-编辑时被试卷引用则删除，
     */
    NORMAL(0,"正常"),
    DEL(1,"编辑删除");
    private Integer value;
    private String name;

    QuestionEditDelEnum(Integer value, String name) {
        this.value = value;
        this.name = name;
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


    public static QuestionEditDelEnum getLevelEnum(Integer value){
        QuestionEditDelEnum[] values = QuestionEditDelEnum.values();
        for (QuestionEditDelEnum levelEnum : values) {
            if (levelEnum.value.equals(value)) {
                return levelEnum;
            }
        }
        return null;
    }

    public static List<EnumsVO> getAllLevelEnum(){
        List<EnumsVO> list = new ArrayList<>();
        QuestionEditDelEnum[] values = QuestionEditDelEnum.values();
        for (QuestionEditDelEnum levelEnum : values) {
            EnumsVO vo = new EnumsVO();
            vo.setName(levelEnum.name);
            vo.setValue(levelEnum.value);
            list.add(vo);
        }
        return list;
    }
}
