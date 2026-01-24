package com.example.webapp.bms.controller;

import com.example.webapp.DO.UserDO;
import com.example.webapp.query.UserQuery;
import com.example.webapp.Service.User.UserService;
import com.example.webapp.result.Result;
import com.example.webapp.result.ResultPage;
import com.github.pagehelper.PageInfo;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@RequestMapping("/api/bms/user")
public class UserController {
    private UserService userService;

    /**
     * 查询所有用户
     * @param query
     * @return
     */
    @PostMapping(value = "query-list")
    public ResultPage<List<UserDO>> querUserList(@RequestBody UserQuery query){
        PageInfo page = userService.queryUserList(query);
        if(page==null){
            return ResultPage.fail("暂无数据");
        }
        return ResultPage.ok(page.getList(),page.getPageNum(),page.getPageSize(),page.getTotal());
    }

    /**
     * 新建用户
     * @param userDO
     * @return
     */
    public Result add(@RequestBody UserDO userDO){
        Result result = userService.insert(userDO);
        return result;
    }
    //
}
