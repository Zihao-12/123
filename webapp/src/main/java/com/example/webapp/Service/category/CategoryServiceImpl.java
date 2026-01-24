package com.example.webapp.Service.category;

import com.example.webapp.DO.CategoryDO;
import com.example.webapp.DO.CategoryObjectRefDO;
import com.example.webapp.DTO.CategoryDTO;
import com.example.webapp.DTO.CategoryObjNumParam;
import com.example.webapp.DTO.CategoryObjectNumDTO;
import com.example.webapp.DTO.ObjectCategoryDTO;
import com.example.webapp.Mapper.MechanismOpen.MechanismOpenMapper;
import com.example.webapp.Mapper.category.CategoryMapper;
import com.example.webapp.annotation.Cacheable;
import com.example.webapp.annotation.RepeatableCommit;
import com.example.webapp.enums.CategoryNodeTypeEnum;
import com.example.webapp.enums.CollegeChildTypeEnum;
import com.example.webapp.enums.MechanismOpenTypeEnum;
import com.example.webapp.result.CodeEnum;
import com.example.webapp.result.Result;
import com.example.webapp.utils.DateTimeUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.Transactional;


import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 分类表
 * @author ghs
 */
@EnableTransactionManagement
@Slf4j
@Service
public class CategoryServiceImpl implements CategoryService,Serializable{
    private static final long serialVersionUID = 4800994516532057532L;
    public static final int MAX_CREATE_NODE_SIZE = 20;
    public static final Integer DEFAULT_ROOT_PARENT_ID = 0;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private MechanismOpenMapper mechanismOpenMapper;

    /**
     * 创建一个新分类树
     * @param treeName
     * @return
     */
    @Override
    public Result createCategorytree(String treeName) {
        return createSigleNode(DEFAULT_ROOT_PARENT_ID, treeName, CategoryNodeTypeEnum.NODE.getType());
    }

    /**
     * 创建分类-支持批量创建子分类（不能创建树）
     * @param parentId
     * @param nodeType
     * @param nodeNameList
     * @return
     */
    @RepeatableCommit(timeout = 5)
    @Override
    public Result createCategoryNode(Integer parentId, Integer nodeType, List<String> nodeNameList) {
        if(parentId == null || parentId<=0){
            //（不能创建树）
            return Result.fail("父节点不存在");
        }
        if(CollectionUtils.isEmpty(nodeNameList)){
            return Result.fail("nodeNameList is null!");
        }
        if(nodeNameList.size() > MAX_CREATE_NODE_SIZE){
            return Result.fail(CodeEnum.FAILED.getValue(),"同时创建分类数量不能超过20个");
        }
        List<Map<String,Object>> relist = Lists.newArrayList();
        nodeNameList.stream().forEach(name ->{
            Map<String,Object> map = Maps.newConcurrentMap();
            Result result = this.createSigleNode(parentId,name,nodeType);
            map.put("code",result.getCode());
            map.put("name",name);
            map.put("msg","success");
            if(CodeEnum.FAILED.getValue().equals(result.getCode())){
                map.put("msg",result.getMessage());
            }else {
                CategoryDO collegeDO = (CategoryDO) result.getData();
                map.put("id",collegeDO.getId());
            }
            relist.add(map);
        });
        return Result.ok(relist);
    }

