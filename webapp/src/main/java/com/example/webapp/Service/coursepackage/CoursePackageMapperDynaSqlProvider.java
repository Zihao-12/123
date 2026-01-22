package com.example.webapp.Service.coursepackage;

import com.example.webapp.DO.CoursePackageDO;
import com.example.webapp.DO.CoursePackageRefDO;
import com.example.webapp.Query.CoursePackageQuery;
import com.example.webapp.enums.CoursePackageStatusEnum;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.jdbc.SQL;
import org.springframework.util.CollectionUtils;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;


public class CoursePackageMapperDynaSqlProvider {

    public String selectAll(final CoursePackageQuery query) {
        SQL sql = new SQL() {
            {
                SELECT(" cp.*,(select count(*) from course_package_ref cpr where cpr.course_package_id=cp.id and cpr.is_delete=0) courseNumber");
                FROM(" course_package cp ");
                if (StringUtils.isNotBlank(query.getName())) {
                    WHERE(" cp.name like #{name}");
                }
                if (query.getUsed() != null && query.getUsed() >= 0) {
                    WHERE(" cp.used = #{used}");
                }
                if (query.getType() != null && query.getType() >= 0) {
                    WHERE(" cp.type = #{type}");
                }
                WHERE(" cp.is_delete=0 ");
                ORDER_BY("cp.id desc");
            }
        };
        return sql.toString();
    }

    public String selectList(final CoursePackageQuery query) {
        SQL sql = new SQL() {
            {
                SELECT(" cp.*,(select count(*) from course_package_ref cpr where cpr.course_package_id=cp.id and cpr.is_delete=0) courseNumber ");
                FROM(" course_package cp ");
                if (StringUtils.isNotBlank(query.getName())) {
                    WHERE(" cp.name like #{name}");
                }
                WHERE(" cp.used =  "+ CoursePackageStatusEnum.NOT_USED.status);
                if (query.getType() != null && query.getType() >= 0) {
                    WHERE(" cp.type = #{type}");
                }
                if (CollectionUtils.isNotEmpty(query.getCoursePackageIdList())) {
                    String ids = StringUtils.join(query.getCoursePackageIdList(), ",");
                    WHERE(" cp.id not in (" + ids + ") ");
                }
                WHERE(" cp.is_delete=0 ");
                ORDER_BY(" cp.id desc");
            }
        };
        return sql.toString();
    }

    public String update(final CoursePackageDO meCoursePackageDO) {
        return new SQL() {
            {
                UPDATE("course_package");
                SET("name=#{name}");
                SET(" introduction=#{introduction}");
                if (meCoursePackageDO.getTryStatus() != null) {
                    SET(" try_status=#{tryStatus}");
                }
                WHERE(" id=#{id}");
            }
        }.toString();
    }

    public String insertCoursePackageRef(Map map) {
        List<CoursePackageRefDO> coursePackageRefList = (List<CoursePackageRefDO>) map.get("list");
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO `course_package_ref`( `course_id`, `course_package_id`, `sort`, `is_delete`, `create_time`)  ");
        sb.append(" VALUES ");
        MessageFormat mf = new MessageFormat(
                "( #'{'list[{0}].courseId},#'{'list[{0}].coursePackageId},#'{'list[{0}].sort} ,0,now()   )");
        for (int i = 0; i < coursePackageRefList.size(); i++) {
            sb.append(mf.format(new Object[]{i}));
            if (i < coursePackageRefList.size() - 1) {
                sb.append(",");
            }
        }
        return sb.toString();
    }



}
