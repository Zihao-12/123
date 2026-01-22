package com.example.webapp.utils;

import java.io.Serializable;
import java.lang.reflect.Method;

/**
 * Created by gehaisong on 2018/11/23.
 *  @author gehaisong
 */
public class SortField implements Serializable {
    private static final long serialVersionUID = -3386609948297680163L;
    /** 参数：排序字段 */
    private String sortName;
    /** 参数：默认正序 */
    private boolean asc=true;
    /** 属性get方法,由工具类生成 */
    private Method method;

    public String getSortName() {
        return sortName;
    }

    public void setSortName(String sortName) {
        this.sortName = sortName;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }

    public boolean isAsc() {
        return asc;
    }

    public void setAsc(boolean asc) {
        this.asc = asc;
    }
}
