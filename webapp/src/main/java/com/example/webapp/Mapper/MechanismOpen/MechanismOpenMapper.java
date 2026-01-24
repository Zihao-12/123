package com.example.webapp.Mapper.MechanismOpen;

import com.example.webapp.DO.MechanismOpenDO;
import com.example.webapp.DO.MechanismOpenDelayRecordDO;
import com.example.webapp.DTO.CoursePackageDTO;
import com.example.webapp.DTO.MechanismOpenDTO;
import com.example.webapp.Query.MechanismOpenQuery;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Mapper
public interface MechanismOpenMapper {

    /**
     * 机构开通创建
     * @return
     */
    @Insert("insert into mechanism_open" +
            " (mechanism_id ,course_package_id,open_days,begin_time,end_time,account_number,status,open_type) " +
            " values(#{mechanismId},#{coursePackageId},#{openDays},#{beginTime},#{endTime},#{accountNumber},#{status},#{openType})")
    @Options(useGeneratedKeys = true, keyColumn = "id", keyProperty = "id")
    Integer insertMechanismOpen(MechanismOpenDO mechanismOpen);

    /**
     * 机构开通分页查询
     * @param query
     * @return
     */
    @SelectProvider(type= MechanismOpenMapperDynaSqlProvider.class,method="findMechanismOpenList")
    List<MechanismOpenDTO> findMechanismOpenList(MechanismOpenQuery query);


    /**
     * 查询机构未过期开通
     * @param mechanismId
     * @param openType 开通类型 0实训 1微软
     * @return
     */
    @SelectProvider(type= MechanismOpenMapperDynaSqlProvider.class,method="findEffectiveOpenByMechanismId")
    MechanismOpenDO findEffectiveOpenByMechanismId(Integer mechanismId, Integer openType);

    /**
     * 修改机构开通
     * @param meMechanismOpenDO
     * @return
     */
    @UpdateProvider(type = MechanismOpenMapperDynaSqlProvider.class,method = "updateMechanismOpen")
    Integer updateMechanismOpen(MechanismOpenDO meMechanismOpenDO);


    /**
     * 根据ID查询机构开通对象
     * @param mechanismOpenId
     * @return
     */
    @Select("SELECT  o.id, m.name mechanismName,p.name packageName,(select count(*) from course_package_ref cpr where cpr.course_package_id=p.id and cpr.is_delete=0) courseNumber,o.mechanism_id, o.course_package_id, o.open_days, o.begin_time, o.end_time, " +
            "  o.status,o.update_time,o.create_time,o.account_number accountNumber," +
            " timestampdiff(day,curdate(),o.end_time) surplusDays ,o.open_type openType" +
            " FROM mechanism_open o " +
            " JOIN mechanism m on m.id =o.mechanism_id " +
            " JOIN course_package p on p.id =o.course_package_id " +
            " WHERE o.id = #{mechanismOpenId}")
    MechanismOpenDTO findMechanismOpenById(Integer mechanismOpenId);

    @Select("select * from mechanism_open where id =#{mechanismOpenId} and is_delete=0")
    MechanismOpenDTO findMechanismOpen(Integer mechanismOpenId);

    /**
     * 获取已开通到机构ID
     * @return
     */
    @Select("SELECT DISTINCT mechanism_id FROM mechanism_open WHERE is_delete =0 ")
    List<Integer> getOpenMechanismIdList();
    /**
     * 保存延期记录
     * @return
     */
    @Insert("insert into mechanism_open_delay_record" +
            " (mechanism_open_id ,delay_days,begin_time,end_time,last_open_days,last_end_time,is_delay,description) " +
            " values(#{mechanismOpenId},#{delayDays},#{beginTime},#{endTime},#{lastOpenDays},#{lastEndTime},#{isDelay},#{description})")
    @Options(useGeneratedKeys = true, keyColumn = "id", keyProperty = "id")
    Integer insertMechanismOpenDelayRecord(MechanismOpenDelayRecordDO delayRecord);

    @Select("select * from course_package where id =#{packageId} and is_delete =0 ")
    CoursePackageDTO findCoursePackageById(Integer packageId);


    /**
     * 查询几天开通的课程包ID
     * @param mechanismId
     * @param openType
     * @return
     */
    @Select("select course_package_id  from mechanism_open where open_type=#{openType} and mechanism_id = #{mechanismId} order by id desc limit 1 ")
    Integer getCoursePackageIdOfMechanismOpen(Integer mechanismId, Integer openType);

    /**
     * 查询机构开通记录
     * @param mechanismId
     * @param openType 开通类型 0实训 1微软
     * @return
     */
    @SelectProvider(type= MechanismOpenMapperDynaSqlProvider.class,method="findOpenRecordByMechanismId")
    List<MechanismOpenDO> findOpenRecordByMechanismId(Integer mechanismId, Integer openType);

}