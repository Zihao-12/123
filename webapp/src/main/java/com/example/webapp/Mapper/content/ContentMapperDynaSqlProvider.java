package com.example.webapp.Mapper.content;


import com.example.webapp.DO.ContentDO;
import com.example.webapp.Query.ContentQuery;
import com.example.webapp.enums.DictionaryTypeEnum;
import com.example.webapp.enums.RecommendEnum;
import com.example.webapp.enums.UpDownStatusEnum;
import org.apache.commons.lang3.StringUtils;
import org.apache.ibatis.jdbc.SQL;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

public class ContentMapperDynaSqlProvider {
    public String selectAll(final ContentQuery query) {
        SQL sql = new SQL() {
            {
                SELECT(" c.*,cate.name categoryName ");
                FROM(" content c ");
                LEFT_OUTER_JOIN(" category cate on cate.id=c.category_id and cate.is_delete=0 ");
                if (!StringUtils.isEmpty(query.getTitle())) {
                    WHERE(" c.title like #{title}");
                }
                WHERE(" c.is_delete=0");
                if (query.getContentType()!=null) {
                    WHERE(" c.content_type=#{contentType}");
                }
                if (query.getStatus()!=null) {
                    WHERE(" c.status=#{status}");
                }
                if (query.getType()!=null) {
                    WHERE(" c.type=#{type}");
                }
                if (query.getCategoryId()!=null) {
                    WHERE(" c.category_id=#{categoryId}");
                }
                ORDER_BY(" c.create_time desc");
            }
        };
        return sql.toString();
    }

    public String insertImageRef(Map map){
        List<String> imageUrlList = (List<String>) map.get("list");
        Integer id = (Integer) map.get("id");
        StringBuilder sb = new StringBuilder();
        sb.append(" INSERT INTO `content_image_ref`(`image_url`, `content_id`, `is_delete`) ");
        sb.append(" values");
        MessageFormat mf = new MessageFormat("(#'{'list[{0}]},#'{'id},0)");
        for (int i = 0; i < imageUrlList.size(); i++) {
            sb.append(mf.format(new Object[]{i}));
            if(i<imageUrlList.size()-1){
                sb.append(",");
            }
        }
        return sb.toString();
    }

    public String update(final ContentDO contentDO) {
        return new SQL() {
            {
                UPDATE(" content ");
                SET("category_id=#{categoryId}");
                SET("style=#{style}");
                SET("title=#{title}");
                SET("video_url=#{videoUrl}");
                SET("source=#{source}");
                SET("details=#{details}");
                SET("sort=#{sort}");
                WHERE("id=#{id}");
            }
        }.toString();
    }


    public String portalList(final ContentQuery query) {
        SQL sql = new SQL() {
            {
                SELECT(" c.id,c.type,c.content_type,c.style,c.title,c.video_url,c.source,d.name sourceCn,c.details,c.create_time,c.category_id,GROUP_CONCAT(cir.image_url) imageUrls ");
                FROM(" content c ");
                LEFT_OUTER_JOIN("  content_image_ref cir on cir.content_id=c.id and cir.is_delete=0 ");
                LEFT_OUTER_JOIN("  dictionary d on d.value=c.source and d.is_delete=0 and d.type="+ DictionaryTypeEnum.NEWS.getType());
                WHERE(" c.is_delete=0 and c.type=#{type} and c.status="+ UpDownStatusEnum.UP.getStatus());
                if (query.getCategoryId() != null) {
                    WHERE(" c.category_id=#{categoryId}");
                }
                GROUP_BY(" c.id,d.name ");
                ORDER_BY(" c.sort asc,c.create_time desc");
            }
        };
        return sql.toString();
    }

    public String portalRecommendList(final ContentQuery query) {
        SQL sql = new SQL() {
            {
                SELECT(" c.id,c.type,c.content_type,c.style,c.title,c.video_url,c.source,c.details,c.create_time,GROUP_CONCAT(cir.image_url) imageUrls ");
                FROM(" content c ");
                LEFT_OUTER_JOIN("  content_image_ref cir on cir.content_id=c.id and cir.is_delete=0 ");
                WHERE(" c.is_delete=0 and type=#{type} and c.status="+ UpDownStatusEnum.UP.getStatus());
                WHERE(" c.recommend="+ RecommendEnum.recommend_yes.getValue());
                GROUP_BY(" c.id ");
                ORDER_BY(" c.sort asc,c.create_time desc");
            }
        };
        return sql.toString();
    }

    public String portalPortionList(final ContentQuery query) {
        SQL sql = new SQL() {
            {
                SELECT(" c.id,c.type,c.content_type,c.style,c.title,c.video_url,c.source,c.details,c.create_time,GROUP_CONCAT(cir.image_url) imageUrls,d.name sourceCn ");
                FROM(" content c ");
                LEFT_OUTER_JOIN("  content_image_ref cir on cir.content_id=c.id and cir.is_delete=0 ");
                LEFT_OUTER_JOIN("  dictionary d on d.value=c.source and d.is_delete=0 and d.type="+ DictionaryTypeEnum.NEWS.getType());
                WHERE(" c.is_delete=0 and c.type=#{type} and c.status="+ UpDownStatusEnum.UP.getStatus());
                GROUP_BY(" c.id,d.name ");
                ORDER_BY(" c.create_time desc");

            }
        };
        return sql.toString()+" limit "+query.getPortionCount();
    }

}
