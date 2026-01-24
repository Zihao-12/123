package com.example.webapp.utils;

import lombok.Data;

import java.io.Serializable;
import java.util.List;


@Data
public class ListOperateDTO<E> implements Serializable {
    private static final long serialVersionUID = -8630407503903114716L;
	//删除
	List<E> deleteList;
	//新增
	List<E> addList;
}
