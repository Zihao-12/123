package com.example.webapp.Mapper.user;


import com.example.webapp.DO.UserDO;
import com.example.webapp.query.UserQuery;
import com.example.webapp.common.Constant;
import com.example.webapp.enums.StatusEnum;
import com.example.webapp.enums.UserRoleEnum;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.jdbc.SQL;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

@Service
@Mapper
public interface UserMapper {


    /**
     * 保存用户
     * @param userDO
     * @return
     */
    @Insert("insert into user (user_name, password, nick_name, phone, gender,age,head_img,open_id,union_id,docking_type,reader_badge,pinyin_acronym,mechanism_id,type,job_number,position) " +
            " values(#{userName},#{password},#{nickName},#{phone},#{gender},#{age},#{headImg},#{openId},#{unionId},#{dockingType},#{readerBadge},#{pinyinAcronym},#{mechanismId},#{type},#{jobNumber},#{position})")
    @Options(useGeneratedKeys = true, keyColumn = "id", keyProperty = "id")
    Integer insertUser(UserDO userDO);

    /**
     * 批量保存用户
     * @param userDOList
     * @return
     */
    @InsertProvider(type = UserMapperDynaSqlProvider.class, method = "insertUserList")
    @Options(useGeneratedKeys = true, keyColumn = "id", keyProperty = "id")
    Integer insertUserList(List<UserDO> userDOList);

    /**
     * 更新用户
     * @param u
     * @return
     */
    @UpdateProvider(type = UserMapperDynaSqlProvider.class,method = "update")
    Integer update(UserDO u);
    /**
     * 用户分页查询
     * @param query
     * @return
     */
    @SelectProvider(type= UserMapperDynaSqlProvider.class,method="findUserList")
    List<UserDO> findUserList(UserQuery query);

    /**
     * 手机查询用户
     * @param phone
     * @return
     */
    @Select(" select u.*,m.`name` mechanismName ,m.show_name,m.app_show_name,m.login_logo,m.app_login_logo " +
            "  from user u " +
            "  LEFT JOIN mechanism m on  m.id = u.mechanism_id " +
            " where u.is_delete=0 and u.phone=#{phone}  and u.phone <>'' and u.phone is not null ")
    UserDO findUserByPhone(@Param("phone") String phone);

    @Select(" select u.*,m.`name` mechanismName ,m.show_name,m.app_show_name,m.login_logo,m.app_login_logo  " +
            "  from user u " +
            "  LEFT JOIN mechanism m on  m.id = u.mechanism_id " +
            " where u.is_delete=0 and u.open_id=#{openid}  ")
    UserDO findUserByOpenId(String openid);

    /**
     * 根据读者证号查询用户
     * @param readerBadge
     * @param mechanismId
     * @param dockingType
     * @return
     */
    @Select(" select u.*,m.`name` mechanismName ,m.show_name,m.app_show_name,m.login_logo,m.app_login_logo  " +
            "  from user u" +
            "  LEFT JOIN mechanism m on  m.id = u.mechanism_id " +
            " where u.is_delete=0 and u.reader_badge=#{readerBadge} " +
            "  and u.mechanism_id=#{mechanismId} and u.docking_type=#{dockingType}  ")
    UserDO findUserByReaderBadge(String readerBadge, Integer mechanismId, String dockingType);

    /**
     * 用户名查询用户
     * @param userName
     * @return
     */
    @Select(" select id,user_name, password,nick_name, phone, gender,age,head_img,docking_type,reader_badge,pinyin_acronym,mechanism_id,type,create_time,job_number,position,`status`,is_delete" +
            "  from user where is_delete=0 and user_name=#{userName} ")
    UserDO getUserByName(String userName);

    /**
     * 主键查询用户
     * @param id
     * @return
     */
    @Select(" select id,user_name, password,nick_name, phone, gender,age,head_img,docking_type,reader_badge,pinyin_acronym,mechanism_id,type,create_time,job_number,position,`status`,is_delete" +
            "  from user where is_delete=0 and id=#{id} ")
    UserDO findUserById(@Param("id") Integer id);

    /**
     * 机构工号查询用户
     * @param mechanismId
     * @param jobNumber
     * @return
     */
    @Select("select * from user where is_delete=0 and mechanism_id =#{mechanismId} and job_number =#{jobNumber} ")
    UserDO findUserByMechanismIdAndJobNumber(@Param("mechanismId") Integer mechanismId, @Param("jobNumber") String jobNumber);

    @Select("select id,`name`,nick_name,job_number,phone,`status`,head_img,gender,age from co_user  " +
            "where job_number= #{jobNumber} and mechanism_id =#{mechanismId} and is_delete=0")
    UserDO findUserByJobNumber(Integer mechanismId, String jobNumber);

    /**
     * 批量删除用户
     * @param userIdList
     * @return
     */
    @UpdateProvider(type= UserMapperDynaSqlProvider.class,method="batchDeleteUserByIdList")
    int batchDeleteUserByIdList(@Param("list") List<Integer> userIdList);

    /**
     * 批量停用用户
     * @param userIdList
     * @return
     */
    @UpdateProvider(type= UserMapperDynaSqlProvider.class,method="batchDeactivateUserByIdList")
    int batchDeactivateUserByIdList(@Param("list") List<Integer> userIdList);

}