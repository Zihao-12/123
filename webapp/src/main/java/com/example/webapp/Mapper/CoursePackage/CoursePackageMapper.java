package com.example.webapp.Mapper.CoursePackage;

import com.example.webapp.DO.CoursePackageDO;
import com.example.webapp.DO.CoursePackageRefDO;
import com.example.webapp.DO.StUserLearnRecordDetailDOIP;
import com.example.webapp.DTO.CourseDTO;
import com.example.webapp.DTO.CoursePackageDTO;
import com.example.webapp.query.CoursePackageQuery;
import com.example.webapp.query.CourseQuery;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@Mapper
public interface CoursePackageMapper {

    /**
     * 套餐列表
     * @param query
     * @return
     */
    @Results(id = "coursePackageResultsMap",value = {
            @Result(id=true,property="id",column="id"),
            @Result(property="name",column="name")
    })
    @SelectProvider(type= CoursePackageMapperDynaSqlProvider.class,method="selectAll")
    List<CoursePackageDTO> selectAll(CoursePackageQuery query);

    @SelectProvider(type= CoursePackageMapperDynaSqlProvider.class,method="selectList")
    List<CoursePackageDTO> selectList(CoursePackageQuery query);

    @Insert("INSERT INTO `course_package`(`name`, `introduction`, `type`, `status`, `used`) " +
            " values(#{name},#{introduction},#{type},#{status},#{used})")
    @Options(useGeneratedKeys = true, keyColumn = "id", keyProperty = "id")
    int insert(CoursePackageDO meCoursePackageDO);

    @UpdateProvider(type = CoursePackageMapperDynaSqlProvider.class,method = "update")
    int update(CoursePackageDO meCoursePackageDO);

    @Update("update course_package set is_delete=1 where id=#{id}")
    int delete(int id);

    @Update("update course_package set used=#{used} where id=#{id}")
    int updateUse(int id, int used);

    @Select("select * from course_package where id=#{id}")
    CoursePackageDTO view(int id);

    @Update("update course_package_ref set is_delete=1 where course_id=#{courseId} and course_package_id=#{packageId}")
    int disassociate(Integer packageId, Integer courseId);

    @Update("update course_package_ref set is_delete=1 where course_id=#{courseId} ")
    int disassociateByCourseId(Integer id);

    @InsertProvider(type = CoursePackageMapperDynaSqlProvider.class,method = "insertCoursePackageRef")
    int insertCoursePackageRef(List<CoursePackageRefDO> coursePackageRefList);

    @Select(" select c.*,cpr.sort " +
            " from course_package_ref cpr " +
            " INNER JOIN course c on c.id=cpr.course_id " +
            " where cpr.is_delete=0 and c.is_delete=0 and cpr.course_package_id=#{packageId} " +
            " order by cpr.sort asc ")
    List<CourseDTO> viewCoursePackageRef(Integer packageId);

    @Select(" select distinct cpr.course_package_id from course_package_ref cpr  where cpr.course_id=#{courseId} and cpr.is_delete=0 ")
    List<Integer> listPackageIdByCourseId(@Param("courseId") Integer courseId);

    @Update("update course_package set status=#{status} where id=#{id} and is_delete=0")
    int updateSale(Integer id, Integer status);
    @Update("update course_package set try_status=#{tryStatus} where id=#{id} and is_delete=0")
    int updateTrySale(Integer id, Integer tryStatus);

    @Select(" select cp.* from course_package_ref cpr  " +
            " INNER JOIN course_package cp on cp.id=cpr.course_package_id " +
            " where cpr.course_id=#{courseId} and cpr.is_delete=0 and cp.is_delete=0 ")
    List<CoursePackageDTO> listPackageByCourseId(Integer courseId);

    @Update({
            "<script> ",
            "update course_package_ref set is_delete=1 ",
            "<where> ",
            " course_id=#{courseId} and course_package_id in ",
            "<foreach collection='deleteList' index='index' item='item' open='(' separator=',' close=')'> " ,
            "#{item} ",
            "</foreach>",
            "</where> ",
            "</script>"
    })
    int deleteCoursePackageRefList(int courseId, List<Integer> deleteList);

    @Update("update course_package_ref set is_delete=1 where course_package_id=#{packageId}")
    int deleteCoursePackageRef(Integer packageId);


    @Select(" select * from course_package_ref cpr where cpr.is_delete=0 and cpr.course_package_id=#{coursePackageId}")
    List<CoursePackageRefDO> listCoursePackageRef(int coursePackageId);

    @Select(" select course_id from course_package_ref cpr where cpr.is_delete=0 and cpr.course_package_id=#{coursePackageId}")
    List<Integer> getCourseIdListByPackageId(Integer coursePackageId);
}