package com.example.webapp.enums;

public enum ConsumeGoldCoinsTypeEnum {
    /**
     * 积分 消耗类型
     */
    TAKE_COURSE(1,"领取课程",10),
    LOTTERY(2,"抽奖",10);
    private int type;
    private int score;
    private String name;

    ConsumeGoldCoinsTypeEnum(int type, String name, int score) {
        this.type = type;
        this.name = name;
        this.score =score;
    }

    public static String getTypeName(int type){
        ConsumeGoldCoinsTypeEnum statusEnum = getTypeEnum(type);
        return statusEnum==null?"":statusEnum.name;
    }

    public static ConsumeGoldCoinsTypeEnum getTypeEnum(int type){
        ConsumeGoldCoinsTypeEnum[] values = ConsumeGoldCoinsTypeEnum.values();
        for (ConsumeGoldCoinsTypeEnum value : values) {
            if (value.type==type) {
                return value;
            }
        }
        return null;
    }
    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getScore() {
        return score;
    }
}
