package com.example.webapp.Mapper.Mechanism;

import com.example.webapp.DO.MechanismContactPersonDO;
import com.example.webapp.DO.MechanismDO;
import com.example.webapp.query.MechanismQuery;
import com.example.webapp.enums.MechanismListOpenTypeEnum;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.jdbc.SQL;
import org.springframework.util.CollectionUtils;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

public class MechanismMapperDynaSqlProvider {

    public String findMechanismList(final MechanismQuery query){
        SQL sql = new SQL(){
            {
                SELECT(" m.* ,a.name provinceCn,b.name cityCn,d.name attributeCn ");
                FROM(" mechanism m ");
                LEFT_OUTER_JOIN(" area a on a.id =m.province ");
                LEFT_OUTER_JOIN(" area b on b.id =m.city ");
                LEFT_OUTER_JOIN(" (select * from dictionary where type=1) d on d.value=m.attribute ");
                WHERE(" m.is_delete = 0");
                if(!StringUtils.isEmpty(query.getName())){
                    WHERE(" m.name like #{name}");
                }
                if(query.getAttribute() != null){
                    WHERE(" m.attribute = #{attribute}");
                }
                if(query.getProvince() != null){
                    WHERE(" m.province = #{province}");
                }
                if(query.getOpenType()!=null){
                    if(MechanismListOpenTypeEnum.EXCLUDE_OPEN.getType().equals(query.getOpenType())){
                        WHERE(" m.id  not in (select distinct mechanism_id from mechanism_open o where  o.open_type =0 and o.is_delete=0 and ( o.begin_time >now() or (o.end_time >= now() and o.begin_time <=now())) ) ");
                    }else  if(MechanismListOpenTypeEnum.ONLY_OPEN.getType().equals(query.getOpenType())){
                        WHERE(" m.id in (select distinct mechanism_id from mechanism_open o where  o.open_type =0 and o.is_delete=0 and ( o.begin_time >now() or (o.end_time >= now() and o.begin_time <=now())) ) ");
                    }
                }
                ORDER_BY(" m.id desc");
            }
        };
        return sql.toString();
    }

    /**
     * 保存机构联系人
     * @param map
     * @return
     */
    public String insertContactPersonList(Map map) {
        List<MechanismContactPersonDO> insertList = (List<MechanismContactPersonDO>) map.get("list");
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO mechanism_contact_person ");
        sb.append("(mechanism_id ,name,phone,email,remark) ");
        sb.append("VALUES ");
        MessageFormat mf = new MessageFormat(
                "( #'{'list[{0}].mechanismId},#'{'list[{0}].name},#'{'list[{0}].phone} ,#'{'list[{0}].email},#'{'list[{0}].remark})");
        for (int i = 0; i < insertList.size(); i++) {
            sb.append(mf.format(new Object[]{i}));
            if (i < insertList.size() - 1) {
                sb.append(",");
            }
        }
        return sb.toString();
    }

    /**
     * 删除不在修改列表的联系人(修改列表空则全删)
     * @param mechanismId
     * @param
     * @return
     */
    public String delPersonOfNotInUpdateList(Integer mechanismId,List<Integer> list ){
        String sql= new SQL(){
            {
                UPDATE("mechanism_contact_person");
                SET("is_delete=1 ");
                WHERE("mechanism_id = #{mechanismId}");
                if(!CollectionUtils.isEmpty(list)){
                    String idList= StringUtils.join(list, ",");
                    WHERE("id not in ( "+idList+" ) ");
                }

            }
        }.toString();
        return sql;
    }

    /** 修改对象*/
    public String updateMechanism(final MechanismDO mechanism){
        return new SQL(){
            {
                UPDATE("mechanism");
                if(!StringUtils.isEmpty(mechanism.getName())){
                    SET("name=#{name}");
                }
                if(!StringUtils.isEmpty(mechanism.getPassword())){
                    SET("password=#{password}");
                }
                if(mechanism.getAttribute()!=null){
                    SET("attribute=#{attribute}");
                }
                if(mechanism.getProvince()!=null){
                    SET("province=#{province}");
                }
                if(mechanism.getCity()!=null){
                    SET("city=#{city}");
                }
                SET("address=#{address}");
                SET("domain=#{domain}");
                SET("navbar_logo=#{navbarLogo}");
                SET("login_logo=#{loginLogo}");
                SET("show_name=#{showName}");
                SET("app_login_logo=#{appLoginLogo}");
                SET("app_navbar_logo=#{appNavbarLogo}");
                SET("app_domain=#{appDomain}");
                SET("app_show_name=#{appShowName}");
                WHERE("id=#{id}");
            }
        }.toString();
    }



}
