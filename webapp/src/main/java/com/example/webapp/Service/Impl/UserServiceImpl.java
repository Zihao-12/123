package com.example.webapp.Service.Impl;

import com.example.webapp.DO.UserDO;
import com.example.webapp.Mapper.UserMapper;
import com.example.webapp.Query.UserQuery;
import com.example.webapp.Service.UserService;
import com.example.webapp.common.Constant;
import com.example.webapp.enums.GenderTypeEnum;
import com.example.webapp.enums.UserRoleEnum;
import com.example.webapp.result.Result;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.Serializable;

@Service
@Slf4j
public class UserServiceImpl implements UserService,Serializable{
    public static final int LENGTH_20 = 20;
    public static final int LENGTH_15 = 15;
    public static final int RANDOM_COUNT = 50;

    @Autowired
    private UserMapper userMapper;

    @Override
    public Result insert(UserDO user){
        try {
            user.setGender(GenderTypeEnum.parseGender(user.getGender()));
            user.setPosition(UserRoleEnum.NO_ROLE.getType());
            user.setType(Constant.USER_TYPE);
            userMapper.insertUser(user);
        }catch (Exception e){
            log.error("save error");
            throw e;
        }
        return  Result.ok(user.getId());
    }

    @Override
    public UserDO findUserByPhone(String phone) {
        return null;
    }

    @Override
    public UserDO getUserByName(String userName) {
        return null;
    }

    @Override
    public PageInfo queryUseryList(UserQuery query) {
        return null;
    }

    @Override
    public Integer updateUserInfo(UserDO u) {
        return null;
    }

    @Override
    public String getRandomUserName() {
        return null;
    }

    @Override
    public UserDO findUserByOpenId(String openId) {
        return null;
    }

    @Override
    public UserDO findUserByReaderBadge(String readerBadge, Integer mechanismId, String dockingType) {
        return null;
    }

    @Override
    public PageInfo queryUserList(UserQuery query) {
        return null;
    }
}
