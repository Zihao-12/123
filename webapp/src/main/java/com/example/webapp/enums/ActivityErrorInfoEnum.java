package com.example.webapp.enums;

public enum ActivityErrorInfoEnum {
    /**
     * 用户端活动错误提示信息
     */
    JA_NO_START("活动未开始"),
    JA_END("活动已结束"),
    JA_NOT_SET_OPERATION("未设置运营规则"),
    JA_NOT_SET_CONTENT("未设置答题内容"),
    JA_TODAY_NO_TIMES("今日答题次数已满"),
    JA_ALL_NO_TIMES("答题次数已满"),

    LO_NO_TIMES("抽奖次数已用完"),
    LO_CLOSE("抽奖已关闭"),
//    LO_PRIZE_FINISHED("你来晚了\n奖品已经发完了"),
    LO_NOT_SET_PRIZE("未设置奖品");

    private String info;
    ActivityErrorInfoEnum(String info) {
        this.info = info;
    }

    public String getInfo() {
        return info;
    }


}
