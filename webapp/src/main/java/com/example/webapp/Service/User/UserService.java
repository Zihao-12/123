package com.example.webapp.Service.User;

import com.example.webapp.DO.UserDO;
import com.example.webapp.Query.UserQuery;
import com.example.webapp.result.Result;
import com.github.pagehelper.PageInfo;

public interface UserService {
    Result insert(UserDO user);
    UserDO findUserByPhone(String phone);
    UserDO getUserByName(String userName);
    PageInfo queryUseryList(UserQuery query);
    Integer updateUserInfo(UserDO u);
    String getRandomUserName();
    UserDO findUserByOpenId(String openId);
    UserDO findUserByReaderBadge(String readerBadge,Integer mechanismId,String dockingType);
    PageInfo queryUserList(UserQuery query);
}
