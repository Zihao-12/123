package com.example.webapp.utils;

import com.google.common.collect.Sets;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;

/**
 * @author wujun
 * @description
 * @date 2020/11/1
 */
public class ListUtil {

    /**
     * 获取操作对象
     * @param newRefIdList     新对象集合
     * @param existedRefIdList 已存在的对象集合
     * @param <E>
     * @return
     */
    public static <E> ListOperateDTO<E> getListOperateDTO(List<E> newRefIdList, List<E> existedRefIdList){
        ListOperateDTO<E> op = new ListOperateDTO<>();
        if(CollectionUtils.isEmpty(existedRefIdList)){
            op.setAddList(newRefIdList);
            return op;
        }
        //交集
        List<E> intersection = intersection(newRefIdList,existedRefIdList);
        //删除
        List<E> deleteList = symmetricDifference(existedRefIdList, intersection);
        //新增
        List<E> addList = symmetricDifference(newRefIdList, intersection);
        op.setAddList(addList);
        op.setDeleteList(deleteList);
        return op;
    }


    /**
     * 交集
     * @param list1
     * @param list2
     * @param <E>
     * @return
     */
    public static <E> List<E> intersection(List<E> list1, List<E> list2){
        List<E> resultList = new ArrayList<>();
        Set set1 = new HashSet();
        Set set2 = new HashSet();
        list1.forEach(o->set1.add(o));
        list2.forEach(o->set2.add(o));
        Sets.SetView result = Sets.intersection(set1, set2);
        result.forEach(r->resultList.add((E)r));
        return resultList;
    }

    /**
     * 并集
     * @param list1
     * @param list2
     * @param <E>
     * @return
     */
    public static <E> List<E> union(List<E> list1, List<E> list2){
        List<E> resultList = new ArrayList<>();
        Set set1 = new HashSet();
        Set set2 = new HashSet();
        list1.forEach(o->set1.add(o));
        list2.forEach(o->set2.add(o));
        Sets.SetView result = Sets.union(set1, set2);
        result.forEach(r->resultList.add((E)r));
        return resultList;
    }

    /**
     * 补集
     * @param list1
     * @param list2
     * @param <E>
     * @return
     */
    public static <E> List<E> difference(List<E> list1, List<E> list2){
        List<E> resultList = new ArrayList<>();
        Set set1 = new HashSet();
        Set set2 = new HashSet();
        list1.forEach(o->set1.add(o));
        list2.forEach(o->set2.add(o));
        Sets.SetView result = Sets.difference(set1, set2);
        result.forEach(r->resultList.add((E)r));
        return resultList;
    }

    /**
     * 差集
     * @param list1
     * @param list2
     * @param <E>
     * @return
     */
    public static <E> List<E> symmetricDifference(List<E> list1, List<E> list2){
        List<E> resultList = new ArrayList<>();
        Set set1 = new HashSet();
        Set set2 = new HashSet();
        list1.forEach(o->set1.add(o));
        list2.forEach(o->set2.add(o));
        Sets.SetView result = Sets.symmetricDifference(set1, set2);
        result.forEach(r->resultList.add((E)r));
        return resultList;
    }

    /**
     * 正序排序
     * @param list
     * @param sortField
     */
    public static void sortList(List list, String... sortField){
        sortList( list,true,  sortField);
    }

    /**
     *引用对象集合排序 不同字段同一排序规则
     * @param sortFieldList 排序对象属性名 数组
     *                 排序字段支持的类型：String,Date,Integer,Double,必须要有对应的get方法
     * @param asc       asc fase倒叙
     * @param list
     */
    public static void sortList(List list, final boolean asc, final String... sortFieldList){
        if(CollectionUtils.isNotEmpty(list)){
            Collections.sort(list, new Comparator<Object>() {
                @Override
                public int compare(Object obj1, Object obj2) {
                    int compare=0;
                    try {
                        Class clazz=obj1.getClass();
                        List<Method> methodList=getSetMethodOfField( clazz,sortFieldList);
                        for(Method method:methodList){
                            compare = getSortCompare(obj1, obj2, method, asc);
                            if(compare==0){
                                continue;
                            }else {
                                break;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return compare;
                }
            });
        }
    }

    /**
     *引用对象集合排序 不同字段可设置不同的排序规则
     * @param sortFields 排序对象属性名 集合
     *                 排序字段支持的类型：String,Date,Integer,Double,必须要有对应的get方法
     * @param list
     */
    public static void sortList(List list, final List<SortField> sortFields){
        if(CollectionUtils.isNotEmpty(list)){
            Collections.sort(list, new Comparator<Object>() {
                @Override
                public int compare(Object obj1, Object obj2) {
                    int compare=0;
                    try {
                        Class clazz=obj1.getClass();
                        List<SortField> sortFieldedList=parseSortFields( clazz,sortFields);
                        for(SortField sortField:sortFieldedList){
                            Method method=sortField.getMethod();
                            compare = getSortCompare(obj1, obj2, method, sortField.isAsc());
                            if(compare==0){
                                continue;
                            }else {
                                break;
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return compare;
                }
            });
        }
    }

    private static List<SortField> parseSortFields(Class clazz, List<SortField> sortFields) throws NoSuchMethodException {
        Field[] fields=clazz.getDeclaredFields();
        if(fields==null || fields.length==0 ||CollectionUtils.isEmpty(sortFields)){
            return null;
        }
        for(SortField sortField:sortFields){
            for (Field field:fields){
                if(field.getName().equals(sortField.getSortName())){
                    String methodStr="get"+ StringUtils.capitalize(sortField.getSortName());
                    Method method = clazz.getMethod(methodStr);
                    sortField.setMethod(method);
                    break;
                }
            }
        }
        return sortFields;
    }

    private static List<Method> getSetMethodOfField(Class clazz,String[] sortFieldList) throws NoSuchMethodException {
        List<Method> setMethodList=new ArrayList<Method>();
        Field[] fields=clazz.getDeclaredFields();
        if(fields==null || fields.length==0
                ||sortFieldList==null || sortFieldList.length==0){
            return setMethodList;
        }
        for(String sortField:sortFieldList){
            for (Field field:fields){
                if(field.getName().equals(sortField)){
                    String methodStr="get"+ StringUtils.capitalize(sortField);
                    Method method = clazz.getMethod(methodStr);
                    setMethodList.add(method);
                    break;
                }
            }
        }
        return setMethodList;
    }
    private static int getSortCompare(Object obj1, Object obj2, Method method, boolean asc) throws Exception {
        int compare=0;
        Object result1=method.invoke(obj1);
        Object result2=method.invoke(obj2);
        Object c1=result1,c2=result2;
        if(!asc){
            c1=result2;c2=result1;
        }
        if(c1!=null&&c2!=null){
            if(c1 instanceof String){
                String s1= (String) c1;
                String s2= (String) c2;
                compare=s1.compareTo(s2);
            }
            if(c1 instanceof Date){
                Date s1= (Date) c1;
                Date s2= (Date) c2;
                compare=s1.compareTo(s2);
            }
            if(c1 instanceof Integer){
                Integer s1= (Integer) c1;
                Integer s2= (Integer) c2;
                compare=s1.compareTo(s2);
            }
            if(c1 instanceof Double){
                Double s1= (Double) c1;
                Double s2= (Double) c2;
                compare=s1.compareTo(s2);
            }
        }
        return compare;
    }
}
