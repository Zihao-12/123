package com.example.webapp.Mapper.college;

import com.example.webapp.DO.CollegeDO;
import com.example.webapp.DO.CollegeUserRefDO;
import com.example.webapp.DO.UserDO;
import com.example.webapp.DTO.CollegeDTO;
import com.example.webapp.DTO.CollegeUserRefDTO;
import com.example.webapp.query.UserNumQuery;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Mapper
public interface CollegeMapper {


    /**
     * 创建院系/班级
     * @param college
     * @return
     */
    @Insert("insert into college (name ,description,parent_id,mechanism_id,id_full_path,name_full_path,node_type,child_type,`sort`) " +
            " values(#{name},#{description},#{parentId},#{mechanismId},#{idFullPath},#{nameFullPath},#{nodeType},#{childType},#{sort})")
    @Options(useGeneratedKeys = true, keyColumn = "id", keyProperty = "id")
    void insertCollege(CollegeDO college);

    @Select("select * from college where id = #{nodeId} and is_delete=0")
    CollegeDO findCollegeNodeById(@Param("nodeId") Integer nodeId);

    @UpdateProvider(type = CollegeMapperDynaSqlProvider.class,method = "updateCollege")
    Integer updateCollege(CollegeDO collegeDO);

    @Select("select * from college where mechanism_id = #{mechanismId} and parent_id =0 and is_delete=0")
    CollegeDO findCollegeRootByMechanismId(@Param("mechanismId") Integer mechanismId);

    @Select("select * from college where parent_id=#{parentId} and name =#{nodeName} and is_delete=0")
    CollegeDO findChildNodeByNodeNameAndParentId(@Param("nodeName") String nodeName, @Param("parentId") Integer parentId);

    @Select("select id,parent_id,id_full_path,name,name_full_path,node_type,child_type,sort,mechanism_id,create_time " +
            "  from college where mechanism_id=#{mechanismId} and is_delete=0")
    List<CollegeDTO> findCollegeNodeByMechanismId(@Param("mechanismId") Integer mechanismId);

    @Select("select id,parent_id,id_full_path,name,name_full_path,node_type,child_type,sort,mechanism_id,create_time " +
            " from college where id_full_path like #{parentIdFullPath} and is_delete=0")
    List<CollegeDTO> findOffspringNodeByParentIdFullPath(@Param("parentIdFullPath") String parentIdFullPath);

    @Select("select id,parent_id,id_full_path,name,name_full_path,node_type,child_type,sort,mechanism_id,create_time " +
            " from college where parent_id =#{parentId} and is_delete=0")
    List<CollegeDTO> findChildNodeByParentId(@Param("parentId") Integer parentId);

    /**
     * 根据父ID全路径删除所有子孙节点
     * @param parentIdFullPath
     * @return
     */
    @Update("update college set is_delete=1 where id_full_path like #{parentIdFullPath} ")
    int delOffspringNodeByParentIdFullPath(String parentIdFullPath);

    /**
     * 查询部门下的人数
     * @param query
     * @return
     */
    @SelectProvider(type= CollegeMapperDynaSqlProvider.class,method="countUserNumberByNodeId")
    Integer countUserNumberByNodeId(UserNumQuery query);

    @Select( " select c.id,c.name,c.parent_id,c.mechanism_id,c.id_full_path,c.name_full_path,c.node_type,c.child_type,c.create_time " +
            " from   college c " +
            " where  c.id_full_path like #{parentIdFullPath} " +
            " and c.node_type =1 and c.is_delete=0;")
    List<CollegeDTO> findClassNodeByPraent(@Param("parentIdFullPath") String parentIdFullPath);

    @Select("SELECT u.id,u.nick_name,u.user_name,u.phone,u.job_number,gender,u.`position`,u.head_img " +
            "     ,u.mechanism_id,u.`status`,u.is_delete,u.update_time,u.create_time " +
            "FROM college_user_ref cu JOIN college c on cu.college_id =c.id " +
            "    JOIN user u on cu.user_id =u.id and u.is_delete =0 and cu.is_delete=0 " +
            "WHERE c.is_delete = 0 AND cu.position = #{position} AND cu.college_id = #{classId}")
    List<UserDO> findUserByClassIdAndPositionType(@Param("classId") Integer classId, @Param("position") Integer position);

    /**
     * 查询用户班级是否绑定过
     * @param collegeId
     * @param userId
     * @return
     */
    @Select("select count(1) from college_user_ref where college_id =#{collegeId} and user_id =#{userId} and is_delete =0 ")
    int countCollegeUser(@Param("collegeId") Integer collegeId, @Param("userId") Integer userId);

    @Insert(" insert into college_user_ref (college_id ,user_id,position,is_delete) values(#{collegeId},#{userId},#{position},0)")
    @Options(useGeneratedKeys = true, keyColumn = "id", keyProperty = "id")
    int insertCollegeUser(CollegeUserRefDTO collegeUserRefDTO);

    @Update("update college_user_ref set is_delete=1 where position=#{position} and college_id=#{collegeId}")
    int delCollegeUserRefByCollegeIdAndPosition(@Param("collegeId") Integer collegeId, @Param("position") Integer position);

    @InsertProvider(type = CollegeMapperDynaSqlProvider.class, method = "insertCollegeUserRefList")
    @Options(useGeneratedKeys = true, keyColumn = "id", keyProperty = "id")
    Integer insertCollegeUserRefList(List<CollegeUserRefDO> collegeUserRefDOList);

    @Update("update college_user_ref set is_delete=1 where user_id=#{userId} and college_id=#{collegeId}")
    Integer detachUserCollegeByCollegeIdAndUserId(@Param("userId") Integer userId, @Param("collegeId") Integer collegeId);

    @Update("<script>"
            + "update college_user_ref set is_delete=1 where   college_id=#{collegeId} and user_id in  "
            + "<foreach item='item' index='index' collection='userIdList' open='(' separator=',' close=')'>"
            + "#{item}"
            + "</foreach>"
            + "</script>")
    Integer bulkDetachUserCollege(@Param("collegeId") Integer collegeId, @Param("userIdList") List<Integer> userIdList);

    /**
     * 获取机构根结点
     * @param mechanismId
     * @return
     */
    @Select("select id,parent_id,id_full_path,name,name_full_path,node_type,child_type,sort,mechanism_id,create_time " +
            "from college where mechanism_id =#{mechanismId} and parent_id =0 and is_delete=0 limit 1")
    CollegeDTO getMechanismCollegeRoot(Integer mechanismId);

}