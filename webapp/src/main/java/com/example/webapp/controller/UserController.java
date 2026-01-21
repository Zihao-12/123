package com.example.webapp.controller;

import com.example.webapp.DO.UserDO;
import com.example.webapp.Query.UserQuery;
import com.example.webapp.Service.UserService;
import com.example.webapp.result.ResultPage;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RequestMapping("/api/bms/user")
public class UserController {
    private UserService userService;

    public ResultPage<List<UserDO>> querUserList(@RequestBody UserQuery query){
        PageInfo page = userService.queryUserList(query);
        if(page==null){
            return ResultPage.fail("暂无数据");
        }
        return ResultPage.ok(page.getList(),page.getPageNum(),page.getPageSize(),page.getTotal());
    }

    //
}
