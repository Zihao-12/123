package com.example.webapp.mechanism.controller;

import com.example.webapp.common.Constant;

public class BaseMechController {

    /**
     * 判断机构是否有权限
     * @param loginMid  登录机构ID     0  默认机构ID 东方易读图书馆
     * @param objMid    对象所属结构ID  0 为运营创建
     * @return
     */
    protected boolean isBelongMechanism(int loginMid, int objMid) {
        if (loginMid > Constant.YUNYING_MECHANISM_ID && loginMid == objMid) {
            return true;
        }
        return false;
    }
}
