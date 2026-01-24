package com.example.webapp.Mapper.category;

import com.example.webapp.DO.CategoryDO;
import com.example.webapp.DO.CategoryObjectRefDO;
import com.example.webapp.DTO.CategoryDTO;
import com.example.webapp.DTO.CategoryObjectNumDTO;
import com.example.webapp.DTO.ObjectCategoryDTO;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@Mapper
public interface CategoryMapper {

    /**
     * 判断子分类名是否存在
     * @param nodeName
     * @param parentId
     * @return
     */
    @Select("select * from category where parent_id=#{parentId} and name =#{nodeName} and is_delete=0")
    CategoryDO findChildNodeByNodeNameAndParentId(@Param("nodeName") String nodeName, @Param("parentId") Integer parentId);

    /**
     * 主键查询分类
     * @param nodeId
     * @return
     */
    @Select("select * from category where id = #{nodeId} and is_delete=0")
    CategoryDO findNodeById(@Param("nodeId") Integer nodeId);

    @Insert(" INSERT INTO `category`(`name`,`parent_id`,`id_full_path`,`name_full_path`,`leaf_node`,`sort`,child_type) " +
            " VALUES (#{name},#{parentId},#{idFullPath},#{nameFullPath},#{leafNode},#{sort},#{childType})")
    @Options(useGeneratedKeys = true, keyColumn = "id", keyProperty = "id")
    int insert(CategoryDO categoryDO);


    @UpdateProvider(type = CategoryMapperDynaSqlProvider.class,method = "update")
    int update(CategoryDO CategoryDO);

    /**
     * 根据父 id_full_path 查询子孙节点
     * @param parentIdFullPath
     * @return
     */
    @Select("select * from category where id_full_path like #{parentIdFullPath} and is_delete=0")
    List<CategoryDTO> findOffspringNodeByParentIdFullPath(@Param("parentIdFullPath") String parentIdFullPath);

    /**
     * 查询所有分类树
     * @return
     */
    @Select("select * from category where parent_id =0 and is_delete=0")
    List<CategoryDTO> findcategoryTree();

    /**
     * 查询所有子节点
     * @param parentId
     * @return
     */
    @Select("select * from category where parent_id =#{parentId} and is_delete=0  ORDER BY sort,id")
    List<CategoryDTO> findChildNodeByParentId(@Param("parentId") Integer parentId);

    /**
     * 根据父ID全路径删除所有子孙节点
     * @param parentIdFullPath
     * @return
     */
    @Update("update category set is_delete=1 where id_full_path like #{parentIdFullPath} ")
    int delOffspringNodeByParentIdFullPath(String parentIdFullPath);

    @Update("update category set is_delete=1 where id=#{id}")
    int delete(Integer id);

    /**
     * 通过叶子节点查询所有tagId
     */
    @Select({
            "<script> ",
            " select id_full_path from category ",
            "<where> ",
            " is_delete=0  and id in ",
            "<foreach collection='tagIdList' index='index' item='item' open='(' separator=',' close=')'> " ,
            "#{item} ",
            "</foreach>",
            "</where> ",
            "</script>"
    })
    List<String> listTagIdsByLeaf(@Param("tagIdList") List<Integer> tagIdList);

    /**
     * 保存分类对象关系
     * @param list
     * @return
     */
    @InsertProvider(type = CategoryMapperDynaSqlProvider.class, method = "insertCategoryObjectRefList")
    @Options(useGeneratedKeys = true, keyColumn = "id", keyProperty = "id")
    Integer insertCategoryObjectRefList(List<CategoryObjectRefDO> list);

    /**
     * 删除对象关联分类
     * @param objectId
     * @param objectType
     */
    @Update("update category_object_ref set is_delete=1 where object_id=#{objectId} and object_type = #{objectType}")
    void deleteCategoryRefByObjectId(Integer objectId,Integer objectType);

