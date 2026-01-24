package com.example.webapp.Mapper.college;

import com.example.webapp.DO.CollegeDO;
import com.example.webapp.DO.CollegeUserRefDO;
import com.example.webapp.enums.ClassPositionEnum;
import com.example.webapp.query.UserNumQuery;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.jdbc.SQL;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

public class CollegeMapperDynaSqlProvider {

    /** 修改对象 */
    public String updateCollege(final CollegeDO collegeDO){
        return new SQL(){
            {
                UPDATE("college");
                if(!StringUtils.isEmpty(collegeDO.getName())){
                    SET("name=#{name}");
                }
                if(!StringUtils.isEmpty(collegeDO.getDescription())){
                    SET("description=#{description}");
                }
                if(collegeDO.getParentId()!=null){
                    SET("parent_id=#{parentId}");
                }
                if(collegeDO.getMechanismId() !=null){
                    SET("mechanism_id=#{mechanismId}");
                }
                if(StringUtils.isNotBlank(collegeDO.getIdFullPath())){
                    SET("id_full_path=#{idFullPath}");
                }
                if(StringUtils.isNotBlank(collegeDO.getNameFullPath())){
                    SET("name_full_path=#{nameFullPath}");
                }
                if(collegeDO.getNodeType()!=null){
                    SET("node_type=#{nodeType}");
                }
                if(collegeDO.getSort()!=null){
                    SET("sort=#{sort}");
                }
                if(collegeDO.getChildType()!=null){
                    SET("child_type=#{childType}");
                }
                if(collegeDO.getIsDelete()!=null){
                    SET("is_delete =#{isDelete}");
                }
                WHERE("id=#{id}");
            }
        }.toString();
    }



    public String countUserNumberByNodeId(final UserNumQuery query){
        SQL sql = new SQL(){
            {
                SELECT(" count(distinct cu.user_id,u.user_name) ");
                FROM(" college_user_ref cu  ");
                JOIN(" college c on cu.college_id =c.id ");
                JOIN(" user u on cu.user_id =u.id and u.is_delete =0 and cu.is_delete=0 ");
                WHERE("c.is_delete = 0 ");
                if(ClassPositionEnum.STUDENT.getType().equals(query.getPosition())){
                    //统计学生数量
                    WHERE("cu.position = 0 ");
                }else if(ClassPositionEnum.HEAD_TEACHER.getType().equals(query.getPosition())) {
                    //查询老师助教
                    WHERE("cu.position >0 ");
                }
                WHERE(" (cu.college_id = #{nodeId} or c.id_full_path like #{parentIdFullPath} ) ");
            }
        };
        return sql.toString();
    }

    /**
     * 批量创建班级用户关系
     * @param map
     * @return
     */
    public String insertCollegeUserRefList(Map map) {
        List<CollegeUserRefDO> insertList = (List<CollegeUserRefDO>) map.get("list");
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO college_user_ref ");
        sb.append("(college_id ,user_id,position) ");
        sb.append("VALUES ");
        MessageFormat mf = new MessageFormat(
                "( #'{'list[{0}].collegeId},#'{'list[{0}].userId},#'{'list[{0}].position} )");
        for (int i = 0; i < insertList.size(); i++) {
            sb.append(mf.format(new Object[]{i}));
            if (i < insertList.size() - 1) {
                sb.append(",");
            }
        }
        return sb.toString();
    }
}
