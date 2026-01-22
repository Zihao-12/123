package com.example.webapp.utils;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.read.listener.PageReadListener;
import com.alibaba.excel.write.metadata.WriteSheet;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.ibatis.cursor.Cursor;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * @author gehaisong
 */
@Slf4j
public class EasyExcelUtils<T> {

    public static final String JAVA_AWT_HEADLESS = "java.awt.headless";
    public static final String TRUE = "true";
    private static int SUB_LIST_SIZE =1000;

    /**
     * EasyExcel文件上传
     * <p>1. 创建excel对应的实体对象 参照{@link EasyDO}
     * <p>2. 由于默认一行行的读取excel，所以需要创建excel一行一行的回调监听器，参照{@link PageReadListener}
     *        JDK8+ , (since: 3.0.0-beta1 可以用默认回调监听器，不用额外创建)
     * <p>3. 直接读即可
     */
    public static void readExcel(InputStream inputStream) throws IOException {
        EasyExcel.read(inputStream, EasyDO.class, new PageReadListener<EasyDO>(dataList -> {
            for (EasyDO data : dataList) {
                log.info("xx读取到一条数据{}", new Gson().toJson(data));
            }
        })).sheet().doRead();
    }

    /**
     * 文件下载
     * @param sheetList
     * @param response
     */
    public static void exportExcel(List<EasySheet> sheetList, HttpServletResponse response) {
        try {
            System.setProperty(JAVA_AWT_HEADLESS, TRUE);
            if(CollectionUtils.isNotEmpty(sheetList)){
                ExcelWriter excelWriter = EasyExcel.write(getExcelOutputStream(sheetList.get(0).fileName, response)).build();
                sheetList.stream().forEach(sheet->{
                    WriteSheet writeSheet = EasyExcel.writerSheet(sheet.sheetName).build();
                    writeSheet.setHead(getExcelHead(sheet.getHeaders()));
                    if(CollectionUtils.isNotEmpty(sheet.getDataList())){
                        List<? extends List<?>> subLists = Lists.partition(sheet.getDataList(), SUB_LIST_SIZE);
                        subLists.stream().forEach(subList->{
                            excelWriter.write(subList,writeSheet);
                        });
                    }else {
                        excelWriter.write(sheet.getDataList(), writeSheet);
                    }
                });
                excelWriter.finish();
            }
        }catch (Exception e){
            log.error("excel下载失败:{}", ExceptionUtils.getStackTrace(e));
        }
    }

    /**
     * 文件下载-流式
     *         Cursor<EasyDO> cursor = easyMpper.getDataList(query);
      * @param cursor
     * @param response
     * @param fileName
     * @param easySheet
     * @throws Exception
     */
    public void cursorExportExcel(Cursor<T> cursor, HttpServletResponse response, String fileName, EasySheet easySheet) throws Exception {
        System.setProperty(JAVA_AWT_HEADLESS, TRUE);
        ExcelWriter excelWriter = EasyExcel.write(getExcelOutputStream(fileName, response)).build();
        WriteSheet writeSheet = EasyExcel.writerSheet(fileName).build();
        writeSheet.setHead(getExcelHead(easySheet.getHeaders()));
        List<T> list = new ArrayList<>();
        //计数
        int count = 0;
        for (T dto : cursor) {
            converAttribute(dto);
            list.add(dto);
            if(count> SUB_LIST_SIZE){
                excelWriter.write(list,writeSheet);
                count = 0;
                list.clear();
            }
            count++;
        }
        //剩余部分数据写入文件
        excelWriter.write(list,writeSheet);
        excelWriter.finish();
    }

    private void converAttribute(T dto) {
        if(dto instanceof EasyDO){
        }
    }


    /**
     * 导出文件时为Writer生成OutputStream
     *
     * @param fileName
     * @param response
     * @return
     */
    private static OutputStream getExcelOutputStream(String fileName, HttpServletResponse response) throws Exception {
        try {
            fileName = URLEncoder.encode(fileName, "UTF-8");
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("utf8");
            response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + fileName + ".xlsx");
            response.setHeader("Pragma", "public");
            response.setHeader("Cache-Control", "no-store");
            response.addHeader("Cache-Control", "max-age=0");
            return response.getOutputStream();
        } catch (IOException e) {
            throw new Exception("导出excel表格失败!", e);
        }
    }

    /**
     * easy 表头
     * @return
     */
    private static List<List<String>> getExcelHead(String[] headList){
        List<List<String>> headResult = new ArrayList<List<String>>();
        for (String s : headList) {
            List<String> head = new ArrayList<>();
            head.add(s);
            headResult.add(head);
        }
        return headResult;
    }

//    @LoginRequired(platform= PlatformMarkEnum.MECHANISM)
//    @ApiOperation(value = "导出班级学生列表", notes = "导出班级学生列表")
//    @PostMapping(value = "down-student-list/{classId}")
//    public void downStudentList (@PathVariable Integer classId, HttpServletResponse response){
//        Integer mechanismId = UserThreadLocal.get().getMechanismId();
//        if(!collegeService.isBelongToMechanism(mechanismId,classId)){
//            return;
//        }
//        try {
//            EasySheet sheet =new EasySheet();
//            sheet.setFileName(classId+"-班级学生列表");
//            sheet.setSheetName("学生列表");
//            sheet.setHeaders(new String[]{"ID","用户名", "姓名","学号/工号","手机号"});
//            List<UserDO> list =  collegeService.findUserByClassIdAndPositionType(classId, ClassPositionEnum.STUDENT.getType());
//            sheet.setDataList(list);
//            List<EasySheet> sheetList =  Lists.newArrayList();
//            sheetList.add(sheet);
//            EasyExcelUtils.exportExcel(sheetList,response);
//        }catch (Exception e){
//            log.error("excel下载失败:{}", ExceptionUtils.getStackTrace(e));
//        }
//    }
}
