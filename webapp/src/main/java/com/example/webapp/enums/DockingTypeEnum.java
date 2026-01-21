package com.example.webapp.enums;

public enum DockingTypeEnum {
    /**
     *
     */
    TU_CHUANG("TU_CHUANG","图创对接"),
    SHOU_DU_TU_SHU_GUAN("SHOU_DU_TU_SHU_GUAN","首都图书馆对接");
    private String dockingType;
    private String name;
    DockingTypeEnum(String dockingType, String name){
        this.dockingType=dockingType;
        this.name=name;
    }

    public String getDockingType() {
        return dockingType;
    }

    public void setDockingType(String dockingType) {
        this.dockingType = dockingType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    /**
     * 根据枚举值获取枚举对象
     * @param dockingType
     * @return
     */
    public static DockingTypeEnum getRankingEnumByOptionType(String dockingType){
        for(DockingTypeEnum operationEnum : DockingTypeEnum.values()){
            if(operationEnum.getDockingType().equals(dockingType)){
                return operationEnum;
            }
        }
        return null;
    }

}