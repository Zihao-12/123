package com.example.webapp.Service.json;

import com.example.webapp.result.Result;

public interface JsonService {

    /**
     * 查询所有子节点
     * @return
     */
    Result generateStaticJson();

    Result procesds(int delCache);
}

