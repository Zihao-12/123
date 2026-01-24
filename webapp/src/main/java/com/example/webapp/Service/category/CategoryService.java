package com.example.webapp.Service.category;

import com.example.webapp.DO.CategoryObjectRefDO;
import com.example.webapp.DTO.CategoryObjNumParam;
import com.example.webapp.DTO.ObjectCategoryDTO;
import com.example.webapp.result.Result;

import java.util.List;
import java.util.Map;

public interface CategoryService {

    /**
     * 创建一个新分类树
     * @param treeName
     * @return
     */
    Result createCategorytree(String treeName);

    /**
     * 创建分类-支持批量创建子分类
     * @param parentId
     * @param nodeType
     * @param nodeNameList
     * @return
     */
    Result createCategoryNode(Integer parentId, Integer nodeType, List<String> nodeNameList);

    /**
     * 查询所有子节点
     * @param nodeId
     * @return
     */
    Result findChildNodeByParentId(Integer nodeId);

    /**
     * 查询所有子孙节点
     * @param nodeId
     * @return
     */
    Result findOffspringNodeByParentId(Integer nodeId);

    /**
     * 删除分类
     * @param id
     * @return
     */
    Result deleteNode(Integer id);


    /**
     * 创建单个分类
     * @param parentId
     * @param name
     * @param nodeType
     * @return
     */
    Result createSigleNode(Integer parentId, String name, Integer nodeType);


    /**
     * 详情
     * @param id
     * @return
     */
    Result view(Integer id);


    /**
     * 修改分类名称：包含本节点和子孙节点的name全路径的修改
     * @param nodeId
     * @param name
     * @return
     */
    Result updateCategoryName(Integer nodeId, String name);

    /**
     * 查询所有分类树
     * @return
     */
    Result findcategoryTree();

    /**
     * 移动节点
     *           偏移量offset：元素从 x 移动到 y 时，offset = y - x。
     *               当元素向排序大的方向移动时，offset的为正值；若往排序小的方向移动时，offset`为负值
     * @param nodeId
     * @param offset
     * @return
     */
    Result moveNode(Integer nodeId, Integer offset);

    List<CategoryObjectRefDO> parseCategoryObjectRefList(Integer objId, List<Integer> categoryIdList, Integer categoryObjectType);

    Map<Integer, ObjectCategoryDTO> getObjectCategoryMap(List<Integer> objectIdList, Integer objectType);

    Map<Integer, ObjectCategoryDTO>  getObjectLevelMap(List<Integer> objectIdList);

    Result getCategoryObjNum(CategoryObjNumParam param);

    /**
     * 机构已购买内容分类列表
     * @param mechanismId
     * @return
     */
    Result getBuyCategoryList(Integer mechanismId);
}