    /**
     * 修改分类名称：包含本节点和子孙节点的name全路径的修改
     * @param nodeId
     * @param name
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @RepeatableCommit(timeout = 5)
    @Override
    public Result updateCategoryName(Integer nodeId, String name) {
        try{
            CategoryDO categoryDO = categoryMapper.findNodeById(nodeId);
            //查看是否存在重名的兄弟节点
            CategoryDO siblingNode = categoryMapper.findChildNodeByNodeNameAndParentId(name,categoryDO.getParentId());
            if(siblingNode!=null){
                return Result.fail(CodeEnum.FAILED.getValue(),"存在同名的兄弟节点,请修改后在提交");
            }
            if(categoryDO!=null){
                String oldNameFullPath = categoryDO.getNameFullPath();
                int index = oldNameFullPath.lastIndexOf(DateTimeUtil.DELIMITER_ONE_PERIOD);
                String newNfp =oldNameFullPath.substring(0,index+1)+name;
                CategoryDO up=new CategoryDO();
                up.setName(name);
                //生成新的name全路径
                up.setNameFullPath(newNfp);
                up.setId(categoryDO.getId());
                categoryMapper.update(up);
                String parentIdFullPath = categoryDO.getIdFullPath()+categoryDO.getId()+".%";
                //更新子孙节点全路径
                List<CategoryDTO> childList = categoryMapper.findOffspringNodeByParentIdFullPath(parentIdFullPath);
                if(CollectionUtils.isNotEmpty(childList)){
                    childList.forEach(node ->{
                        CategoryDO upc = new CategoryDO();
                        upc.setNameFullPath(node.getNameFullPath().replaceFirst(oldNameFullPath,newNfp));
                        upc.setId(node.getId());
                        categoryMapper.update(upc);
                    });
                }
                return Result.ok(categoryDO.getName()+"及其子分类已修改");
            }else {
                return Result.fail(CodeEnum.FAILED.getValue(),"不可修改");
            }
        }catch (Exception e){
            log.error("{}",ExceptionUtils.getStackTrace(e));
            throw new RuntimeException(ExceptionUtils.getStackTrace(e));
        }
    }

    /**
     * 查询所有分类树
     * @return
     */
    @Override
    public Result findcategoryTree() {
        List<CategoryDTO> nodeList =  categoryMapper.findcategoryTree();
        return Result.ok(nodeList);
    }

    /**
     * 查询所有子节点
     * @param nodeId
     * @return
     */
//    @Cacheable(fieldKey = "#nodeId")
    @Override
    public Result findChildNodeByParentId(Integer nodeId){
        CategoryDO categoryDO = categoryMapper.findNodeById(nodeId);
        if(categoryDO == null){
            return Result.fail("分类不存在");
        }
        List<CategoryDTO> nodeList =  categoryMapper.findChildNodeByParentId(nodeId);
        return Result.ok(nodeList);
    }

    /**
     * 查询所有子孙节点
     * @param nodeId
     * @return
     */
//    @Cacheable(fieldKey = "#nodeId")
    @Override
    public Result findOffspringNodeByParentId(Integer nodeId){
        CategoryDO categoryDO = categoryMapper.findNodeById(nodeId);
        if(categoryDO == null){
            return Result.fail("分类不存在");
        }
        //根据父id_full_path 模糊匹配
        String parentIdFullPath = categoryDO.getIdFullPath()+categoryDO.getId()+".%";
        List<CategoryDTO> nodeList = categoryMapper.findOffspringNodeByParentIdFullPath(parentIdFullPath);
        return Result.ok(nodeList);
    }