    /**
     * 查询分类关联对象数
     * @param categoryId
     * @return
     */
    @Select("select count(1) from category_object_ref " +
            " where (category_id =#{categoryId} or category_age_id = #{categoryId} ) " +
            " and is_delete=0")
    Integer getRefObjectCountByCategoryId(@Param("categoryId") Integer categoryId);

    /**
     * 获取对象分类ID
     * @param courseId
     * @param type
     * @return
     */
    @Select("SELECT DISTINCT category_id FROM category_object_ref " +
            "WHERE object_id =#{courseId} and object_type =#{type} and is_delete =0")
    List<Integer> getCategoryIdListOfObject(Integer courseId, Integer type);

    /**1-分类排序：新增元素时，序号为当前元素数据总量值
     * 2-分类排序：删除元素时，将大于该元素的序号，都减1
     * @param parentId
     * @param sort
     * @return
     */
    @Select(" update category set sort =sort -1  " +
            " where parent_id =#{parentId} and `sort` > #{sort} and is_delete=0")
    Integer minusOneWhenDeleteNode(Integer parentId, Integer sort);

    /**
     * 3-分类排序：当元素从 x 移动到 y 时，
     *     若 x < y 时，则将(x, y)范围内的元素都减1    >x && <=y
     *     若 x > y 时，则将(y, x)范围内的元素都加1    >=y && <x
     * @param parentId
     * @param begin
     * @param end
     * @return
     */
    @Select(" update category set sort =sort - 1  " +
            " where parent_id =#{parentId} and `sort` > #{begin} and `sort` <= #{end} and is_delete=0")
    Integer minusOne(Integer parentId, Integer begin , Integer end);
    @Select(" update category set sort =sort + 1  " +
            " where parent_id =#{parentId} and `sort` >= #{end} and `sort` < #{begin} and is_delete=0")
    Integer addOne(Integer parentId, Integer begin , Integer end);

    @Select(" update category set sort =#{end} where id =#{nodeId}  and is_delete=0")
    void updateNodeSort(Integer nodeId, Integer end);


    /**
     * 批量查询对象分类信息 (存父ID，查询支持叶子分类) name_full_path
     * @param objectIdList
     * @param objectType
     * @return
     */
    @MapKey("id")
    @Select({" <script> ",
            " select cor.object_id id, ",
            " GROUP_CONCAT(distinct c.`id_full_path`,c.id,'.','@',c.`name_full_path` ORDER BY c.`id_full_path` desc  SEPARATOR ',') `idFullPaths`, ",
            " GROUP_CONCAT(distinct c.`name_full_path` ORDER BY c.`name_full_path` asc  SEPARATOR ',') `nameFullPaths`  ",
            " from category_object_ref cor  ",
            " INNER JOIN category c on c.id=cor.category_id and c.is_delete=0 and c.leaf_node =1 ",
            " where cor.is_delete=0 and cor.object_type=#{objectType} and cor.object_id in ",
            " <foreach item='item' index='index' collection='objectIdList' open='(' separator=',' close=')'> ",
            " #{item} ",
            " </foreach> ",
            " GROUP BY cor.object_id",
            " </script>",
    })
    Map<Integer, ObjectCategoryDTO> getObjectCategoryMap(@Param("objectIdList") List<Integer> objectIdList, @Param("objectType") Integer objectType);

    /**
     * 批量查询对象 难度 信息
     * @param objectIdList
     * @return
     */
    @MapKey("id")
    @Select({" <script> ",
            " select cor.question_id id, ",
            " GROUP_CONCAT(distinct c.`name` ORDER BY c.`name` asc  SEPARATOR ',') `levelNames`, ",
            " GROUP_CONCAT(distinct c.`value` ORDER BY c.`value` asc  SEPARATOR ',') `levelIds`    ",
            " from question_level_ref cor   ",
            " INNER JOIN dictionary c on c.`value`=cor.level and c.type =3 and c.is_delete=0  ",
            " where cor.is_delete=0  and cor.question_id in ",
            " <foreach item='item' index='index' collection='objectIdList' open='(' separator=',' close=')'> ",
            " #{item} ",
            " </foreach> ",
            " GROUP BY cor.question_id",
            " </script>",
    })
    Map<Integer, ObjectCategoryDTO> getObjectLevelMap(@Param("objectIdList") List<Integer> objectIdList);



