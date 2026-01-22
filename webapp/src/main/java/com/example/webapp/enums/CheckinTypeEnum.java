package com.example.webapp.enums;

public enum CheckinTypeEnum {
    /**
     * -1消耗积分
     * 0视频签到
     * 1活动签到
     */
    CONSUME(-1,"消耗积分",0),
    VIDEO(0,"视频签到",20),
    ACTIVITY(1,"活动签到",0),
    LOTTERY(2,"活动抽奖",0);
    private int type;
    private int score;
    private String name;


    CheckinTypeEnum(int type, String name,int score) {
        this.type = type;
        this.name = name;
        this.score =score;
    }

    public static String getTypeName(int type){
        CheckinTypeEnum statusEnum = getTypeEnum(type);
        return statusEnum==null?"":statusEnum.name;
    }

    public static CheckinTypeEnum getTypeEnum(int type){
        CheckinTypeEnum[] values = CheckinTypeEnum.values();
        for (CheckinTypeEnum value : values) {
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
