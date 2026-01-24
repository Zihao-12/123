package com.example.webapp.Mapper.Mechanism;

import com.example.webapp.DO.FakeDateSetDO;
import com.example.webapp.DO.MechanismContactPersonDO;
import com.example.webapp.DO.MechanismDO;
import com.example.webapp.DO.MechanismRestrictIpDO;
import com.example.webapp.DTO.MechanismContactPersonDTO;
import com.example.webapp.DTO.MechanismDTO;
import com.example.webapp.DTO.MechanismRestrictIpDTO;
import com.example.webapp.query.IpQuery;
import com.example.webapp.query.MechanismQuery;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Mapper
public interface MechanismMapper {

    /**
     * 创建机构
     * @param mechanism
     * @return
     */
    @Insert("insert into mechanism (name ,account,password,attribute,province,city,address,show_name,app_login_logo,app_navbar_logo,app_domain,app_show_name,domain,navbar_logo,login_logo) " +
            " values(#{name},#{account},#{password},#{attribute},#{province},#{city},#{address},#{showName},#{appLoginLogo},#{appNavbarLogo},#{appDomain},#{appShowName},#{domain},#{navbarLogo},#{loginLogo})")
    @Options(useGeneratedKeys = true, keyColumn = "id", keyProperty = "id")
    Integer insertMechanism(MechanismDO mechanism);

    @UpdateProvider(type = MechanismMapperDynaSqlProvider.class,method = "updateMechanism")
    Integer updateMechanism(MechanismDO mechanism);

    /**
     * 保存机构联系人
     * @param contactPersonList
     * @return
     */
    @InsertProvider(type = MechanismMapperDynaSqlProvider.class, method = "insertContactPersonList")
    @Options(useGeneratedKeys = true, keyColumn = "id", keyProperty = "id")
    Integer insertContactPersonList(List<MechanismContactPersonDO> contactPersonList);


    /**
     * 更新机构联系人
     * @param contactPersonList
     * @return
     */
    @Update({
            "<script>"+
                    "<foreach collection='list' item='item' index='index' separator=';'>"+
                    "update mechanism_contact_person "+
                    "set is_delete = #{item.isDelete},name= #{item.name},phone= #{item.phone},email= #{item.email},remark= #{item.remark}  where id= #{item.id}"+
                    "</foreach>"+
                    "</script>"
    })
    Integer updateContactPersonList(@Param(value = "list") List<MechanismContactPersonDO> contactPersonList);

    /**
     * 机构分页查询
     * @param query
     * @return
     */
    @SelectProvider(type= MechanismMapperDynaSqlProvider.class,method="findMechanismList")
    List<MechanismDTO> findMechanismList(MechanismQuery query);

    @Select("select id,name,account,attribute,province,city,address,show_name,app_login_logo,app_navbar_logo,app_domain,app_show_name,domain,navbar_logo,login_logo,ip_restrict,create_time,update_time,is_delete " +
            " from mechanism where id=#{mechanismId}")
    MechanismDTO findMechanismById(Integer mechanismId);

    @Select("select id,name,account,attribute,province,city,address,show_name,app_login_logo,app_navbar_logo,app_domain,app_show_name,domain,navbar_logo,login_logo,ip_restrict,create_time,update_time,is_delete " +
            " from mechanism where account=#{account}")
    MechanismDTO findMechanismByccount(String account);

    @Select("select id,name,account,attribute,province,city,address,show_name,app_login_logo,app_navbar_logo,app_domain,app_show_name,domain,navbar_logo,login_logo,ip_restrict,create_time,update_time,is_delete " +
            " from mechanism where name=#{name} and is_delete=0 limit 1")
    MechanismDTO findMechanismByName(String name);

    /**
     * 根据机构ID查询联系人
     * @param mechanismId
     * @return
     */
    @Results(id = "mechanismContactPersonResultsMap",value = {
            @Result(id=true,property="id",column="id"),
            @Result(property="mechanismId",column="mechanism_id"),
            @Result(property="name",column="name"),
            @Result(property="phone",column="phone"),
            @Result(property="email",column="email"),
            @Result(property="remark",column="remark"),
            @Result(property="createTime",column="create_time"),
            @Result(property="updateTime",column="update_time"),
            @Result(property="isDelete",column="is_delete"),
    })
    @Select("select id,mechanism_id,name,phone,email,remark,create_time,update_time,is_delete " +
            " from mechanism_contact_person where is_delete =0 and mechanism_id=#{mechanismId}")
    List<MechanismContactPersonDTO> findContactPersonByMechanismId(Integer mechanismId);

    /**
     * 删除不在修改列表的联系人(修改列表空则全删)
     * @param updateIdList
     * @return 影响行数
     */
    @UpdateProvider(type= MechanismMapperDynaSqlProvider.class,method="delPersonOfNotInUpdateList")
    int delPersonOfNotInUpdateList(@Param("mechanismId") Integer mechanismId, @Param("list") List<Integer> updateIdList);

    /**
     * 删除机构
     * @param id
     * @return
     */
    @Update("update mechanism set is_delete=1 where id=#{id}")
    int deleteMechanism(Integer id);


    /**
     * 是否限制IP
     * @param mid
     * @param restrict
     * @return
     */
    @Update("update mechanism set ip_restrict=#{restrict} where id=#{mid}")
    int restrictIp(Integer mid, Integer restrict);

    /**
     * 添加IP
     * @param restrictIpDO
     * @return
     */
    @Insert("insert into mechanism_restrict_ip (ip ,mechanism_id ) " +
            " values(#{ip},#{mechanismId} )")
    @Options(useGeneratedKeys = true, keyColumn = "id", keyProperty = "id")
    int insertIp(MechanismRestrictIpDO restrictIpDO);

    /**
     * 删除机构限制IP
     * @param ipId
     * @return
     */
    @Update("update mechanism_restrict_ip set is_delete=1 where id=#{id}")
    Integer deleteIp(Integer ipId);

    @Select("select id,ip,mechanism_id,create_time " +
            " from mechanism_restrict_ip where is_delete =0 and mechanism_id=#{mechanismId}")
    List<MechanismRestrictIpDTO> ipList(IpQuery query);

    @Select("SELECT mechanism_id,fakeset from mechanism_fake_set WHERE mechanism_id = #{mechanismId}")
    FakeDateSetDO getFakeset(@Param("mechanismId") Integer mechanismId);

    @Insert("insert into mechanism_fake_set (mechanism_id,fakeset )   values(#{mechanismId},#{fakeset})")
    @Options(useGeneratedKeys = true, keyColumn = "id", keyProperty = "id")
    Integer saveFakeset(FakeDateSetDO fakeDateSetDO);

    @Update("UPDATE mechanism_fake_set SET fakeset =#{fakeset} WHERE mechanism_id =#{mechanismId}")
    Integer updateFakeset(FakeDateSetDO fakeDateSetDO);
}