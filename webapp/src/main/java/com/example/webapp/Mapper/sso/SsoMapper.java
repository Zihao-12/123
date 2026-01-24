package com.example.webapp.Mapper.sso;


import com.example.webapp.DO.MechanismDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Service;

@Service
@Mapper
public interface SsoMapper {

    @Select("select * from mechanism where account =#{account} and is_delete=0")
    MechanismDO getMechanismByAccount(@Param("account")  String account);


}