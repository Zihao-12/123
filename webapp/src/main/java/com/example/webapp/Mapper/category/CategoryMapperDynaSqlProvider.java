package com.example.webapp.Mapper.category;

import com.example.webapp.DO.CategoryDO;
import com.example.webapp.DO.CategoryObjectRefDO;
import org.apache.ibatis.jdbc.SQL;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

public class CategoryMapperDynaSqlProvider {

    public String update(final CategoryDO categoryDO) {
        return new SQL() {
            {
                UPDATE(" category ");
                if(categoryDO.getName() !=null){
                    SET("`name`=#{name}");
                }
                if(categoryDO.getParentId()!=null){
                    SET("`parent_id`=#{parentId}");
                }
                if(categoryDO.getIdFullPath()!=null){
                    SET("`id_full_path`=#{idFullPath}");
                }
                if(categoryDO.getNameFullPath()!=null){
                    SET("`name_full_path`=#{nameFullPath}");
                }
                if(categoryDO.getLeafNode()!=null){
                    SET("`leaf_node`=#{leafNode}");
                }
                if(categoryDO.getSort()!=null){
                    SET("`sort`=#{sort}");
                }
                if(categoryDO.getIsDelete()!=null){
                    SET("`is_delete`=#{isDelete}");
                }
                if(categoryDO.getChildType()!=null){
                    SET("`child_type`=#{childType}");
                }
                WHERE("id=#{id}");
            }
        }.toString();
    }

    /**
     * 保存分类对象关系
     * @param map
     * @return
     */
    public String insertCategoryObjectRefList(Map map) {
        List<CategoryObjectRefDO> insertList = (List<CategoryObjectRefDO>) map.get("list");
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO category_object_ref ");
        sb.append("(category_id ,category_age_id ,object_id,object_type) ");
        sb.append("VALUES ");
        MessageFormat mf = new MessageFormat(
                "( #'{'list[{0}].categoryId},#'{'list[{0}].categoryAgeId},#'{'list[{0}].objectId},#'{'list[{0}].objectType})");
        for (int i = 0; i < insertList.size(); i++) {
            sb.append(mf.format(new Object[]{i}));
            if (i < insertList.size() - 1) {
                sb.append(",");
            }
        }
        return sb.toString();
    }
}