    /**
     * 批量查询 分类关联对象的数量
     * @param objectIdList
     * @return
     */
    @Select({" <script> ",
            " SELECT cof.category_id,c.`name` categoryName,count(1) objNum ",
            " FROM category_object_ref cof   ",
            "  LEFT JOIN category c on c.id = cof.category_id  ",
            " <if test ='objectType == 4'>",
            "   JOIN question q on q.id = cof.object_id and q.is_delete =0 ",
            " </if>",
            " <if test ='objectType == 1'>",
            "   JOIN course q on q.id = cof.object_id and q.is_delete =0 ",
            " </if>",
            " WHERE cof.is_delete=0 and cof.object_type =#{objectType} and cof.category_id in  ",
            " <foreach item='item' index='index' collection='objectIdList' open='(' separator=',' close=')'> ",
            " #{item} ",
            " </foreach> ",
            " GROUP BY cof.category_id ,c.`name` ",
            " </script>",
    })
    List<CategoryObjectNumDTO> getObjNumOfCategory(@Param("objectType") Integer objectType, @Param("objectIdList") List<Integer> objectIdList);

    @MapKey("categoryId")
    @Select({" <script> ",
            " SELECT cof.category_id categoryId,c.`name` categoryName,count(1) objNum ",
            " FROM category_object_ref cof   ",
            "  LEFT JOIN category c on c.id = cof.category_id  ",
            " <if test ='objectType == 4'>",
            "   JOIN question q on q.id = cof.object_id and q.is_delete =0 ",
            " </if>",
            " WHERE cof.is_delete=0 and cof.object_type =#{objectType} and cof.category_id in  ",
            " <foreach item='item' index='index' collection='objectIdList' open='(' separator=',' close=')'> ",
            " #{item} ",
            " </foreach> ",
            " GROUP BY cof.category_id ,c.`name` ",
            " </script>",
    })
    Map<Integer,CategoryObjectNumDTO> getObjNumOfCategoryMap(@Param("objectType") Integer objectType,@Param("objectIdList") List<Integer> objectIdList);

    /**
     * 批量查询分类
     * @param categoryIdList
     * @return
     */
    @MapKey("id")
    @Select({" <script> ",
            " SELECT * ",
            " from category   ",
            " WHERE is_delete =0 and id in ",
            " <foreach item='item' index='index' collection='categoryIdList' open='(' separator=',' close=')'> ",
            " #{item} ",
            " </foreach> ",
            " </script>",
    })
    Map<Integer, CategoryDTO> getCategoryMap(@Param("categoryIdList") List<Integer> categoryIdList);

    /**
     * 查询机构购买的分类
     * @param coursePackageId
     * @return
     */
    @Select("SELECT id, `name`  FROM `category` WHERE id in ( " +
            "SELECT DISTINCT c.`parent_id`  " +
            "FROM `category` c " +
            "LEFT JOIN `category_object_ref` cor on cor.`category_id` =c.`id`  and cor.`object_type` =1 and cor.`is_delete` =0 " +
            "LEFT JOIN `course_package_ref` cpr on  cor.`object_id` =cpr.`course_id` and cpr.is_delete=0 " +
            "WHERE cpr.`course_package_id` =#{coursePackageId} and c.`leaf_node` =1 " +
            "    ) " +
            " ")
    List<CategoryDTO> getBuyCategoryList(@Param("coursePackageId") Integer coursePackageId);
}
