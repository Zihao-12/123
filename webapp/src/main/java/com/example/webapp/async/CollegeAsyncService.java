package com.example.webapp.async;

import com.example.webapp.DTO.CourseSectionDTO;
import com.example.webapp.third.AliOSS;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * ***************使用此线程类,需要在调用类添加@Lazy********************
 * @Async无效的问题 -异步方法和调用方法一定要 写在不同的类中
 */
@Lazy
@Component
@Slf4j
public class CollegeAsyncService {

    @Async("webappThreadPool")
    public void batchDeleteCourseFile(String cover, List<CourseSectionDTO> sectionList) {
        try {
            log.info("线程池-删除阿里云课程封面及附件");
            AliOSS.deleteObject(cover);
            if (CollectionUtils.isNotEmpty(sectionList)) {
                sectionList.forEach(section->{
                    try {
                        //删除章节附件
                        AliOSS.deleteObject(section.getCourseware());
                    }catch (Exception e){
                        log.error("{}", ExceptionUtils.getStackTrace(e));
                    }
                });
            }
        } catch (Exception e) {
            log.error(ExceptionUtils.getStackTrace(e));
        }
    }



}
