package com.example.webapp.Mapper;


import com.example.webapp.DO.UserDO;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Service;

@Service
@Mapper
public class UserMapper {
    public Integer insertUser(UserDO userDO) {
        return null;
    }
}
