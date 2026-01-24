package com.example.webapp.Mapper.MechanismOpen;

import com.example.webapp.DO.MechanismOpenDO;
import com.example.webapp.query.MechanismOpenQuery;
import com.example.webapp.enums.MechanismOpenEnum;
import com.example.webapp.enums.MechanismOpenTypeEnum;
import com.example.webapp.enums.StatusEnum;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.jdbc.SQL;

public class MechanismOpenMapperDynaSqlProvider {

    public String findMechanismOpenList(final MechanismOpenQuery query){
        StringBuilder sb = new StringBuilder();

        SQL sql = new SQL(){
            {
                SELECT("o.id, m.name mechanismName,p.name packageName,(select count(*) from course_package_ref cpr where cpr.course_package_id=p.id and cpr.is_delete=0) courseNumber,o.mechanism_id, o.course_package_id, o.open_days," +
                        "o.begin_time, o.end_time, o.status,o.update_time,o.create_time," +
                        "  timestampdiff(day,curdate(),o.end_time) surplusDays,o.account_number accountNumber");
                FROM("mechanism_open o ");
                JOIN("mechanism m on m.id =o.mechanism_id");
                JOIN("course_package p on p.id =o.course_package_id");
                WHERE("o.is_delete = 0");

                if(!StringUtils.isEmpty(query.getName())){
                    WHERE(" m.name like #{name}");
                }
                if(query.getStatus()!=null&&query.getStatus()> StatusEnum.ALL.getType()){
                    WHERE("o.status=#{status} ");
                }
                if(query.getOpenType()!=null){
                    WHERE("o.open_type=#{openType} ");
                }
                if(MechanismOpenTypeEnum.PRACTICE.getType().equals(query.getOpenType()) &&
                        query.getOpen()!=null && query.getOpen()>= MechanismOpenEnum.ALL.getType()){
                    if(query.getOpen().equals(MechanismOpenEnum.FINISHED.getType())){
                        WHERE(" o.end_time <now()");
                    }else  if(query.getOpen().equals(MechanismOpenEnum.TO_START.getType())){
                        WHERE(" o.begin_time >now()");
                    }else  if(query.getOpen().equals(MechanismOpenEnum.IN_PROGRESS.getType())){
                        WHERE(" o.end_time >= now() and o.begin_time <=now()");
                    }
                }
                ORDER_BY("o.id desc");
            }
        };
        return sql.toString();
    }


    public String  findEffectiveOpenByMechanismId(Integer mechanismId,Integer openType){
        SQL sql = new SQL(){
            {
                SELECT("id,mechanism_id,course_package_id,open_days,begin_time,end_time,status");
                FROM("mechanism_open ");
                WHERE("is_delete = 0");
                if(!MechanismOpenTypeEnum.ALL_OPEN.getType().equals(openType)){
                    WHERE(" open_type=#{openType} ");
                    if(MechanismOpenTypeEnum.PRACTICE.getType().equals(openType)){
                        WHERE(" end_time >= now()");
                    }
                }
                WHERE(" mechanism_id = #{mechanismId}");

            }
        };
        String str=sql.toString()+" limit 1";
        return str;
    }


    /** 修改机构开通表*/
    public String updateMechanismOpen(final MechanismOpenDO meMechanismOpenDO){
        return new SQL(){
            {
                UPDATE("mechanism_open");
                if(meMechanismOpenDO.getMechanismId()!=null){
                    SET("mechanism_id=#{mechanismId}");
                }
                if(meMechanismOpenDO.getCoursePackageId()!=null){
                    SET("course_package_id=#{coursePackageId}");
                }
                if(meMechanismOpenDO.getOpenDays()!=null){
                    SET("open_days=#{openDays}");
                }
                if(meMechanismOpenDO.getBeginTime()!=null){
                    SET("begin_time=#{beginTime}");
                }
                if(meMechanismOpenDO.getEndTime()!=null){
                    SET("end_time=#{endTime}");
                }
                if(meMechanismOpenDO.getAccountNumber()!=null){
                    SET("account_number=#{accountNumber}");
                }
                if(meMechanismOpenDO.getStatus()!=null){
                    SET("status= #{status}");
                }
                if(meMechanismOpenDO.getIsDelete()!=null){
                    SET("is_delete= #{isDelete}");
                }

                WHERE("id=#{id}");
            }
        }.toString();
    }

    public String  findOpenRecordByMechanismId(Integer mechanismId,Integer openType){
        SQL sql = new SQL(){
            {
                SELECT(" * ");
                FROM("mechanism_open ");
                WHERE("is_delete = 0");
                WHERE(" open_type=#{openType} ");
                WHERE(" mechanism_id = #{mechanismId}");
                ORDER_BY(" id desc");

            }
        };
        String str=sql.toString();
        return str;
    }
}
