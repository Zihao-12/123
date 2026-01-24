package com.example.webapp.Service.college;

import com.example.webapp.DO.CollegeDO;
import com.example.webapp.DO.UserDO;
import com.example.webapp.DTO.CreateCollegeNodeDTO;
import com.example.webapp.DTO.SelectUserBindDTO;
import com.example.webapp.query.UserQuery;
import com.example.webapp.result.Result;
import com.github.pagehelper.PageInfo;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CollegeService {


    Result findCollegeNodeByMechanismId(Integer mechanismId);

    CollegeDO findNodeById(Integer nodeId);

    Result findOffspringNodeByParentId(Integer nodeId);

    Result findChildNodeByParentId(Integer nodeId);

    Result createMechanismCollegeRoot(Integer mechanismId, String mechanismName);

    Result deleteCollegeNode(Integer id);

    @Transactional(rollbackFor = Exception.class)
    Result updateCollegeNodeName(Integer id, String name);

    /**
     * 创建结构组织结构结点
     * @param parentId
     * @param name
     * @param description
     * @param nodeType 0院系 1班级
     * @return
     */
    Result createCollegeNode(Integer parentId, String name, String description, Integer nodeType);
    /**
     * 批量创建结构组织结构结点
     * @return
     */
    Result bulkCreateCollegeNode(CreateCollegeNodeDTO createCollegeNodeDTO);

    Result updateCollegeRootNodeName(Integer nodeId, String name);

    Result saveUserStudent(Integer mechanismId, Integer collegeId, UserDO userDO);

    Result saveUserTeacher(Integer mechanismId, UserDO userDO);

    Result findUserById(Integer userId);

    Result resetPwd(Integer userId);

    Result batchDeleteUserByIdList(List<Integer> userIdList);

    Result batchDeactivateUserByIdList(List<Integer> userIdList);

    Result deactivateUserById(Integer userId);

    PageInfo findUserList(UserQuery query);

    Result findDepartmentDetail(Integer nodeId);

    Result findClassDetail(Integer nodeId);


    /**
     * 选择用户绑定到班级
     * @return
     */
    Result selectUserBindCollege(SelectUserBindDTO selectUserBindDTO);

    Result detachUserCollege(Integer userId, Integer classId);

    Result bulkDetachUserCollege(SelectUserBindDTO selectUserBindDTO);

    @Transactional(rollbackFor = Exception.class)
    Result bulkChangeCollege(SelectUserBindDTO selectUserBindDTO);

    Result getMechanismCollegeRoot(Integer mechanismId);

    /**
     * 根据工号/学号查询用户
     * @param jobNumber
     * @return
     */
    UserDO findUserByJobNumber(Integer mechanismId, String jobNumber);

    /**
     * 查询绑定到班级到用户列表
     * @param classId
     * @param positionType 0学生 1老师2助教
     * @return
     */
    List<UserDO> findUserByClassIdAndPositionType(Integer classId, Integer positionType);

    boolean isBelongToMechanism(Integer mechanismId, Integer nodeId);

    boolean isUserBelongToMechanism(Integer mechanismId, Integer userId);
}