    /**
     * 删除分类
     * @param id
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @RepeatableCommit(timeout = 5)
    @Override
    public Result deleteNode(Integer id){
        try{
            CategoryDO categoryDO = categoryMapper.findNodeById(id);
            if(categoryDO!=null && !DEFAULT_ROOT_PARENT_ID.equals(categoryDO.getParentId())){
                //判断没有关联才删
                Integer refCount = categoryMapper.getRefObjectCountByCategoryId(id);
                if(refCount > 0 ){
                    return Result.fail(CodeEnum.FAILED.getValue(),"需要先移除关联关系");
                }
                List<CategoryDTO> nodeList =  categoryMapper.findChildNodeByParentId(id);
                if(CollectionUtils.isNotEmpty(nodeList)){
                    return Result.fail(CodeEnum.FAILED.getValue(),"需要先移除子节点");
                }
                List<CategoryDTO>  cns=categoryMapper.findChildNodeByParentId(categoryDO.getParentId());
                if(CollectionUtils.isEmpty(cns)){
                    CategoryDO parent=new CategoryDO();
                    parent.setChildType(CollegeChildTypeEnum.CREATE_DEPARTMENT_CLASS.getType());
                    parent.setId(categoryDO.getParentId());
                    categoryMapper.update(parent);
                }
                //删除节点
                categoryMapper.delete(categoryDO.getId());
                // 2-分类排序：删除元素时，将大于该元素的序号，都减1
                categoryMapper.minusOneWhenDeleteNode(categoryDO.getParentId(),categoryDO.getSort());
                return Result.ok(categoryDO.getName()+"已删除");
            }else {
                return Result.fail(CodeEnum.FAILED.getValue(),"节点不存在");
            }
        }catch (Exception e){
            log.error("{}",ExceptionUtils.getStackTrace(e));
            throw new RuntimeException(ExceptionUtils.getStackTrace(e));
        }
    }

    /**
     * 创建分类
     * @param parentId
     * @param name
     * @param nodeType
     * @return
     */
    @Override
    public Result createSigleNode(Integer parentId, String name, Integer nodeType){
        try {
            nodeType = CategoryNodeTypeEnum.LEAF.getType().equals(nodeType)?nodeType:CategoryNodeTypeEnum.NODE.getType();
            CategoryDO category = new CategoryDO();
            category.setParentId(parentId);
            category.setName(name);
            category.setLeafNode(nodeType);
            CategoryDO parentNode = null;
            if(!DEFAULT_ROOT_PARENT_ID.equals(parentId)){
                parentNode = categoryMapper.findNodeById(category.getParentId());
                if(parentNode==null){
                    return Result.fail(CodeEnum.FAILED.getValue(),"父节点不可用");
                }else {
                    category.setIdFullPath(parentNode.getIdFullPath()+parentNode.getId()+".");
                    category.setNameFullPath(parentNode.getNameFullPath()+"."+name);

                }
            } else {
                category.setIdFullPath(".");
                category.setNameFullPath(name);
            }

            CategoryDO childNode = categoryMapper.findChildNodeByNodeNameAndParentId(name,parentId);
            if(childNode!=null){
                return Result.fail(CodeEnum.FAILED.getValue(),"节点名已存在,请修改后在提交");
            }

            Result result = parseChildType(category, parentNode);
            if(CodeEnum.FAILED.getValue().equals(result.getCode())){
                return result;
            }
            // 1-分类排序：新增元素时，序号为当前元素数据总量值
            List<CategoryDTO> childList =  categoryMapper.findChildNodeByParentId(parentId);
            Integer sort = CollectionUtils.isEmpty(childList)?0:childList.size();
            category.setSort(sort);
            categoryMapper.insert(category);
            return Result.ok(category);
        }catch (Exception e){
            log.error("{}",ExceptionUtils.getStackTrace(e));
            return Result.fail(ExceptionUtils.getStackTrace(e));
        }
    }

    private Result parseChildType(CategoryDO category, CategoryDO parentNode) {
        if(DEFAULT_ROOT_PARENT_ID.equals(category.getParentId())){
            category.setChildType(CollegeChildTypeEnum.CREATE_DEPARTMENT_CLASS.getType());
            return Result.ok(0);
        }
        Integer subChildType = CategoryNodeTypeEnum.LEAF.getType().equals(category.getLeafNode()) ? CollegeChildTypeEnum.NOT_CREATE_NODE.getType():CollegeChildTypeEnum.CREATE_DEPARTMENT_CLASS.getType();
        Integer parentChildType = CategoryNodeTypeEnum.LEAF.getType().equals(category.getLeafNode()) ? CollegeChildTypeEnum.CREATE_CLASS.getType():CollegeChildTypeEnum.CREATE_DEPARTMENT.getType();
        category.setChildType(subChildType);
        boolean faile =(CollegeChildTypeEnum.CREATE_CLASS.getType().equals(parentNode.getChildType()) &&CategoryNodeTypeEnum.NODE.getType().equals(category.getLeafNode()))
                ||(CollegeChildTypeEnum.CREATE_DEPARTMENT.getType().equals(parentNode.getChildType()) &&CategoryNodeTypeEnum.LEAF.getType().equals(category.getLeafNode()));
        if(faile){
            return Result.fail(CodeEnum.FAILED.getValue(),"不支持同一目录层级中，同时存在分类和叶子节点");
        }
        if(CollegeChildTypeEnum.NOT_CREATE_NODE.getType().equals(parentNode.getChildType())){
            return Result.fail(CodeEnum.FAILED.getValue(),"不可创建子节点");
        }
        if(CollegeChildTypeEnum.CREATE_DEPARTMENT_CLASS.getType().equals(parentNode.getChildType())){
            //此父结点只能创建部门
            parentNode.setChildType(parentChildType);
            categoryMapper.update(parentNode);
        }
        return Result.ok(0);
    }

