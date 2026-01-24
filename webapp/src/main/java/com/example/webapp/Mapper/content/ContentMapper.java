package com.example.webapp.Mapper.content;

import com.example.webapp.DO.ContentDO;
import com.example.webapp.DO.ContentImageRefDO;
import com.example.webapp.DTO.ContentDTO;
import com.example.webapp.query.ContentQuery;
import com.example.webapp.VO.ContentVO;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Mapper
public interface ContentMapper {

    @SelectProvider(type= ContentMapperDynaSqlProvider.class,method="selectAll")
    List<ContentDTO> selectAll(ContentQuery query);


    @Insert(" INSERT INTO `content`(`type`, `content_type`, `category_id`, `style`, `title`, `video_url`, `source`, `details`, `status`, `top`, `sort`, `is_delete`) " +
            " VALUES (#{type}, #{contentType}, #{categoryId}, #{style}, #{title}, #{videoUrl}, #{source}, #{details}, #{status}, #{top}, #{sort}, 0) ")
    @Options(useGeneratedKeys = true, keyColumn = "id", keyProperty = "id")
    int insert(ContentDO contentDO);

    @InsertProvider(type = ContentMapperDynaSqlProvider.class,method = "insertImageRef")
    void insertImageRef(@Param("id") Integer id, @Param("list") List<String> imageUrlList);

    @Update(" update content_image_ref set is_delete=1 where content_id=#{contentId} ")
    void deleteImageRef(@Param("contentId") Integer contentId);

    @UpdateProvider(type = ContentMapperDynaSqlProvider.class,method = "update")
    int update(ContentDO contentDO);

    @Update("update content set is_delete=1 where id=#{id}")
    int delete(int id);

    @Select(" select c.*,GROUP_CONCAT(cir.image_url) imageUrls " +
            " from content c " +
            " left join content_image_ref cir on cir.content_id=c.id and cir.is_delete=0 " +
            " where c.is_delete=0 and c.id=#{id} " +
            " GROUP BY c.id ")
    ContentDTO view(int id);

    @Update("update content set status=#{status} where id=#{id}")
    int updateStatus(int id, int status);

    /**
     * 是否置顶:0未置顶 1.置顶
     */
    @Update(" update content set top=#{top} where id=#{id} ")
    int top(int id, int top);

    /**
     * 是否推荐:0否 1.是
     */
    @Update(" update content set recommend=#{recommend} where id=#{id} ")
    int recommend(int id, int recommend);

    @SelectProvider(type= ContentMapperDynaSqlProvider.class,method="portalList")
    List<ContentVO> portalList(ContentQuery query);

    @Select(" select id,type,content_type,style,title,video_url,source,details,create_time from content where id=#{id} ")
    ContentVO portalDetail(int id);

    @Update(" update content set page_view=#{pageView} where id=#{id} ")
    void updatePageView(int id, int pageView);

    @SelectProvider(type= ContentMapperDynaSqlProvider.class,method="portalRecommendList")
    List<ContentVO> portalRecommendList(ContentQuery query);

    @Select(" select * from content_image_ref cir where cir.content_id=#{id} ")
    List<ContentImageRefDO> viewImageRef(int id);

    @SelectProvider(type= ContentMapperDynaSqlProvider.class,method="portalPortionList")
    List<ContentVO> portalPortionList(ContentQuery query);
}