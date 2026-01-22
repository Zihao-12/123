package com.example.webapp.Mapper;


import com.example.webapp.DO.UserDO;
import com.example.webapp.Query.UserQuery;
import com.example.webapp.common.Constant;
import com.example.webapp.enums.StatusEnum;
import com.example.webapp.enums.UserRoleEnum;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.jdbc.SQL;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

@Service
@Mapper
public class UserMapper {
    /**
     * 保存用户
     *
     * @param userDO
     * @return
     */
    @Insert("insert into user (user_name, password, nick_name, phone, gender,age,head_img,open_id,union_id,docking_type,reader_badge,pinyin_acronym,mechanism_id,type,job_number,position) " +
            " values(#{userName},#{password},#{nickName},#{phone},#{gender},#{age},#{headImg},#{openId},#{unionId},#{dockingType},#{readerBadge},#{pinyinAcronym},#{mechanismId},#{type},#{jobNumber},#{position})")
    @Options(useGeneratedKeys = true, keyColumn = "id", keyProperty = "id")
    public Integer insertUser(UserDO userDO) {
        return null;
    }

    @SelectProvider(type= UserMapperDynaSqlProvider.class,method="findUserList")
    public List<UserDO> findUserList(UserQuery query) {
        return null;
    }

    public static class UserMapperDynaSqlProvider {


        public String findUserList(final UserQuery query){
            SQL sql = new SQL(){
                {
                    SELECT("  id,user_name, nick_name, phone, gender,age,head_img,docking_type,reader_badge,pinyin_acronym,mechanism_id,type,`status`,create_time  ,job_number,position,`status`");
                    FROM("user ");
                    WHERE("is_delete = 0 ");
                    if(StringUtils.isNotEmpty(query.getNickName())){
                        WHERE(" nick_name like #{nickName}");
                    }
                    if(StringUtils.isNotBlank(query.getPhone())){
                        WHERE(" phone = #{phone}");
                    }
                    if(query.getBeginTime()!=null && query.getEndTime()!=null){
                        WHERE(" create_time > #{beginTime} and create_time < #{endTime} ");
                    }
                    if(query.getMechanismId()!=null){
                        WHERE(" mechanism_id = #{mechanismId} ");
                    }else {
                        WHERE(" mechanism_id = 0 ");
                    }
                    if(query.getPosition() != null){
                        if(Constant.TEACHER.equals(query.getPosition())){
                            //老师
                            WHERE("position in (1,2) ");
                        }else {
                            WHERE(" position = #{position}");
                        }
                    }
                    if(query.getStatus() != null && query.getStatus()> StatusEnum.ALL.getType()){
                        WHERE(" status = #{status}");
                    }
                    if(query.getCollegeId()!=null && query.getCollegeId()>0 && UserRoleEnum.STUDENT.getType().equals(query.getPosition())){
                        //学生选择绑定班级列表排除已绑定该班级的学生
                        WHERE(" id not in (select user_id from college_user_ref where college_id =25 and position =0 and is_delete=0) ");
                    }
                    ORDER_BY("id desc");
                }
            };
            return sql.toString();
        }

        public String update(final UserDO userDO) {
            return new SQL() {
                {
                    UPDATE(" user ");
                    if(StringUtils.isNotBlank(userDO.getNickName())){
                        SET("nick_name=#{nickName}");
                    }
                    if(StringUtils.isNotBlank(userDO.getPassword())){
                        SET("password=#{password}");
                    }
                    if(StringUtils.isNotBlank(userDO.getPhone())){
                        SET("phone=#{phone}");
                    }
                    if(userDO.getGender() != null){
                        SET("gender=#{gender}");
                    }
                    if(userDO.getAge() != null){
                        SET("age=#{age}");
                    }
                    if(StringUtils.isNotBlank(userDO.getHeadImg())){
                        SET("head_img=#{headImg}");
                    }
                    if(StringUtils.isNotBlank(userDO.getOpenId())){
                        SET("open_id=#{openId}");
                    }
                    if(StringUtils.isNotBlank(userDO.getUnionId())){
                        SET("union_id=#{unionId}");
                    }
                    if(userDO.getDockingType() != null){
                        SET("docking_type=#{dockingType}");
                    }
                    if(userDO.getReaderBadge() != null){
                        SET("reader_badge=#{readerBadge}");
                    }
                    if(userDO.getPinyinAcronym() != null){
                        SET("pinyin_acronym=#{pinyinAcronym}");
                    }
                    if(userDO.getMechanismId() != null){
                        SET("mechanism_id=#{mechanismId}");
                    }
                    if(userDO.getJobNumber() != null){
                        SET("job_number=#{jobNumber}");
                    }
                    if(userDO.getPosition() != null){
                        SET("position=#{position}");
                    }

                    if(userDO.getType() != null){
                        SET("type=#{type}");
                    }
                    if(userDO.getStatus() != null){
                        SET("status=#{status}");
                    }
                    WHERE("id=#{id}");
                }
            }.toString();
        }

        /**
         * 批量保存用户
         * @param map
         * @return
         */
        public String insertUserList(Map map) {
            List<UserDO> insertList = (List<UserDO>) map.get("list");
            StringBuilder sb = new StringBuilder();
            sb.append("INSERT INTO user ");
            sb.append(" (user_name, password, nick_name, phone, gender,head_img,open_id,union_id,docking_type,reader_badge,pinyin_acronym,mechanism_id,type,job_number,position) ");
            sb.append("VALUES ");
            MessageFormat mf = new MessageFormat(
                    "( #'{'list[{0}].userName},#'{'list[{0}].password},#'{'list[{0}].nickName},#'{'list[{0}].phone},#'{'list[{0}].gender },#'{'list[{0}].headImg },#'{'list[{0}].openId },#'{'list[{0}].unionId },#'{'list[{0}].dockingType },#'{'list[{0}].readerBadge },#'{'list[{0}].pinyinAcronym },#'{'list[{0}].mechanismId },#'{'list[{0}].type },#'{'list[{0}].jobNumber },#'{'list[{0}].position })");
            for (int i = 0; i < insertList.size(); i++) {
                sb.append(mf.format(new Object[]{i}));
                if (i < insertList.size() - 1) {
                    sb.append(",");
                }
            }
            return sb.toString();
        }

        /**
         * 批量删除用户
         * @param list
         * @return
         */
        public String batchDeleteUserByIdList(List<Integer> list ){
            String sql= new SQL(){
                {
                    UPDATE("user");
                    SET("is_delete=1 ");
                    if(!CollectionUtils.isEmpty(list)){
                        String idList= StringUtils.join(list, ",");
                        WHERE("id   in ( "+idList+" ) ");
                    }

                }
            }.toString();
            return sql;
        }

        /**
         * 批量停用用户
         * @param list
         * @return
         */
        public String batchDeactivateUserByIdList(List<Integer> list ){
            String sql= new SQL(){
                {
                    UPDATE("user");
                    SET("status=0 ");
                    if(!CollectionUtils.isEmpty(list)){
                        String idList= StringUtils.join(list, ",");
                        WHERE("id   in ( "+idList+" ) ");
                    }

                }
            }.toString();
            return sql;
        }

    }
}
