package com.example.webapp.utils.excel;

import com.google.common.collect.Lists;
import io.swagger.annotations.ApiModelProperty;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Stream;

@Slf4j
public class TableBeanUtils {

    /**
     * 根据类名获取表结构
     * @param name 取值 TableDOEnum.name
     * @return
     */
    public static TableDTO getTableStructure(String name) {
        TableDTO tableDTO = new TableDTO();
        String clazzStr = TableDOEnum.getClazzByName(name);
        if(StringUtils.isBlank(clazzStr)){
            return null;
        }
        tableDTO.setClazz(clazzStr);
        List<TableFieldDTO> list = Lists.newArrayList();
        try {
            Class clazz = Class.forName(tableDTO.getClazz());
            Field[] fieldArr = clazz.getDeclaredFields();
            Stream<Field> stream = Stream.of(fieldArr);
            stream.forEach(field -> {
                TableFieldDTO dto = new TableFieldDTO();
                ApiModelProperty annotation = field.getAnnotation(ApiModelProperty.class);
                if(annotation !=null && !field.getName().equals("id")){
                    dto.setFieldName(annotation.value());
                    dto.setField(field.getName());
                    String type =field.getType().getName()
                            .replace("java.lang.","")
                            .replace("java.util.","")
                            .replace("java.math.","");
                    dto.setFieldType(type);
                    list.add(dto);
                }
            });
            tableDTO.setFieldList(list);
        } catch (ClassNotFoundException e) {
            log.error("{}", ExceptionUtils.getStackTrace(e));
        }
        return tableDTO;
    }
}
