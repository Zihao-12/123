package com.example.webapp.Mapper.Banner;

import com.example.webapp.DO.BannerDO;
import com.example.webapp.DO.MechanismBannerRefDO;
import com.example.webapp.query.BannerQuery;
import com.example.webapp.query.BindMechanismQuery;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.jdbc.SQL;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

public class BannerMapperDynaSqlProvider {

    /**
     * 机构端轮播图列表
     * @param query
     * @return
     */
    public String mechList(final BannerQuery query) {
        SQL sql = new SQL() {
            {
                SELECT(" DISTINCT b.* ");
                FROM(" banner b ");
                LEFT_OUTER_JOIN("  mechanism_banner_ref mbr  on b.id=mbr.banner_id  and mbr.is_delete=0 ");
                if (!StringUtils.isEmpty(query.getName())) {
                    WHERE(" b.name like #{name} ");
                }
                if(query.getType()!=null){
                    WHERE(" b.type=#{type} ");
                }
                if(query.getStatus()!=null){
                    WHERE(" b.status=#{status} ");
                }
                WHERE(" b.is_delete=0 ");
                WHERE(" mbr.mechanism_id=#{mechanismId} ");

                ORDER_BY(" b.create_time desc");
            }
        };
        return sql.toString();
    }

    /**
     * 运营端轮播图列表
     * @param query
     * @return
     */
    public String list(final BannerQuery query) {
        SQL sql = new SQL() {
            {
                SELECT(" * ");
                FROM(" banner b ");
                if (!StringUtils.isEmpty(query.getName())) {
                    WHERE(" b.name like #{name} ");
                }
                WHERE(" b.is_delete=0 ");
                WHERE(" b.mechanism_id=0 ");
                if(query.getStatus()!=null){
                    WHERE(" b.status=#{status} ");
                }
                if(query.getType()!=null){
                    WHERE(" b.type=#{type} ");
                }
                ORDER_BY(" b.create_time desc");
            }
        };
        return sql.toString();
    }


    public String update(final BannerDO bannerDO) {
        return new SQL() {
            {
                UPDATE(" banner ");
//                SET("type=#{type}");
//                SET("status=#{status}");
                SET("name=#{name}");
                SET("image_url=#{imageUrl}");
                SET("image_url_pc=#{imageUrlPc}");
                SET("jump_type=#{jumpType}");
                SET("jump_url=#{jumpUrl}");
                SET("sort=#{sort}");
                WHERE("id=#{id}");
            }
        }.toString();
    }


    /**
     * 批量保存机构轮播图
     * @param map
     * @return
     */
    public String insertMechanismBannerRefList(Map map) {
        List<MechanismBannerRefDO> insertList = (List<MechanismBannerRefDO>) map.get("list");
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO mechanism_banner_ref ");
        sb.append("(mechanism_id ,banner_id) ");
        sb.append("VALUES ");
        MessageFormat mf = new MessageFormat(
                "( #'{'list[{0}].mechanismId},#'{'list[{0}].bannerId})");
        for (int i = 0; i < insertList.size(); i++) {
            sb.append(mf.format(new Object[]{i}));
            if (i < insertList.size() - 1) {
                sb.append(",");
            }
        }
        return sb.toString();
    }

    public String getRefMechanismList(final BindMechanismQuery query) {
        SQL sql = new SQL() {
            {
                SELECT(" DISTINCT m.* ,a.name provinceCn,b.name cityCn,d.name attributeCn  ");
                FROM(" mechanism m  ");
                LEFT_OUTER_JOIN("  mechanism_banner_ref mbr on m.id =mbr.mechanism_id and mbr.is_delete=0 ");
                LEFT_OUTER_JOIN(" area a on a.id =m.province ");
                LEFT_OUTER_JOIN(" area b on b.id =m.city ");
                LEFT_OUTER_JOIN(" (select * from dictionary where type=1) d on d.value=m.attribute ");
                if (!StringUtils.isEmpty(query.getName())) {
                    WHERE(" m.name like #{name} ");
                }
                WHERE(" m.is_delete=0 ");
                WHERE(" mbr.banner_id=#{id} ");

                ORDER_BY(" m.create_time desc");
            }
        };
        return sql.toString();
    }
}