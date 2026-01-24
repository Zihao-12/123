package com.example.webapp.Mapper.Banner;

import com.example.webapp.DO.BannerDO;
import com.example.webapp.DO.MechanismBannerRefDO;
import com.example.webapp.DTO.BannerDTO;
import com.example.webapp.DTO.MechanismDTO;
import com.example.webapp.query.BannerQuery;
import com.example.webapp.query.BindMechanismQuery;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Mapper
public interface BannerMapper {

    /**
     * 机构端轮播图列表
     * @param query
     * @return
     */
    @SelectProvider(type= BannerMapperDynaSqlProvider.class,method="mechList")
    List<BannerDTO> mechList(BannerQuery query);

    /**
     * 运营端轮播图列表
     * @param query
     * @return
     */
    @SelectProvider(type= BannerMapperDynaSqlProvider.class,method="list")
    List<BannerDTO> list(BannerQuery query);


    @Insert(" INSERT INTO `banner`(`type`, `status`, `name`, `image_url`,`image_url_pc`, `jump_type`, `jump_url`, `sort`, `is_delete`,mechanism_id)  " +
            " VALUES (#{type}, #{status}, #{name}, #{imageUrl},#{imageUrlPc}, #{jumpType}, #{jumpUrl}, #{sort}, 0,#{mechanismId}) ")
    @Options(useGeneratedKeys = true, keyColumn = "id", keyProperty = "id")
    int insert(BannerDO bannerDO);


    @UpdateProvider(type = BannerMapperDynaSqlProvider.class,method = "update")
    int update(BannerDO bannerDO);

    @Update("update banner set is_delete=1 where id=#{id}")
    int delete(int id);

    @Select("select * from banner where id=#{id}")
    BannerDTO view(int id);

//    /**
//     * 已有轮播图数据(type 1.首页)
//     * @param type
//     * @return
//     */
//    @Select(" select count(*) from banner where is_delete=0 and type=#{type} ")
//    int countBanner(Integer type);

    @Update("update banner set status=#{status} where id=#{id}")
    int updateStatus(int id, int status);

    /**
     * 批量保存机构轮播图关联
     * @param mechanismBannerRefDOList
     * @return
     */
    @InsertProvider(type = BannerMapperDynaSqlProvider.class, method = "insertMechanismBannerRefList")
    @Options(useGeneratedKeys = true, keyColumn = "id", keyProperty = "id")
    Integer insertMechanismBannerRefList(List<MechanismBannerRefDO> mechanismBannerRefDOList);


    /**
     * 删除指定轮播图 所有关联
     * @param bannerId
     * @return
     */
    @Update("update mechanism_banner_ref set is_delete=1 where banner_id=#{bannerId}")
    int deleteMechanismBannerRef(int bannerId);

    /**
     * 取消轮播图机构关联
     * @param bannerId
     * @param mechanismId
     * @return
     */
    @Update("update mechanism_banner_ref set is_delete=1 where mechanism_id = #{mechanismId} and banner_id=#{bannerId}")
    int disassociate(Integer bannerId, Integer mechanismId);

    /**
     * 批量取消轮播图机构关联
     * @param bannerId
     * @param mechanismIdList
     * @return
     */
    @Update("<script>"
            + "update mechanism_banner_ref set is_delete=1 where   banner_id=#{bannerId} and mechanism_id in  "
            + "<foreach item='item' index='index' collection='mechanismIdList' open='(' separator=',' close=')'>"
            + "#{item}"
            + "</foreach>"
            + "</script>")
    int disassociateMechanismRefList(Integer bannerId, List<Integer> mechanismIdList);
    /**
     * 获取已开通到机构ID
     * @return
     */
    @Select("SELECT mechanism_id FROM mechanism_open WHERE is_delete =0 ")
    List<Integer> getOpenMechanismIdList();

    /**
     * 获取轮播图关联的机构列表
     * @param query
     * @return
     */
    @SelectProvider(type= BannerMapperDynaSqlProvider.class,method="getRefMechanismList")
    List<MechanismDTO> getRefMechanismList(BindMechanismQuery query);

    /**
     * 获取关联机构ID
     * @param bannerId
     * @return
     */
    @Select(" select DISTINCT mechanism_id  from mechanism_banner_ref where is_delete =0 and banner_id=#{bannerId} ")
    List<Integer> getRefMechanismIdList(Integer bannerId);
}