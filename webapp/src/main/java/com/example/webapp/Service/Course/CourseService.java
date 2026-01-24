package com.example.webapp.Service.Course;

import com.example.webapp.DO.*;
import com.example.webapp.DTO.CourseDTO;
import com.example.webapp.DTO.CourseSectionDTO;
import com.example.webapp.query.CourseQuery;
import com.example.webapp.query.UserLearnRecordQuery;
import com.example.webapp.result.Result;
import com.example.webapp.result.ResultPage;
import com.github.pagehelper.PageInfo;
import org.springframework.stereotype.Service;

@Service
public interface CourseService {

    /**
     * 用户端课程详情
     * @param query
     * @return
     */
    CourseDTO viewPortal(CourseQuery query);

    /**
     * 收藏
     * @param userFavoriteRefDO
     * @return
     */
    Result favorite(UserFavoriteRefDO userFavoriteRefDO);

    /**
     * 取消收藏
     * @param userFavoriteRefDO
     * @return
     */
    Result cancelFavorite(UserFavoriteRefDO userFavoriteRefDO);

    /**
     * 分页查询
     * @param query
     * @return
     */
    ResultPage list(CourseQuery query);

    /**
     * 关联课程--课程列表(弹层选择课程列表)
     * @param query
     * @return
     */
    ResultPage selectList(CourseQuery query);


    /**
     * 获取机构已购课程列表
     * @param query
     * @return
     */
    PageInfo findMechanismCourseList(CourseQuery query);

    /**
     * 用户端课程列表（机构购买&自建）
     * @param query
     * @return
     */
    PageInfo findPortalCourseList(CourseQuery query);


    /**
     * 新建代码帮助
     * @param meCourseDO
     * @return
     */
    Result insert(CourseDO meCourseDO);

    /**
     * 更新代码帮助
     * @param meCourseDO
     * @return
     */
    Result update(CourseDO meCourseDO);

    /**
     * 删除代码帮助
     * @param id
     * @return
     */
    Result delete(int id);

    /**
     * 更新状态(停用/启用)
     * @param id
     * @param status
     * @return
     */
    Result updateStatus(int id, int status);

    /**
     * 编辑查看
     * @param id
     * @return
     */
    CourseDTO view(int id);

    CourseDTO view(int id, Integer mechanismId);

    /**
     * 课程关联产品包
     * @param associateCoursePackageDO
     * @return
     */
    Result associateCoursePackage(AssociateCoursePackageDO associateCoursePackageDO);

    /**
     * 课程已关联产品包
     * @param courseId
     * @return
     */
    Result associatedCoursePackage(int courseId);

    /**
     * 课节详情
     * @param sectionId
     * @return
     */
    Result<CourseSectionDTO> viewSection(int sectionId);

    /**
     * 开通机构时-初始化机构课程解锁模式
     * @param mechanismId
     * @param coursePackageId
     */
    void initMechanismCourseUnlock(Integer mechanismId, Integer coursePackageId);

    /**
     * 判断是否购买该课程
     * @param mechanismId
     * @param courseId
     * @return
     */
    boolean isBuyCourse(Integer mechanismId, Integer courseId);

    /**
     * 机构端设置课程模式
     * @param mechanismId
     * @param courseId
     * @param mode
     * @return
     */
    Result setCourseMode(Integer mechanismId, Integer courseId, Integer mode);

    /** 保存听课时长： 视频播放80%时，前端请求带上课节完成标志 complete=1
     *      1。更新课程 的 完成状态/进度/总学习时长
     *      2。更新课程章节 的 完成状态/总学习时长
     * @param detail
     * @return
     */
    Result insertOrUpdateLearnRecordDetail(StUserLearnRecordDetailDO detail);

    /**
     * 继续学习--获取最后一次学习课节id
     * @param query
     * @return
     */
    Integer lastCourseSectionId(CourseQuery query);

    /**
     * 已学习课程列表
     * @param query
     * @return
     */
    ResultPage learnCourselist(UserLearnRecordQuery query);

    /**
     * 查看机构课程试看状态
     * @param mechanismId
     * @return
     */
    Integer getTryStatusOfMechanism(Integer mechanismId);

    Result insertOrUpdateLearnRecordDetailIP(StUserLearnRecordDetailDOIP detail);

}
