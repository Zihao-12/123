package com.example.webapp.Service.excel;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.example.webapp.Service.college.CollegeService;
import com.example.webapp.common.Constant;
import com.example.webapp.utils.DateTimeUtil;
import com.example.webapp.utils.SpringContextUtil;
import com.example.webapp.utils.excel.TableDOEnum;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import jakarta.servlet.annotation.WebListener;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.util.CollectionUtils;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 *
 * 有个很重要的点 DemoDataListener 不能被spring管理，要每次读取excel都要new,然后里面用到spring可以构造方法传进去
 */
@Slf4j
@WebListener
public class NoModleDataListener extends AnalysisEventListener<Map<Integer, String>> {

    /**
     * 每3000条保存一次并清理list ，方便内存回收
     */
    private static final int BATCH_COUNT = 500;

    /**
     * 实体类属性名称
     */
    public static final String RELATION_ATTRIBUTE = "attribute";
    /**
     * excel字段索引值
     */
    public static final String RELATION_INDEX = "index";

    public static final String JAVA_UTIL_DATE = "java.util.Date";
    public static final String JAVA_LANG_LONG = "java.lang.Long";
    public static final String JAVA_LANG_INTEGER = "java.lang.Integer";
    public static final String JAVA_LANG_FLOAT = "java.lang.Float";
    public static final String JAVA_LANG_DOUBLE = "java.lang.Double";
    public static final String JAVA_MATH_BIG_DECIMAL = "java.math.BigDecimal";

    List<Object> list = new ArrayList<>();
    private String clazzref;
    private Map<Integer,String> relationMap ;

    public NoModleDataListener(Map<String,String> argsMap){
        clazzref = argsMap.get(Constant.CLAZZ);
        String relation = argsMap.get(Constant.RELATION);
        relationMap = Maps.newConcurrentMap();
        JsonArray dataList = new JsonParser().parse(relation).getAsJsonArray();
        for (int i = 0; i < dataList.size(); i++) {
            JsonObject object = dataList.get(i).getAsJsonObject();
            String attribute = object.get(RELATION_ATTRIBUTE).getAsString();
            Integer index = object.get(RELATION_INDEX).getAsInt();
            relationMap.put(index,attribute);
        }
        log.info("Excel下标->属性名:{}",relationMap);
    }


    /**
     * 每一条数据解析都会来调用
     * @param data  index:字段值
     * @param context
     */
    @Override
    public void invoke(Map<Integer, String> data, AnalysisContext context) {
        try {
            Class clazz  =Class.forName(clazzref);
            Object obj = clazz.newInstance();
            for (Map.Entry<Integer, String> entry : data.entrySet()) {
                Integer index = entry.getKey();
                String valueStr =entry.getValue();
                String attributeName = relationMap.get(index);
                if(StringUtils.isBlank(attributeName)){
                    continue;
                }
                //获得对应属性
                Field field = clazz.getDeclaredField(attributeName);
                PropertyDescriptor pd = new PropertyDescriptor(relationMap.get(index), clazz);
                Method setMethod = pd.getWriteMethod();
                Object value = getObjectField(field,valueStr);
                setMethod.invoke(obj,value);
            }
            list.add(obj);
            if (list.size() >= BATCH_COUNT) {
                saveData();
                list.clear();
            }
        } catch (Exception e) {
            log.error("{}", ExceptionUtils.getStackTrace(e));
        }
    }

    /**
     * 利用反射给字段赋值（值为String类型，而字段类型可能是任意类型）
     * @param field
     * @param valueStr
     * @return
     * @throws Exception
     */
    public static Object getObjectField(Field field,String valueStr) {
        try {
            if(StringUtils.isBlank(valueStr)){
                return null;
            }
            String  typeClass = field.getType().toString();
            if(typeClass.contains(JAVA_UTIL_DATE)){
                return DateTimeUtil.parse(valueStr);
            }else if(typeClass.contains(JAVA_LANG_LONG)){
                return Long.valueOf(valueStr);
            }else if(typeClass.contains(JAVA_LANG_INTEGER)){
                return Integer.valueOf(valueStr);
            }else if(typeClass.contains(JAVA_LANG_FLOAT)){
                return  Float.valueOf(valueStr);
            }else if(typeClass.contains(JAVA_LANG_DOUBLE)){
                return  Double.valueOf(valueStr);
            }else if(typeClass.contains(JAVA_MATH_BIG_DECIMAL)){
                return  new BigDecimal(valueStr);
            }else {
                return valueStr;
            }
        }catch (Exception e){
            log.error("属性值转化报错 >>>> 属性名:{}，属性类型:{},属性值Str:{}",field.getName(),field.getType(),valueStr);
        }
        return null;
    }

    /**
     * 所有数据解析完成了 都会来调用
     * @param context
     */
    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        saveData();
    }

    /**
     * 加上存储数据库
     */
    public void saveData() {
        if (!CollectionUtils.isEmpty(list)) {
            insertData(list);
        }
    }


    /**
     * 插入数据库
     * @param list
     */
    public void insertData(List<Object> list) {
        log.info("{}-db batchSave size :{}",clazzref,list.size());
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        if(TableDOEnum.USER.getName().equals(TableDOEnum.getNameByClazz(clazzref))){
            CollegeService collegeService = SpringContextUtil.getContext().getBean(CollegeService.class);
            //insert to db
            list.stream().forEach(o->{
                log.error("插入失败！！！！！！！！！！！！！");
                log.info("通用上传（TODO save db）:{}",new Gson().toJson(o));
            });
        }
    }


}