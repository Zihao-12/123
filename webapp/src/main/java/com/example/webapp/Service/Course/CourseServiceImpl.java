package com.example.webapp.Service.Course;

import com.example.webapp.DTO.CourseDTO;
import com.example.webapp.DTO.CoursePackageDTO;
import com.example.webapp.DTO.ObjectCategoryDTO;
import com.example.webapp.Mapper.CoursePackage.CoursePackageMapper;
import com.example.webapp.query.CourseQuery;
import com.example.webapp.common.Constant;
import com.example.webapp.enums.CourseSectionTypeEnum;
import com.example.webapp.enums.CourseTypeEnum;
import com.example.webapp.enums.ObjectTypeEnum;
import com.example.webapp.enums.UpDownStatusEnum;
import com.example.webapp.result.ResultPage;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CourseServiceImpl implements Serializable, CourseService {
    @Autowired
    private CourseMapper courseMapper;
    @Autowired
    private CoursePackageMapper coursePackageMapper;



    public ResultPage list(CourseQuery query){
        PageInfo pageInfo = null;
        try {
            query.setPageNo(query.getPageNo()==null?0:query.getPageNo());
            query.setPageSize(query.getPageSize()==null? Constant.PAGE_SIZE :query.getPageSize());
            if (StringUtils.isNotBlank(query.getName())) {
                query.setName("%" + query.getName() + "%");
            }
            if(query.getType()== null){
                query.setType(CourseTypeEnum.VIDEO.getType());
            }
            PageHelper.startPage(query.getPageNo(), query.getPageSize());
            List<CourseDTO> list = courseMapper.selectAll(query);
            pageInfo = new PageInfo(list);
            if (CollectionUtils.isEmpty(list)) {
                return ResultPage.ok(pageInfo.getList(), pageInfo.getPageNum(), pageInfo.getPageSize(), pageInfo.getTotal());
            }
            List<Integer> courseIdList = list.stream().map(CourseDTO::getId).collect(Collectors.toList());
            Map<Integer, ObjectCategoryDTO> categoryMap = categoryService.getObjectCategoryMap(courseIdList, ObjectTypeEnum.VIDEO.getType());
            //查询课节数
            List<CourseDTO> sectionNumList = courseMapper.listSectionNum(courseIdList, CourseSectionTypeEnum.SECTION.getType());
            Map<Integer, Integer> sectionNumMap = convertSectionNumToMap(sectionNumList);
            //查询课程包列表
            List<CoursePackageDTO> packageList = courseMapper.selectPackageList(courseIdList);
            Map<Integer, List<CoursePackageDTO>> packageMap = convertPackageToMap(packageList);
            list.forEach(p -> {
                p.setTypeCn(CourseTypeEnum.getTypeName(p.getType()));
                List<CoursePackageDTO> linPackageList = packageMap.get(p.getId());
                p.setCoursePackageList(linPackageList);
                p.setPackageNumbers(linPackageList == null ? 0 : linPackageList.size());
                p.setStatusCn(UpDownStatusEnum.getUpDownStatusName(p.getStatus()));
                Integer sectionNum = sectionNumMap.get(p.getId());
                p.setCourseSectionNumber(sectionNum==null?0:sectionNum);
                if(categoryMap.get(p.getId()) != null){
                    p.setIdFullPathList(parseStrToStringList(categoryMap.get(p.getId()).getIdFullPaths()));
                    p.setNameFullPathList(parseStrToStringList(categoryMap.get(p.getId()).getNameFullPaths()));
                }
            });
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}
