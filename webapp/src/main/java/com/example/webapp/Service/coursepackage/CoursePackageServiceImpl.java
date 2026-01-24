package com.example.webapp.Service.coursepackage;

import com.example.webapp.DO.CoursePackageDO;
import com.example.webapp.DO.CoursePackageRefDO;
import com.example.webapp.DTO.CoursePackageDTO;
import com.example.webapp.Mapper.CourseMapper;
import com.example.webapp.Query.CoursePackageQuery;
import com.example.webapp.common.Constant;
import com.example.webapp.enums.CoursePackageStatusEnum;
import com.example.webapp.enums.CourseTryStatusEnum;
import com.example.webapp.enums.UpDownStatusEnum;
import com.example.webapp.result.Result;
import com.example.webapp.result.ResultPage;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
public class CoursePackageServiceImpl implements CoursePackageService, Serializable {
    @Autowired
    private CoursePackageMapper coursePackageMapper;
    @Autowired
    private CourseMapper courseMapper;
    @Autowired
    CategoryService categoryService;

    public CoursePackageMapper getCoursePackageMapper() {
        return coursePackageMapper;
    }

    public void setCoursePackageMapper(CoursePackageMapper coursePackageMapper) {
        this.coursePackageMapper = coursePackageMapper;
    }

    @Override
    public ResultPage list(CoursePackageQuery query) {
        PageInfo pageInfo = null;
        try {
            query.setPageNo(query.getPageNo()==null?0:query.getPageNo());
            query.setPageSize(query.getPageSize()==null? Constant.PAGE_SIZE :query.getPageSize());
            if(StringUtils.isNotBlank(query.getName())){
                query.setName("%"+query.getName()+"%");
            }
            PageHelper.startPage(query.getPageNo(), query.getPageSize());
            List<CoursePackageDTO> list = coursePackageMapper.selectAll(query);
            pageInfo = new PageInfo(list);
            if (CollectionUtils.isEmpty(list)) {
                return ResultPage.ok(pageInfo.getList(), pageInfo.getPageNum(), pageInfo.getPageSize(), pageInfo.getTotal());
            }
            list.forEach(p->{
                p.setTryStatusCn(CourseTryStatusEnum.getTryStatusName(p.getTryStatus()));
                p.setStatusCn(UpDownStatusEnum.getUpDownStatusName(p.getStatus()));
                p.setUsedCn(CoursePackageStatusEnum.getStatusName(p.getUsed()));
            });
        } catch (Exception e) {
            log.error("{}", ExceptionUtils.getStackTrace(e));
            throw e;
        }
        return ResultPage.ok(pageInfo.getList(), pageInfo.getPageNum(), pageInfo.getPageSize(), pageInfo.getTotal());
    }

