package com.example.webapp.Service.coursepackage;

import com.example.webapp.DO.CoursePackageDO;
import com.example.webapp.DO.CoursePackageRefDO;
import com.example.webapp.DTO.CourseDTO;
import com.example.webapp.DTO.CoursePackageDTO;
import com.example.webapp.Query.CoursePackageQuery;
import com.example.webapp.result.ResultPage;
import org.apache.ibatis.annotations.*;
import org.springframework.stereotype.Service;

import java.util.List;


@Service
@Mapper
public interface CoursePackageService {
    /**
     * 分页查询
     * @param query
     * @return
     */
    ResultPage list(CoursePackageQuery query);

    /**
     * 弹窗--课程包列表
     * @param query
     * @return
     */
    ResultPage selectList(CoursePackageQuery query);

    /**
     * 新建代码帮助
     * @param meCoursePackageDO
     * @return
     */
    Result insert(CoursePackageDO meCoursePackageDO);

    /**
     * 更新代码帮助
     * @param meCoursePackageDO
     * @return
     */
    Result update(CoursePackageDO meCoursePackageDO);

    /**
     * 删除代码帮助
     * @param id
     * @return
     */
    Result delete(int id);

    /**
     * 编辑查看
     * @param id
     * @return
     */
    Result view(int id);

    /**
     * 取消关联
     *
     * @param packageId
     * @param courseId
     * @return
     */
    Result disassociate(int packageId, int courseId);

    /**
     * 上下架
     * @param id
     * @param status
     * @return
     */
    Result updateSale(Integer id, Integer status);

    /**
     * 修改试看设置
     * @param id
     * @param status
     * @return
     */
    Result updateTrySale(Integer id, Integer status);


    /**
     * 复制包
     * @param id
     * @return
     */
    Result copyPackage(int id);
}
