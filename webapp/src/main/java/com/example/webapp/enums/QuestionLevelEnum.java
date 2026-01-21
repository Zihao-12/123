package com.example.webapp.enums;



import io.micrometer.common.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ghs
 * @description
 */
public enum QuestionLevelEnum {
    /**
     * 1星 2星 3星 4星 5星
     */
    ONE(1,"1星"),
    TWO(2,"2星"),
    THREE(3,"3星"),
    FOUR(4,"4星"),
    FIVE(5,"5星");
    private Integer value;
    private String name;

    QuestionLevelEnum(Integer value, String name) {
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

    /**
     * 将数字转换为中文释义(传1,2,3等)
     * @param level
     * @return
     */
    public static String getLevelName(String level){
        if(StringUtils.isBlank(level)){
            return "";
        }
        String[] arrLevel = level.split(",");
        StringBuilder result = new StringBuilder();
        for (String le : arrLevel) {
            QuestionLevelEnum en = getLevelEnum(Integer.valueOf(le));
            if(en==null){
                continue;
            }
            result.append(en.getName()).append(";");
        }
        return result.toString();
    }

    public static QuestionLevelEnum getLevelEnum(Integer value){
        if(value == null || value <= ONE.value){
            return ONE;
        }else if(value>=FIVE.value){
            return FIVE;
        }
        QuestionLevelEnum[] values = QuestionLevelEnum.values();
        for (QuestionLevelEnum levelEnum : values) {
            if (levelEnum.value.equals(value)) {
                return levelEnum;
            }
        }
        return null;
    }

    public static List<EnumsVO> getAllLevelEnum(){
        List<EnumsVO> list = new ArrayList<>();
        QuestionLevelEnum[] values = QuestionLevelEnum.values();
        for (QuestionLevelEnum levelEnum : values) {
            EnumsVO vo = new EnumsVO();
            vo.setName(levelEnum.name);
            vo.setValue(levelEnum.value);
            list.add(vo);
        }
        return list;
    }
}
