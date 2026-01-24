package com.example.webapp.common.config;

import lombok.Data;

@Data
public class DockingMechConfig {

    /**机构ID*/
    private Integer mechanismId;
    private String mechanismName;
    /** 密钥*/
    private String staticKey;
    /**读者证对接类型 */
    private String dockingType;
    /**读者证认证接口*/
    private String dockingUrl;
}
