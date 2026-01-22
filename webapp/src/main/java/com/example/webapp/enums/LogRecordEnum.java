package com.example.webapp.enums;

public enum LogRecordEnum {
    /**
     * 类型：类型：1.书房(buy-course-list)
     *           2.课程详情(course/view)
     *           3.活动（含详情）(activity/list && /view )
     *           4.排行榜(/ranking-list)
     *           5.我的(/common/my-info) ',
     *           6.课程分类听课时长
     *           7.课程年龄听课时长
     *           8.总听课时长
     */
    SHU_FANG(1,"书房"),
    COURSE_XQ(2,"课程详情"),
    HUO_DONG(3,"活动"),
    RANKING(4,"排行榜"),
    MY(5,"我的"),
    COURSE_FL(6,"课程分类"),
    COURSE_NL(7,"课程年龄"),
    COURSE_TM(8,"总听课时长");

    private Integer type;
    private String name;
    LogRecordEnum(Integer type,String name) {
        this.name = name;
        this.type =type;
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
}