    @Override
    public ResultPage selectList(CoursePackageQuery query) {
        PageInfo pageInfo = null;
        try {
            query.setPageNo(query.getPageNo()==null?0:query.getPageNo());
            query.setPageSize(query.getPageSize()==null? Constant.PAGE_SIZE :query.getPageSize());
            if(StringUtils.isNotBlank(query.getName())){
                query.setName("%"+query.getName()+"%");
            }
            PageHelper.startPage(query.getPageNo(), query.getPageSize());
            List<CoursePackageDTO> list = coursePackageMapper.selectList(query);
            pageInfo = new PageInfo(list);
            if (CollectionUtils.isEmpty(list)) {
                return ResultPage.ok(pageInfo.getList(), pageInfo.getPageNum(), pageInfo.getPageSize(), pageInfo.getTotal());
            }
            list.forEach(p->{
                p.setTryStatusCn(CourseTryStatusEnum.getTryStatusName(p.getTryStatus()));
                p.setStatusCn(UpDownStatusEnum.getUpDownStatusName(p.getStatus()));
                p.setUsedCn(CoursePackageStatusEnum.getStatusName(p.getUsed()));
            });
        } catch (Exception e) {
            log.error("{}",ExceptionUtils.getStackTrace(e));
            throw e;
        }
        return ResultPage.ok(pageInfo.getList(), pageInfo.getPageNum(), pageInfo.getPageSize(), pageInfo.getTotal());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result insert(CoursePackageDO meCoursePackageDO) {
        int count = 0;
        try {
            //课程包课程数量
            List<CoursePackageRefDO> refList = meCoursePackageDO.getCoursePackageRefList();
            meCoursePackageDO.setType(1);
            meCoursePackageDO.setUsed(CoursePackageStatusEnum.NOT_USED.status);
            meCoursePackageDO.setStatus(UpDownStatusEnum.UP.getStatus());
            count = coursePackageMapper.insert(meCoursePackageDO);
            insertCoursePackageRef(refList,meCoursePackageDO.getId());
        } catch (Exception e) {
            log.error("{}",ExceptionUtils.getStackTrace(e));
            throw e;
        }
        return Result.ok(count);
    }

    private void insertCoursePackageRef(List<CoursePackageRefDO> coursePackageRefList, Integer packageId) {
        if(CollectionUtils.isEmpty(coursePackageRefList)){
            return;
        }
        AtomicInteger sort = new AtomicInteger(1);
        coursePackageRefList.forEach(p->{
            p.setCoursePackageId(packageId);
            p.setSort(sort.getAndIncrement());
        });
        coursePackageMapper.insertCoursePackageRef(coursePackageRefList);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result update(CoursePackageDO meCoursePackageDO) {
        int count = 0;
        try {
            //课程包课程数量
            List<CoursePackageRefDO> refList = meCoursePackageDO.getCoursePackageRefList();
            count = coursePackageMapper.update(meCoursePackageDO);
            coursePackageMapper.deleteCoursePackageRef(meCoursePackageDO.getId());
            insertCoursePackageRef(refList, meCoursePackageDO.getId());
        } catch (Exception e) {
            log.error("{}",ExceptionUtils.getStackTrace(e));
            throw e;
        }
        return Result.ok(count);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result delete(int id) {
        int count = 0;
        try {
            //删除课程包
            count = coursePackageMapper.delete(id);
            //删除课程与课程包关联
            coursePackageMapper.deleteCoursePackageRef(id);
        } catch (Exception e) {
            log.error("{}",ExceptionUtils.getStackTrace(e));
            throw e;
        }
        return Result.ok(count);
    }

    @Override
    public Result view(int id) {
        CoursePackageDTO dto = null;
        try {
            dto = coursePackageMapper.view(id);
            if(dto==null){
                dto = new CoursePackageDTO();
            }else{
                dto.setTryStatusCn(CourseTryStatusEnum.getTryStatusName(dto.getTryStatus()));
                //查询课程包课程
                List<Integer> courseIdList = coursePackageMapper.getCourseIdListByPackageId(id);
                if (CollectionUtils.isNotEmpty(courseIdList)) {
                    List<CourseDTO> courseList = courseMapper.findCourseListByIdList(courseIdList);
                    Map<Integer, ObjectCategoryDTO> categoryMap = categoryService.getObjectCategoryMap(courseIdList,ObjectTypeEnum.VIDEO.getType());
                    courseList.forEach(c->{
                        c.setTypeCn(CourseTypeEnum.getTypeName(c.getType()));
                        if(categoryMap.get(c.getId()) != null){
                            c.setIdFullPathList(parseStrToStringList(categoryMap.get(c.getId()).getIdFullPaths()));
                            c.setNameFullPathList(parseStrToStringList(categoryMap.get(c.getId()).getNameFullPaths()));
                        }
                    });
                    //查询课节数
                    List<CourseDTO> sectionNumList = courseMapper.listSectionNum(courseIdList, CourseSectionTypeEnum.SECTION.getType());
                    Map<Integer, Integer> sectionNumMap = convertSectionNumToMap(sectionNumList);
                    courseList.forEach(p -> {
                        Integer sectionNum = sectionNumMap.get(p.getId());
                        p.setCourseSectionNumber(sectionNum==null?0:sectionNum);
                    });
                    dto.setCourseList(courseList);
                }
            }
        } catch (Exception e) {
            log.error("{}",ExceptionUtils.getStackTrace(e));
            throw e;
        }
        return Result.ok(dto);
    }

    /**
     * 课节数量转map
     * @param sectionNumList
     * @return
     */
    private Map<Integer, Integer> convertSectionNumToMap(List<CourseDTO> sectionNumList) {
        Map<Integer, Integer> result = new HashMap<>();
        if (CollectionUtils.isEmpty(sectionNumList)) {
            return result;
        }
        for (CourseDTO dto : sectionNumList) {
            result.put(dto.getId(),dto.getCourseSectionNumber());
        }
        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result disassociate(int packageId, int courseId) {
        int count = 0;
        try {
            count = coursePackageMapper.disassociate(packageId,courseId);
        } catch (Exception e) {
            log.error("{}",ExceptionUtils.getStackTrace(e));
            throw e;
        }
        return Result.ok(count);
    }

    @Override
    public Result updateSale(Integer id, Integer status) {
        int count = 0;
        try {
            count = coursePackageMapper.updateSale(id, status);
        } catch (Exception e) {
            log.error("{}",ExceptionUtils.getStackTrace(e));
            throw e;
        }
        return Result.ok(count);
    }

    @Override
    public Result updateTrySale(Integer id, Integer status) {
        int count = 0;
        try {
            if(CourseTryStatusEnum.ALL.getType()>status || CourseTryStatusEnum.TRY_FIRST_THREE.getType()< status){
                return Result.fail("status:"+status);
            }
            count = coursePackageMapper.updateTrySale(id, status);
        } catch (Exception e) {
            log.error("{}",ExceptionUtils.getStackTrace(e));
            throw e;
        }
        return Result.ok(count);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Result copyPackage(int id) {
        CoursePackageDO packageDO = getPackageDO(id);
        List<CoursePackageRefDO> coursePackageRefList = coursePackageMapper.listCoursePackageRef(id);
        coursePackageMapper.insert(packageDO);
        int count = 0;
        if(CollectionUtils.isNotEmpty(coursePackageRefList)){
            coursePackageRefList.forEach(p->p.setCoursePackageId(packageDO.getId()));
            count = coursePackageMapper.insertCoursePackageRef(coursePackageRefList);
        }
        return Result.ok(count);
    }

    private CoursePackageDO getPackageDO(int id) {
        String suffix = "副本-";
        CoursePackageDTO packageDTO = coursePackageMapper.view(id);
        CoursePackageDO packageDO = new CoursePackageDO();
        packageDO.setName(suffix+packageDTO.getName());
        packageDO.setIntroduction(packageDTO.getIntroduction());
        packageDO.setStatus(packageDTO.getStatus());
        packageDO.setType(packageDTO.getType());
        packageDO.setUsed(CoursePackageStatusEnum.NOT_USED.status);
        packageDO.setIsDelete(packageDTO.getIsDelete());
        return packageDO;
    }
    private List<String> parseStrToStringList(String names) {
        List<String> nameList = Lists.newArrayList();
        if(StringUtils.isNotBlank(names)){
            nameList = Arrays.asList(names.split(","));
        }
        return nameList;
    }
}