    /**
     * 移动节点
     * @param nodeId 移动节点ID
     * @param offset 移动偏移量
     * @return
     */
    @Override
    public Result moveNode(Integer nodeId, Integer offset) {
        CategoryDO dto = categoryMapper.findNodeById(nodeId);
        if(dto == null ){
            return Result.fail("节点不存在");
        }
        Integer begin = dto.getSort();
        Integer end = begin + offset;
        if(begin < end){
            categoryMapper.minusOne(dto.getParentId(),begin,end);
        }else if(begin > end) {
            categoryMapper.addOne(dto.getParentId(),begin,end);
        }
        categoryMapper.updateNodeSort(nodeId,end);
        return Result.ok(0);
    }

    @Override
    public Result view(Integer id) {
        CategoryDO dto = null;
        try {
            dto = categoryMapper.findNodeById(id);
            if(dto==null){
                dto = new CategoryDO();
            }
        }catch (Exception e){
            log.error("{}", ExceptionUtils.getStackTrace(e));
            throw e;
        }
        return Result.ok(dto);

    }

    /**
     * 生成分类对象关联对象集合
     * @param objId
     * @param categoryIdList
     * @param categoryObjectType
     * @return
     */
    @Override
    public List<CategoryObjectRefDO> parseCategoryObjectRefList(Integer objId, List<Integer> categoryIdList, Integer categoryObjectType) {
        List<CategoryObjectRefDO> list = Lists.newArrayList();
        categoryIdList = categoryIdList.stream().distinct().collect(Collectors.toList());
        for (Integer categoryId : categoryIdList) {
            CategoryObjectRefDO cof = new CategoryObjectRefDO();
            cof.setCategoryId(categoryId);
            cof.setObjectId(objId);
            cof.setObjectType(categoryObjectType);
            list.add(cof);
        }
        return list;
    }

    /**
     * 批量查询分类
     * @param objectIdList
     * @param objectType
     * @return
     */
    @Override
    public Map<Integer, ObjectCategoryDTO>  getObjectCategoryMap(List<Integer> objectIdList, Integer objectType) {
        Map<Integer, ObjectCategoryDTO> map = categoryMapper.getObjectCategoryMap(objectIdList,objectType);
        if(map == null){
            map = Maps.newHashMap();
        }
        return map;
    }

    /**
     * 查询题目难度
     * @param objectIdList
     * @return
     */
    @Override
    public Map<Integer, ObjectCategoryDTO>  getObjectLevelMap(List<Integer> objectIdList) {
        Map<Integer, ObjectCategoryDTO> map = categoryMapper.getObjectLevelMap(objectIdList);
        if(map == null){
            map = Maps.newHashMap();
        }
        return map;
    }

    @Override
    public Result getCategoryObjNum(CategoryObjNumParam param) {
        List<CategoryObjectNumDTO> list = categoryMapper.getObjNumOfCategory(param.getObjectType(),param.getActegoryIdList());
        return Result.ok(list);
    }

    /**
     * 机构已购买内容分类列表
     * @param mechanismId
     * @return
     */
    @Cacheable(prefix = "getBuyCategoryList",fieldKey = "#mechanismId",expireTime = 60)
    @Override
    public Result getBuyCategoryList(Integer mechanismId) {
        Integer coursePackageId = mechanismOpenMapper.getCoursePackageIdOfMechanismOpen(mechanismId, MechanismOpenTypeEnum.PRACTICE.getType());
        List<CategoryDTO> nodeList = categoryMapper.getBuyCategoryList(coursePackageId);
        return Result.ok(nodeList);
    }


}


