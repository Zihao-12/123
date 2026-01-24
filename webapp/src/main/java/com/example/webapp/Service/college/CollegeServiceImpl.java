package com.example.webapp.Service.college;

import com.example.webapp.DO.CollegeDO;
import com.example.webapp.DO.CollegeUserRefDO;
import com.example.webapp.DO.UserDO;
import com.example.webapp.DTO.*;
import com.example.webapp.Mapper.Mechanism.MechanismMapper;
import com.example.webapp.Mapper.college.CollegeMapper;
import com.example.webapp.Mapper.user.UserMapper;
import com.example.webapp.async.CollegeAsyncService;
import com.example.webapp.common.Constant;
import com.example.webapp.common.redis.RedisKeyGenerator;
import com.example.webapp.common.redis.RedisLock;
import com.example.webapp.common.redis.RedisUtils;
import com.example.webapp.enums.*;
import com.example.webapp.query.UserNumQuery;
import com.example.webapp.query.UserQuery;
import com.example.webapp.result.CodeEnum;
import com.example.webapp.result.Result;
import com.example.webapp.utils.DateTimeUtil;
import com.example.webapp.utils.Md5Util;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Lazy
@Service
@Slf4j
public class CollegeServiceImpl implements CollegeService, Serializable {
    public static final int PAGE_SIZE = 20;
    //批量导入用户时，每个线程单次处理能力
    public static final int PER_THREAD_PROCESS_DATA_MAX_SIZE = 500;
    public static final String ROOT_ID_FIX = "MID";
    public static final String CRETE_MECHANISM_COLLEGE_DEPARTMENT = "CRETE_MECHANISM_COLLEGE_DEPARTMENT";
    public static final String CRETE_MECHANISM_COLLEGE_CLASS = "CRETE_MECHANISM_COLLEGE_CLASS";
    public static final String CRETE_MECHANISM_COLLEGE_ROOT = "CRETE_MECHANISM_COLLEGE_ROOT";
    public static final String FIND_COLLEGE_NODE_BY_MECHANISM_ID = "FIND_COLLEGE_NODE_BY_MECHANISM_ID";
    public static final String FIND_OFFSPRING_NODE_BY_PARENT_ID = "FIND_OFFSPRING_NODE_BY_PARENT_ID";
    public static final String FIND_CHILD_NODE_BY_PARENT_ID = "FIND_CHILD_NODE_BY_PARENT_ID";
    public static final String SAVE_USER = "SAVE_USER";
    public static final String UTF_8 = "utf-8";
    public static final String FIND_USER_LIST = "FIND_USER_LIST";
    public static final String FIND_USER_BY_ID = "FIND_USER_BY_ID";
    public static final String BATCH_DELETE_USER_BY_ID_LIST = "BATCH_DELETE_USER_BY_ID_LIST";
    public static final String BATCH_DEACTIVATE_USER_BY_ID_LIST = "BATCH_DEACTIVATE_USER_BY_ID_LIST";
    public static final String DEACTIVATE_USER_BY_ID = "DEACTIVATE_USER_BY_ID";
    public static final String RESET_PWD = "RESET_PWD";
    public static final String DELETE_COLLEGE_NODE = "DELETE_COLLEGE_NODE";
    public static final String UPDATE_COLLEGE_NODE_NAME = "UPDATE_COLLEGE_NODE_NAME";
    public static final String FIND_DEPARTMENT_DETAIL = "FIND_DEPARTMENT_DETAIL";
    public static final String ADD_USER_TO_CLASS = "ADD_USER_TO_CLASS";
    /**
     * 最大助教数量
     */
    public static final int MAX_ASSISTANT_NUM = 10;
    public static final int MAX_CREATE_NODE_SIZE = 20;
    public static final String SELECT_USER_BIND_COLLEGE = "SELECT_USER_BIND_COLLEGE";
    public static final int CLASS_TEACHER_MAX_NUM = 1;
    public static final int CLASS_ASSISTANT_MAX_NUM = 10;
    public static final String DETACH_USER_COLLEGE = "DETACH_USER_COLLEGE";
    public static final String BULK_DETACH_USER_COLLEGE = "BULK_DETACH_USER_COLLEGE";
    public static final String BULK_CHANGE_COLLEGE = "BULK_CHANGE_COLLEGE";
    public static final String GET_MECHANISM_COLLEGE_ROOT = "GET_MECHANISM_COLLEGE_ROOT";
    public static final String FIND_USER_BY_JOB_NUMBER = "FIND_USER_BY_JOB_NUMBER";
    public static final String FIND_USER_BY_CLASS_ID_AND_POSITION_TYPE = "FIND_USER_BY_CLASS_ID_AND_POSITION_TYPE";
    public static final String FIND_NODE_BY_ID = "findNodeById";
    @Autowired
    private CollegeMapper collegeMapper;
    @Autowired
    private MechanismMapper mechanismMapper;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private CollegeAsyncService collegeAsyncService;

    @Autowired
    private RedisUtils redisUtils;
    @Autowired
    private RedisLock redisLock;

    /**
     * 查询机构的学院组织结构
     * @param mechanismId
     * @return
     */
    @Override
    public Result findCollegeNodeByMechanismId(Integer mechanismId){
        String key = RedisKeyGenerator.getKey(CollegeService.class, FIND_COLLEGE_NODE_BY_MECHANISM_ID, mechanismId);
        List<CollegeDTO> nodeList = (List<CollegeDTO>) redisUtils.get(key);
        if(CollectionUtils.isEmpty(nodeList)){
            nodeList = collegeMapper.findCollegeNodeByMechanismId(mechanismId);
            redisUtils.set(key,nodeList, TimeUnit.MINUTES.toSeconds(1));
        }
        return Result.ok(nodeList);
    }

    /**
     * 判断节点是否是该机构
     * @param mechanismId
     * @param nodeId
     * @return
     */
    @Override
    public boolean isBelongToMechanism(Integer mechanismId, Integer nodeId) {
        CollegeDO collegeDO = findNodeById(nodeId);
        return collegeDO!=null && collegeDO.getMechanismId()!=null && collegeDO.getMechanismId().equals(mechanismId);
    }

    /**
     * 判断用户是否是该机构
     * @param mechanismId
     * @param userId
     * @return
     */
    @Override
    public boolean isUserBelongToMechanism(Integer mechanismId, Integer userId) {
        UserDO userDO= (UserDO) findUserById(userId).getData();
        return userDO!=null && userDO.getMechanismId()!=null && userDO.getMechanismId().equals(mechanismId);
    }

    /**
     * 根据ID查询结点
     * @return
     */
    @Override
    public CollegeDO findNodeById(Integer nodeId){
        String key = RedisKeyGenerator.getKey(CollegeService.class, FIND_NODE_BY_ID,  nodeId);
        CollegeDO dto = (CollegeDO) redisUtils.get(key);
        if(dto==null){
            dto = collegeMapper.findCollegeNodeById(nodeId);
            redisUtils.set(key,dto,TimeUnit.MINUTES.toSeconds(15));
        }
        return dto;
    }

    /**
     * 查询所有子孙节点
     * @param nodeId
     * @return
     */
    @Override
    public Result findOffspringNodeByParentId(Integer nodeId){
        CollegeDO coCollegeDO = collegeMapper.findCollegeNodeById(nodeId);
        //根据父id_full_path 模糊匹配
        String parentIdFullPath = coCollegeDO.getIdFullPath()+coCollegeDO.getId()+".%";
        String key = RedisKeyGenerator.getKey(CollegeService.class, FIND_OFFSPRING_NODE_BY_PARENT_ID, nodeId);
        List<CollegeDTO> nodeList = (List<CollegeDTO>) redisUtils.get(key);
        if(CollectionUtils.isEmpty(nodeList)){
            nodeList = collegeMapper.findOffspringNodeByParentIdFullPath(parentIdFullPath);
            redisUtils.set(key,nodeList,TimeUnit.MINUTES.toSeconds(1));
        }
        return Result.ok(nodeList);
    }

    /**
     * 查询所有子节点
     * @param nodeId
     * @return
     */
    @Override
    public Result findChildNodeByParentId(Integer nodeId){
        List<CollegeDTO> nodeList =  collegeMapper.findChildNodeByParentId(nodeId);
        return Result.ok(nodeList);
    }

    /**
     * 创建机构时 创建院系根结点
     * @param mechanismId
     * @return
     */
    @Override
    public Result createMechanismCollegeRoot(Integer mechanismId,String mechanismName){
        String key = RedisKeyGenerator.getKey(CollegeService.class, CRETE_MECHANISM_COLLEGE_ROOT, mechanismId);
        String token = redisLock.tryLock(key, TimeUnit.SECONDS.toMillis(5));
        try{
            if(token != null) {
                CollegeDO root = collegeMapper.findCollegeRootByMechanismId(mechanismId);
                if(root !=null){
                    return Result.fail(CodeEnum.FAILED.getValue(),"该机构根结点已存在,不可重复创建");
                }
                CollegeDO collegeDO = new CollegeDO();
                collegeDO.setName(mechanismName);
                collegeDO.setDescription(mechanismName);
                collegeDO.setMechanismId(mechanismId);
                collegeDO.setParentId(CollegeNodeTypeEnum.ROOT_PARENT_ID.getType());
                collegeDO.setIdFullPath(ROOT_ID_FIX +mechanismId+".");
                collegeDO.setNameFullPath(mechanismName);
                collegeDO.setNodeType(CollegeNodeTypeEnum.COLLEGE.getType());
                collegeDO.setSort(0);
                collegeDO.setChildType(CollegeChildTypeEnum.CREATE_DEPARTMENT.getType());
                collegeMapper.insertCollege(collegeDO);
                return Result.ok(collegeDO.getId());
            } else {
                log.info("重复提交");
            }
        }catch (Exception e){
            throw new RuntimeException(ExceptionUtils.getStackTrace(e));
        }finally {
            if(token!=null) {
                redisLock.unlock(key, token);
            }
        }
        return Result.fail(CodeEnum.FAILED.getValue(),"创建失败");
    }

    /**
     * 批量创建结构组织结构结点
     * @return
     */
    @Override
    public Result bulkCreateCollegeNode(CreateCollegeNodeDTO createCollegeNodeDTO) {
        Integer parentId = createCollegeNodeDTO.getParentId();
        Integer nodeType =createCollegeNodeDTO.getNodeType();
        List<Map<String,Object>> relist = Lists.newArrayList();
        if(CollectionUtils.isNotEmpty(createCollegeNodeDTO.getCollegeNameList())){
            if(createCollegeNodeDTO.getCollegeNameList().size()> MAX_CREATE_NODE_SIZE){
                return Result.fail(CodeEnum.FAILED.getValue(),"同时创建班级/部门数量不能超过20个");
            }
            createCollegeNodeDTO.getCollegeNameList().forEach(collegeName->{
                Map<String,Object> map = Maps.newConcurrentMap();
                Result result = this.createCollegeNode(parentId,collegeName,"",nodeType);
                map.put("code",result.getCode());
                map.put("name",collegeName);
                map.put("msg","success");
                if(CodeEnum.FAILED.getValue().equals(result.getCode())){
                    map.put("msg",result.getMessage());
                }else {
                    CollegeDO collegeDO = (CollegeDO) result.getData();
                    map.put("id",collegeDO.getId());
                }
                relist.add(map);
            });
        }
        return Result.ok(relist);
    }

    /**
     * 创建结构组织结构结点
     * @param parentId
     * @param name
     * @param description
     * @param nodeType 0院系 1班级
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Result createCollegeNode(Integer parentId, String name, String description, Integer nodeType){
        CollegeDO collegeDO = new CollegeDO();
        collegeDO.setParentId(parentId);
        collegeDO.setName(name);
        if(StringUtils.isEmpty(description)){
            description =name;
        }
        collegeDO.setDescription(description);
        collegeDO.setNodeType(nodeType);
        CollegeDO parentNode = collegeMapper.findCollegeNodeById(collegeDO.getParentId());
        if(parentNode==null){
            return Result.fail(CodeEnum.FAILED.getValue(),"父节点不可用");
        }else {
            CollegeDO childNode = collegeMapper.findChildNodeByNodeNameAndParentId(collegeDO.getName(),parentNode.getId());
            if(childNode!=null){
                return Result.fail(CodeEnum.FAILED.getValue(),"节点名已存在,请修改后在提交");
            }
        }
        if(CollegeNodeTypeEnum.COLLEGE.getType().equals(nodeType)){
            return createMechanismCollegeDepartment(collegeDO,parentNode);
        }else if(CollegeNodeTypeEnum.CLASS.getType().equals(nodeType)){
            return createMechanismCollegeClass(collegeDO,parentNode);
        }
        delNodeCache( parentId,  parentNode.getMechanismId());
        return Result.fail(CodeEnum.FAILED.getValue(),"创建失败");
    }

    /**
     * 创建机构院系部门
     * @return
     */
    public Result createMechanismCollegeDepartment(CollegeDO collegeDO, CollegeDO parentNode){
        String key = RedisKeyGenerator.getKey(CollegeService.class, CRETE_MECHANISM_COLLEGE_DEPARTMENT, collegeDO.getParentId());
        String token = redisLock.tryLock(key, TimeUnit.SECONDS.toMillis(3));
        try{
            if(token != null) {
                if(CollegeChildTypeEnum.CREATE_CLASS.getType().equals(parentNode.getChildType())){
                    return Result.fail(CodeEnum.FAILED.getValue(),"创建部门失败:暂不支持同一目录层级中，同时存在子部门和班级两种类型");
                }
                if(CollegeChildTypeEnum.NOT_CREATE_NODE.getType().equals(parentNode.getChildType())){
                    return Result.fail(CodeEnum.FAILED.getValue(),"创建部门失败:该父节点不可创建子节点");
                }
                collegeDO.setMechanismId(parentNode.getMechanismId());
                collegeDO.setIdFullPath(parentNode.getIdFullPath()+parentNode.getId()+".");
                collegeDO.setNameFullPath(parentNode.getNameFullPath()+"."+collegeDO.getName());
                collegeDO.setNodeType(CollegeNodeTypeEnum.COLLEGE.getType());
                collegeDO.setSort(0);
                collegeDO.setChildType(CollegeChildTypeEnum.CREATE_DEPARTMENT_CLASS.getType());
                collegeMapper.insertCollege(collegeDO);
                if(CollegeChildTypeEnum.CREATE_DEPARTMENT_CLASS.getType().equals(parentNode.getChildType())){
                    //此父结点只能创建部门
                    parentNode.setChildType(CollegeChildTypeEnum.CREATE_DEPARTMENT.getType());
                    collegeMapper.updateCollege(parentNode);
                }
                return Result.ok(collegeDO);
            } else {
                log.info("重复提交");
            }
        }catch (Exception e){
            throw new RuntimeException(ExceptionUtils.getStackTrace(e));
        }finally {
            if(token!=null) {
                redisLock.unlock(key, token);
            }
        }
        return Result.fail(CodeEnum.FAILED.getValue(),"部门创建失败");
    }

    /**
     * 创建机构院系班级
     * @return
     */
    public Result createMechanismCollegeClass(CollegeDO collegeDO, CollegeDO parentNode){
        String key = RedisKeyGenerator.getKey(CollegeService.class, CRETE_MECHANISM_COLLEGE_CLASS, collegeDO.getParentId());
        String token = redisLock.tryLock(key, TimeUnit.SECONDS.toMillis(3));
        try{
            if(token != null) {
                if(CollegeChildTypeEnum.CREATE_DEPARTMENT.getType().equals(parentNode.getChildType())){
                    return Result.fail(CodeEnum.FAILED.getValue(),"创建班级失败:暂不支持同一目录层级中，同时存在子部门和班级两种类型");
                }
                if(CollegeChildTypeEnum.NOT_CREATE_NODE.getType().equals(parentNode.getChildType())){
                    return Result.fail(CodeEnum.FAILED.getValue(),"创建部门失败:该父节点不可创建子节点");
                }
                collegeDO.setMechanismId(parentNode.getMechanismId());
                collegeDO.setIdFullPath(parentNode.getIdFullPath()+parentNode.getId()+".");
                collegeDO.setNameFullPath(parentNode.getNameFullPath()+"."+collegeDO.getName());
                collegeDO.setNodeType(CollegeNodeTypeEnum.CLASS.getType());
                collegeDO.setSort(0);
                collegeDO.setChildType(CollegeChildTypeEnum.NOT_CREATE_NODE.getType());
                collegeMapper.insertCollege(collegeDO);
                if(CollegeChildTypeEnum.CREATE_DEPARTMENT_CLASS.getType().equals(parentNode.getChildType())){
                    //此父结点只能创建班级
                    parentNode.setChildType(CollegeChildTypeEnum.CREATE_CLASS.getType());
                    collegeMapper.updateCollege(parentNode);
                }
                return Result.ok(collegeDO);
            } else {
                log.info("重复提交");
            }
        }catch (Exception e){
            throw new RuntimeException(ExceptionUtils.getStackTrace(e));
        }finally {
            if(token!=null) {
                redisLock.unlock(key, token);
            }
        }
        return Result.fail(CodeEnum.FAILED.getValue(),"班级创建失败");
    }

    /**
     * 删除部门/班级 （包含子孙节点）
     * @param id
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Result deleteCollegeNode(Integer id){
        String key = RedisKeyGenerator.getKey(CollegeService.class, DELETE_COLLEGE_NODE,id);
        String lock = redisLock.tryLock(key, TimeUnit.SECONDS.toMillis(10));
        try{
            if(lock != null) {
                CollegeDO collegeDO = collegeMapper.findCollegeNodeById(id);
                if(collegeDO!=null && !CollegeNodeTypeEnum.ROOT_PARENT_ID.getType().equals(collegeDO.getParentId())){
                    //判断没有关联的老师和学生才删
                    if(!allowDelCollegeNode(id)){
                        return Result.fail(CodeEnum.FAILED.getValue(),"需要先移出班级下的教师/学生");
                    }
                    //删除节点及其子孙节点
                    CollegeDO up=new CollegeDO();
                    up.setIsDelete(1);
                    up.setId(collegeDO.getId());
                    collegeMapper.updateCollege(up);
                    //删除子孙节点
                    String parentIdFullPath = collegeDO.getIdFullPath()+collegeDO.getId()+".%";
                    collegeMapper.delOffspringNodeByParentIdFullPath(parentIdFullPath);
                    List<CollegeDTO>  cns=collegeMapper.findChildNodeByParentId(collegeDO.getParentId());
                    if(CollectionUtils.isEmpty(cns)){
                        CollegeDO parent=new CollegeDO();
                        parent.setChildType(CollegeChildTypeEnum.CREATE_DEPARTMENT_CLASS.getType());
                        parent.setId(collegeDO.getParentId());
                        collegeMapper.updateCollege(parent);
                    }
                    delNodeCache( id,  collegeDO.getMechanismId());
                    return Result.ok(collegeDO.getName()+"及其子部门已删除");
                }else {
                    return Result.fail(CodeEnum.FAILED.getValue(),"不可删除");
                }
            } else {
                return Result.fail(CodeEnum.FAILED.getValue(),"重复提交");
            }
        }catch (Exception e){
            log.error("{}",ExceptionUtils.getStackTrace(e));
            throw new RuntimeException(ExceptionUtils.getStackTrace(e));
        } finally {
            if(lock!=null) {
                redisLock.unlock(key, lock);
            }
        }
    }

    /**
     * 节点先存在老师或学生则不允许删除
     * @param nodeId
     * @return
     */
    private boolean allowDelCollegeNode(Integer nodeId) {
        DepartmentDTO department=  this.parseNodeNumber(nodeId);
        if(department.getTeacherTotalNumber()>0 || department.getStudentTotalNumber()>0){
            return false;
        }
        return true;
    }

    /**
     * 修改名称-包含本节点和子孙节点的name全路径的修改
     * @param nodeId
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Result updateCollegeNodeName(Integer nodeId, String name){
        CollegeDO collegeDO = collegeMapper.findCollegeNodeById(nodeId);
        if(CollegeNodeTypeEnum.ROOT_PARENT_ID.getType().equals(collegeDO.getParentId())){
            return Result.fail(CodeEnum.FAILED.getValue(),"根结点不可修改");
        }
        return updateCollegeNodeNameBase(nodeId, name, collegeDO);
    }

    /**
     * 更新机构根结点
     * @param mechanismId
     * @param name
     * @return
     */
    @Override
    public Result updateCollegeRootNodeName(Integer mechanismId, String name){
        CollegeDO root = collegeMapper.findCollegeRootByMechanismId(mechanismId);
        if(root!=null){
            return updateCollegeNodeNameBase(root.getId(), name, root);
        }
        return Result.fail(CodeEnum.FAILED.getValue(),"机构根结点不存在");
    }

    public Result updateCollegeNodeNameBase(Integer id, String name, CollegeDO collegeDO){
        String key = RedisKeyGenerator.getKey(CollegeService.class, UPDATE_COLLEGE_NODE_NAME,id);
        String lock = redisLock.tryLock(key, TimeUnit.MINUTES.toMillis(1));
        try{
            if(lock != null) {
                //查看是否存在重名的兄弟节点
                CollegeDO siblingNode = collegeMapper.findChildNodeByNodeNameAndParentId(name,collegeDO.getParentId());
                if(siblingNode!=null){
                    return Result.fail(CodeEnum.FAILED.getValue(),"存在同名的兄弟节点,请修改后在提交");
                }
                if(collegeDO!=null){
                    String oldNameFullPath = collegeDO.getNameFullPath();
                    int index = oldNameFullPath.lastIndexOf(DateTimeUtil.DELIMITER_ONE_PERIOD);
                    String newNfp =oldNameFullPath.substring(0,index+1)+name;
                    CollegeDO up=new CollegeDO();
                    up.setName(name);
                    up.setDescription(name);
                    //生成新的name全路径
                    up.setNameFullPath(newNfp);
                    up.setId(collegeDO.getId());
                    collegeMapper.updateCollege(up);
                    String parentIdFullPath = collegeDO.getIdFullPath()+collegeDO.getId()+".%";
                    //更新子孙节点全路径
                    List<CollegeDTO> childList = collegeMapper.findOffspringNodeByParentIdFullPath(parentIdFullPath);
                    if(CollectionUtils.isNotEmpty(childList)){
                        childList.forEach(node ->{
                            CollegeDO upc = new CollegeDO();
                            upc.setNameFullPath(node.getNameFullPath().replaceFirst(oldNameFullPath,newNfp));
                            upc.setId(node.getId());
                            collegeMapper.updateCollege(upc);
                        });
                    }
                    delNodeCache( id,  collegeDO.getMechanismId());
                    return Result.ok(collegeDO.getName()+"及其子部门已修改");
                }else {
                    return Result.fail(CodeEnum.FAILED.getValue(),"不可修改");
                }
            } else {
                return Result.fail(CodeEnum.FAILED.getValue(),"节点名称不可频繁修改，请稍后再试");
            }
        }catch (Exception e){
            log.error("{}",ExceptionUtils.getStackTrace(e));
            throw new RuntimeException(ExceptionUtils.getStackTrace(e));
        } finally {
            if(lock!=null) {
                redisLock.unlock(key, lock);
            }
        }
    }

    private void delNodeCache(Integer nodeId,Integer mechanismId){
        String key1 = RedisKeyGenerator.getKey(CollegeService.class, FIND_OFFSPRING_NODE_BY_PARENT_ID, nodeId);
        String key2 = RedisKeyGenerator.getKey(CollegeService.class, FIND_CHILD_NODE_BY_PARENT_ID, nodeId);
        String key3 = RedisKeyGenerator.getKey(CollegeService.class, FIND_COLLEGE_NODE_BY_MECHANISM_ID, mechanismId);
        redisUtils.del(key1);
        redisUtils.del(key2);
        redisUtils.del(key3);
    }

    /**
     * 保存老师
     * @param mechanismId
     * @param user
     * @return
     */
    @Override
    public Result saveUserTeacher(Integer mechanismId, UserDO user){
        Integer role = UserRoleEnum.TEACHER.getType();
        if(UserRoleEnum.ASSISTANT.getType().equals(user.getPosition())){
            role =UserRoleEnum.ASSISTANT.getType();
        }
        user.setGender(GenderTypeEnum.parseGender(user.getGender()));
        user.setPosition(role);
        user.setType(Constant.USER_TYPE);
        return saveUser(mechanismId,user);
    }

    /**
     * 保存学生信息- 班级ID大于0，绑定班级
     * @param mechanismId
     * @param user
     * @param collegeId
     * @return
     */
    @Override
    public Result saveUserStudent(Integer mechanismId,Integer collegeId, UserDO user){
        user.setPosition(UserRoleEnum.STUDENT.getType());
        user.setType(Constant.USER_TYPE);
        user.setGender(GenderTypeEnum.parseGender(user.getGender()));
        Result result = saveUser(mechanismId,user);
        if(CodeEnum.SUCCESS.getValue().equals(result.getCode())){
            UserDO u = (UserDO) result.getData();
            if(collegeId!=null && collegeId>0 ){
                Result addResult = addUserToClass(collegeId,u.getId(), ClassPositionEnum.STUDENT.getType());
                if(addResult.getCode().equals(CodeEnum.FAILED.getValue())){
                    result.setCode(CodeEnum.FAILED.getValue());
                    result.setMessage(addResult.getMessage());
                }
            }
        }
        return result;
    }

    /**
     * 学生绑定班级
     * @param collegeId
     * @param userId
     */
    private Result addUserToClass(Integer collegeId, Integer userId,Integer position) {
        String key = RedisKeyGenerator.getKey(CollegeService.class, ADD_USER_TO_CLASS, "collegeId="+collegeId,"userId="+userId);
        String token = redisLock.tryLock(key, TimeUnit.SECONDS.toMillis(5));
        try{
            if(token != null) {
                CollegeDO coCollegeDO = collegeMapper.findCollegeNodeById(collegeId);
                if(coCollegeDO!=null && CollegeNodeTypeEnum.CLASS.getType().equals(coCollegeDO.getNodeType())){
                    int count= collegeMapper.countCollegeUser(collegeId,userId);
                    List<UserDO> userList  = collegeMapper.findUserByClassIdAndPositionType(collegeId,position);
                    if(CollectionUtils.isNotEmpty(userList) && ClassPositionEnum.HEAD_TEACHER.getType().equals(position)){
                        log.error("班主任已存在，设置失败 collegeId:{},userId:{}",collegeId,userId);
                        return Result.fail(CodeEnum.FAILED.getValue(),"班主任已存在",userList);
                    }
                    if(CollectionUtils.isNotEmpty(userList) && userList.size()> MAX_ASSISTANT_NUM && ClassPositionEnum.ASSISTANT.getType().equals(position)){
                        //最多可添加10名助教
                        log.error("助教人数超过最大值，设置失败 collegeId:{},userId:{},助教num:{}",collegeId,userId,userList.size());
                        return Result.fail(CodeEnum.FAILED.getValue(),"助教人数超过最大值",userList);
                    }
                    if(count<1){
                        CollegeUserRefDTO collegeUserRefDTO  = new CollegeUserRefDTO();
                        collegeUserRefDTO.setCollegeId(collegeId);
                        collegeUserRefDTO.setUserId(userId);
                        collegeUserRefDTO.setPosition(position);
                        collegeMapper.insertCollegeUser(collegeUserRefDTO);
                        delClassUserCache(collegeId,position);
                        return Result.ok(collegeUserRefDTO);
                    }else {
                        return Result.fail(CodeEnum.FAILED.getValue(),"用户已加入班级");
                    }
                }else {
                    return Result.fail(CodeEnum.FAILED.getValue(),"请在班级节点上导入用户");
                }
            }
        }catch (Exception e){
            throw new RuntimeException(ExceptionUtils.getStackTrace(e));
        }finally {
            if(token!=null) {
                redisLock.unlock(key, token);
            }
        }
        return Result.fail();
    }

    /**
     * 选择用户绑定到班级
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Result selectUserBindCollege(SelectUserBindDTO selectUserBindDTO) {
        Integer collegeId = selectUserBindDTO.getCollegeId();
        Integer position = selectUserBindDTO.getPosition();
        List<Integer> userIdList = selectUserBindDTO.getUserIdList();
        try {
            String key = RedisKeyGenerator.getKey(CollegeService.class, SELECT_USER_BIND_COLLEGE, "collegeId="+collegeId);
            String token = redisLock.tryLock(key, TimeUnit.MINUTES.toMillis(3));
            try{
                if(token != null) {
                    CollegeDO coCollegeDO = collegeMapper.findCollegeNodeById(collegeId);
                    if(coCollegeDO!=null && CollegeNodeTypeEnum.CLASS.getType().equals(coCollegeDO.getNodeType())){
                        if(CollectionUtils.isNotEmpty(userIdList)){
                            if(ClassPositionEnum.HEAD_TEACHER.getType().equals(position) ||
                                    ClassPositionEnum.ASSISTANT.getType().equals(position)){
                                //删除原有关联
                                collegeMapper.delCollegeUserRefByCollegeIdAndPosition(collegeId,position);
                            }
                            if(ClassPositionEnum.HEAD_TEACHER.getType().equals(position) && userIdList.size()> CLASS_TEACHER_MAX_NUM){
                                return Result.fail(CodeEnum.FAILED.getValue(),"班级仅支持添加一名班主任老师");
                            }
                            if(ClassPositionEnum.ASSISTANT.getType().equals(position) && userIdList.size()> CLASS_ASSISTANT_MAX_NUM){
                                return Result.fail(CodeEnum.FAILED.getValue(),"最多可添加10名助教");
                            }
                            List<CollegeUserRefDO> collegeUserRefDOList = Lists.newArrayList();
                            userIdList.forEach(userId-> collegeUserRefDOList.add(new CollegeUserRefDO(collegeId,userId,position)));
                            if(ClassPositionEnum.STUDENT.getType().equals(position)){
                                collegeUserRefDOList.forEach(cur-> {
                                    Result result = addUserToClass(cur.getCollegeId(),cur.getUserId(),ClassPositionEnum.STUDENT.getType());
                                    if(result.getCode().equals(CodeEnum.FAILED.getValue())){

                                    }
                                });
                            }else {
                                //保存老师助教新关联
                                collegeMapper.insertCollegeUserRefList(collegeUserRefDOList);
                                delClassUserCache(collegeId,position);
                            }
                            return Result.ok("成功");
                        }else {
                            return Result.fail(CodeEnum.FAILED.getValue(),"数据空");
                        }
                    }else {
                        return Result.fail(CodeEnum.FAILED.getValue(),"用户只能绑定到班级节点");
                    }
                } else {
                    return Result.fail(CodeEnum.FAILED.getValue(),"重复提交");
                }
            } finally {
                if(token!=null) {
                    redisLock.unlock(key, token);
                }
            }
        }catch (Exception e){
            log.error("班级绑定失败");
            return Result.fail(CodeEnum.FAILED.getValue(),"班级绑定失败");
        }
    }

    /**
     * 根据ID查询用户
     * @return
     */
    @Override
    public Result findUserById(Integer userId){
        String key = RedisKeyGenerator.getKey(CollegeService.class, FIND_USER_BY_ID,  userId);
        UserDO dto = (UserDO) redisUtils.get(key);
        if(dto==null){
            dto = userMapper.findUserById(userId);
            redisUtils.set(key,dto,TimeUnit.MINUTES.toSeconds(1));
        }
        return Result.ok(dto);
    }
    /**
     * 用户密码重置
     * @return
     */
    @Override
    public Result resetPwd(Integer userId){
        String key = RedisKeyGenerator.getKey(CollegeService.class, RESET_PWD,userId);
        String lock = redisLock.tryLock(key, TimeUnit.SECONDS.toMillis(10));
        try{
            if(lock != null) {
                UserDO userDO = userMapper.findUserById(userId);
                if(userDO!=null){
                    UserDO up=new UserDO();
                    up.setPassword(Md5Util.MD5(Constant.ADD_USER_DEFAULT_PWD));
                    up.setId(userId);
                    userMapper.update(up);
                    delUserCache(userId);
                    return Result.ok("密码已重置");
                }else {
                    return Result.fail(CodeEnum.FAILED.getValue(),"用户不存在");
                }
            } else {
                return Result.fail(CodeEnum.FAILED.getValue(),"重复提交");
            }
        }catch (Exception e){
            log.error("{}",ExceptionUtils.getStackTrace(e));
        } finally {
            if(lock!=null) {
                redisLock.unlock(key, lock);
            }
        }
        return Result.fail(CodeEnum.FAILED.getValue(),"操作失败");
    }
    /**
     * 批量/单个删除用户
     * @param userIdList
     * @return
     */
    @Override
    public Result batchDeleteUserByIdList(List<Integer> userIdList){
        String key = RedisKeyGenerator.getKey(CollegeService.class, BATCH_DELETE_USER_BY_ID_LIST, StringUtils.join(userIdList,"_"));
        String lock = redisLock.tryLock(key, TimeUnit.SECONDS.toMillis(5));
        try{
            if(lock != null) {
                if(CollectionUtils.isNotEmpty(userIdList)){
                    userMapper.batchDeleteUserByIdList(userIdList);
                    userIdList.forEach(userId->delUserCache(userId));
                }
                return Result.ok(userIdList);
            } else {
                return Result.fail(CodeEnum.FAILED.getValue(),"重复提交");
            }
        }catch (Exception e){
            log.error("{}",ExceptionUtils.getStackTrace(e));
        } finally {
            if(lock!=null) {
                redisLock.unlock(key, lock);
            }
        }
        return Result.fail(CodeEnum.FAILED.getValue(),"操作失败");
    }

    /**
     * 批量停用用户
     * @param userIdList
     * @return
     */
    @Override
    public Result batchDeactivateUserByIdList(List<Integer> userIdList){
        String key = RedisKeyGenerator.getKey(CollegeService.class, BATCH_DEACTIVATE_USER_BY_ID_LIST, StringUtils.join(userIdList,"_"));
        String lock = redisLock.tryLock(key, TimeUnit.SECONDS.toMillis(5));
        try{
            if(lock != null) {
                if(CollectionUtils.isNotEmpty(userIdList)){
                    userMapper.batchDeactivateUserByIdList(userIdList);
                    userIdList.forEach(userId->delUserCache(userId));
                }
                return Result.ok(userIdList);
            } else {
                return Result.fail(CodeEnum.FAILED.getValue(),"重复提交");
            }
        }catch (Exception e){
            log.error("{}",ExceptionUtils.getStackTrace(e));
        } finally {
            if(lock!=null) {
                redisLock.unlock(key, lock);
            }
        }
        return Result.fail(CodeEnum.FAILED.getValue(),"操作失败");
    }

    private void delUserCache(Integer userId) {
        String userkey = RedisKeyGenerator.getKey(CollegeService.class, FIND_USER_BY_ID,  userId);
        redisUtils.del(userkey);
    }

    /**
     * 停用/启用用户
     * @param userId
     * @return
     */
    @Override
    public Result deactivateUserById(Integer userId){
        String key = RedisKeyGenerator.getKey(CollegeService.class, DEACTIVATE_USER_BY_ID,userId);
        String lock = redisLock.tryLock(key, TimeUnit.SECONDS.toMillis(10));
        try{
            if(lock != null) {
                UserDO userDO = userMapper.findUserById(userId);
                if(userDO!=null){
                    UserDO up=new UserDO();
                    StatusEnum status = StatusEnum.ENABLE.getType().equals(userDO.getStatus())? StatusEnum.STOP: StatusEnum.ENABLE;
                    up.setStatus(status.getType());
                    up.setId(userId);
                    userMapper.update(up);
                    delUserCache(userId);
                    return Result.ok(status.getName());
                }else {
                    return Result.fail(CodeEnum.FAILED.getValue(),"不存在");
                }
            } else {
                return Result.fail(CodeEnum.FAILED.getValue(),"重复提交");
            }
        }catch (Exception e){
            log.error("{}",ExceptionUtils.getStackTrace(e));
        } finally {
            if(lock!=null) {
                redisLock.unlock(key, lock);
            }
        }
        return Result.fail(CodeEnum.FAILED.getValue(),"操作失败");
    }



    /**
     * 用户列表分页查询
     * @param query
     * @return MechanismOpenStatusEnum
     */
    @Override
    public PageInfo findUserList(UserQuery query) {
        try {
            query.setPageNo(query.getPageNo()==null?0:query.getPageNo());
            query.setPageSize(query.getPageSize()==null? PAGE_SIZE :query.getPageSize());
            if(StringUtils.isNotBlank(query.getNickName())){
                query.setNickName("%"+query.getNickName()+"%");
            }
            //使用分页插件,核心代码就这一行 #分页配置#
            Page page = PageHelper.startPage(query.getPageNo(), query.getPageSize());
            List<UserDO> list = userMapper.findUserList(query);
            parseStatusShow(list);
            PageInfo pageInfo = new PageInfo(list);
            return pageInfo;
        }catch (Exception e){
            log.error("{}", ExceptionUtils.getStackTrace(e));
        }
        return  null;
    }



    /**
     * 保存机构用户(body-rwa-json) ID not null is edit else is insert
     * @param mechanismId
     * @param userDO
     * @return
     */
    public Result saveUser(Integer mechanismId,UserDO userDO){
        userDO.setMechanismId(mechanismId);
        String key = RedisKeyGenerator.getKey(CollegeService.class, SAVE_USER, mechanismId,userDO.getJobNumber());
        String token = redisLock.tryLock(key, TimeUnit.SECONDS.toMillis(5));
        try{
            if(token != null) {
                if(userDO.getStatus()==null){
                    userDO.setStatus(StatusEnum.ENABLE.getType());
                }else {
                    if(userDO.getStatus().equals(StatusEnum.STOP.getType())){
                        userDO.setStatus(StatusEnum.STOP.getType());
                    }else {
                        userDO.setStatus(StatusEnum.ENABLE.getType());
                    }
                }
                MechanismDTO mechanism = mechanismMapper.findMechanismById(mechanismId);
                if(mechanism==null){
                    return Result.fail(CodeEnum.FAILED.getValue(),"机构不存在");
                }
                boolean verifyJobNumber=true;
                boolean verifyPhone=true;
                if(userDO.getId()!=null){
                    UserDO old = userMapper.findUserById(userDO.getId());
                    if(old==null){
                        return Result.fail(CodeEnum.FAILED.getValue(),"用户不存在！");
                    }
                    userDO.setUserName(old.getUserName());
                    userDO.setNickName(old.getNickName());
                    userDO.setPassword(old.getPassword());
                    userDO.setIsDelete(old.getIsDelete());
                    userDO.setUpdateTime(old.getUpdateTime());
                    userDO.setCreateTime(old.getCreateTime());
                    if(UserRoleEnum.STUDENT.getType().equals(old.getPosition()) && !UserRoleEnum.STUDENT.getType().equals(userDO.getPosition())){
                        return Result.fail(CodeEnum.FAILED.getValue(),"学生角色不可修改！");
                    }
                    if(UserRoleEnum.TEACHER.getType().equals(old.getPosition()) && !UserRoleEnum.TEACHER.getType().equals(userDO.getPosition())){
                        return Result.fail(CodeEnum.FAILED.getValue(),"老师角色不可修改！");
                    }
                    if(StringUtils.isNotEmpty(userDO.getJobNumber()) && userDO.getJobNumber().equals(old.getJobNumber())){
                        verifyJobNumber = false;
                    }else {
                        return Result.fail(CodeEnum.FAILED.getValue(),"工号不允许修改！");
                    }
                    if(StringUtils.isNotEmpty(userDO.getPhone()) && userDO.getPhone().equals(old.getPhone())){
                        verifyPhone = false;
                    }
                }
                if(verifyPhone && StringUtils.isEmpty(userDO.getJobNumber())){
                    return Result.fail(CodeEnum.FAILED.getValue(),"工号不能空！");
                }
                UserDO userJb = userMapper.findUserByMechanismIdAndJobNumber(mechanismId,userDO.getJobNumber());
                UserDO userPh = userMapper.findUserByPhone(userDO.getPhone());
                if(verifyJobNumber && userJb!=null){
                    return Result.fail(CodeEnum.FAILED.getValue(),"工号已存在！");
                }
                if(verifyPhone && userPh!=null){
                    return Result.fail(CodeEnum.FAILED.getValue(),"手机号已存在！");
                }
                if(userDO.getId()==null){
                    userDO.setNickName(userDO.getNickName());
                    userDO.setUserName(userDO.getJobNumber());
                    userDO.setPassword(Md5Util.MD5(Constant.ADD_USER_DEFAULT_PWD));
                    userMapper.insertUser(userDO);
                }else {
                    userMapper.update(userDO);
                }

                return Result.ok(userDO);
            } else {
                return Result.fail(CodeEnum.FAILED.getValue(),"重复提交");
            }
        }catch (Exception e){
            log.error("{}",ExceptionUtils.getStackTrace(e));
        }finally {
            if(token!=null) {
                redisLock.unlock(key, token);
            }
        }
        return Result.fail(CodeEnum.FAILED.getValue(),"用户保存失败");
    }


    /**
     * 获取部门老师学生班级详情
     * @param nodeId
     * @return
     */
    @Override
    public Result findDepartmentDetail(Integer nodeId){
        String key = RedisKeyGenerator.getKey(CollegeService.class, FIND_DEPARTMENT_DETAIL, nodeId);
        DepartmentDTO department = (DepartmentDTO) redisUtils.get(key);
        if(department==null){
            department = parseNodeNumber(nodeId);
            if(department!=null){
                List<CollegeDTO> collegeList = collegeMapper.findChildNodeByParentId(nodeId);
                if(CollectionUtils.isNotEmpty(collegeList)){
                    List<DepartmentDTO> departmentList = Lists.newArrayList();
                    collegeList.forEach(c-> {
                        departmentList.add(parseNodeNumber(c.getId()));
//                        if(CollegeNodeTypeEnum.COLLEGE.getType().equals(c.getNodeType())){
//
//                        }
                    });
                    department.setDepartmentList(departmentList);
                }
            }
            redisUtils.set(key,department,TimeUnit.SECONDS.toSeconds(1));
        }
        return Result.ok(department);
    }

    /**
     * 计算节点的班级/老师/学生总数
     * @param nodeId
     */
    private DepartmentDTO parseNodeNumber(Integer nodeId) {
        CollegeDO collegeDO = collegeMapper.findCollegeNodeById(nodeId);
        if(collegeDO!=null) {
            DepartmentDTO nodeTJ = new DepartmentDTO();
            nodeTJ.setNodeId(nodeId);
            String parentIdFullPath = collegeDO.getIdFullPath() + collegeDO.getId() + ".%";
            nodeTJ.setNodeName(collegeDO.getName());
            UserNumQuery numQuery =new UserNumQuery();
            numQuery.setNodeId(nodeId);
            numQuery.setParentIdFullPath(parentIdFullPath);
            numQuery.setPosition(ClassPositionEnum.HEAD_TEACHER.getType());
            Integer teacherNum = collegeMapper.countUserNumberByNodeId(numQuery );
            numQuery.setPosition(ClassPositionEnum.STUDENT.getType());
            Integer studentNum = collegeMapper.countUserNumberByNodeId(numQuery);
            List<CollegeDTO> classList = collegeMapper.findClassNodeByPraent(parentIdFullPath);
            nodeTJ.setTeacherTotalNumber(teacherNum);
            nodeTJ.setStudentTotalNumber(studentNum);
            if (CollectionUtils.isEmpty(classList)) {
                nodeTJ.setClassTotalNumber(0);
            } else {
                nodeTJ.setClassTotalNumber(classList.size());
            }
            return nodeTJ;
        }
        return null;
    }

    private void parseStatusShow(List<UserDO> list) {
        if(CollectionUtils.isNotEmpty(list)){
            list.stream().forEach(o->{
                if(StatusEnum.ENABLE.getType().equals(o.getStatus())){
                    o.setStatusView(StatusEnum.ENABLE.getName());
                }else   if(StatusEnum.STOP.getType().equals(o.getStatus())){
                    o.setStatusView(StatusEnum.STOP.getName());
                }
            });
        }
    }

    /**
     * 获取班级列表详情
     * @param classId
     * @return
     */
    @Override
    public Result findClassDetail(Integer classId){
        ClassDetailDTO classDetail = new ClassDetailDTO();
        List<UserDO> teacherList  =findUserByClassIdAndPositionType(classId,ClassPositionEnum.HEAD_TEACHER.getType());
        parseStatusShow(teacherList);
        List<UserDO> aassistantList =findUserByClassIdAndPositionType(classId,ClassPositionEnum.ASSISTANT.getType());
        parseStatusShow(aassistantList);
        List<UserDO> studentList =findUserByClassIdAndPositionType(classId,ClassPositionEnum.STUDENT.getType());
        parseStatusShow(studentList);
        classDetail.setTeacherList(teacherList);
        classDetail.setAssistantList(aassistantList);
        classDetail.setStudentList(studentList);
        return Result.ok(classDetail);
    }

    /**
     * 查询绑定到班级到用户列表
     * @param classId
     * @param positionType 0学生 1老师2助教
     * @return
     */
    @Override
    public List<UserDO> findUserByClassIdAndPositionType(Integer classId,Integer positionType) {
        List<UserDO>  userList =collegeMapper.findUserByClassIdAndPositionType(classId,positionType);
        parseStatusShow(userList);
        return userList;
    }

    private void delClassUserCache(Integer classId,Integer positionType){
        String key = RedisKeyGenerator.getKey(CollegeService.class, FIND_USER_BY_CLASS_ID_AND_POSITION_TYPE, classId,positionType);
        redisUtils.del(key);
    }

    /**
     * 移除用户
     * @param userId
     * @param classId
     * @return
     */
    @Override
    public Result detachUserCollege(Integer userId, Integer classId){
        String key = RedisKeyGenerator.getKey(CollegeService.class, DETACH_USER_COLLEGE, "userId="+userId,"classId="+classId);
        String token = redisLock.tryLock(key, TimeUnit.SECONDS.toMillis(5));
        try{
            if(token != null) {
                Integer num=collegeMapper.detachUserCollegeByCollegeIdAndUserId(userId,classId);
                return Result.ok("移除成功:userId="+userId+"classId="+classId);
            } else {
                return Result.fail(CodeEnum.FAILED.getValue(),"重复提交");
            }
        }catch (Exception e){
            log.error("{}",ExceptionUtils.getStackTrace(e));
        }finally {
            if(token!=null) {
                redisLock.unlock(key, token);
            }
        }
        return Result.fail(CodeEnum.FAILED.getValue(),"移除失败");
    }

    /**
     * 批量移除学生
     * @return
     */
    @Override
    public Result bulkDetachUserCollege(SelectUserBindDTO selectUserBindDTO){
        Integer collegeId = selectUserBindDTO.getCollegeId();
        List<Integer> userIdList = selectUserBindDTO.getUserIdList();
        String key = RedisKeyGenerator.getKey(CollegeService.class, BULK_DETACH_USER_COLLEGE, "collegeId="+collegeId,userIdList);
        String token = redisLock.tryLock(key, TimeUnit.SECONDS.toMillis(5));
        try{
            if(token != null) {
                if(collegeId!=null && CollectionUtils.isNotEmpty(userIdList)){
                    Integer num=collegeMapper.bulkDetachUserCollege(collegeId,userIdList);
                    return Result.ok("批量移除学生成功");
                }

            } else {
                return Result.fail(CodeEnum.FAILED.getValue(),"重复提交");
            }
        }catch (Exception e){
            log.error("{}",ExceptionUtils.getStackTrace(e));
        }finally {
            if(token!=null) {
                redisLock.unlock(key, token);
            }
        }
        return Result.fail(CodeEnum.FAILED.getValue(),"移除失败");
    }

    /**
     * 学生批量调班
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    @Override
    public Result bulkChangeCollege(SelectUserBindDTO selectUserBindDTO){
        Integer collegeId = selectUserBindDTO.getCollegeId();
        Integer newCollegeId = selectUserBindDTO.getNewCollegeId();
        List<Integer> userIdList = selectUserBindDTO.getUserIdList();
        String key = RedisKeyGenerator.getKey(CollegeService.class, BULK_CHANGE_COLLEGE, "collegeId="+collegeId,"newCollegeId="+newCollegeId,userIdList);
        String token = redisLock.tryLock(key, TimeUnit.SECONDS.toMillis(5));
        try{
            if(token != null) {
                if(collegeId!=null && CollectionUtils.isNotEmpty(userIdList)){
                    Integer num=collegeMapper.bulkDetachUserCollege(collegeId,userIdList);
                    userIdList.forEach(userId-> {
                        Result result = addUserToClass(newCollegeId,userId,ClassPositionEnum.STUDENT.getType());
                        if(result.getCode().equals(CodeEnum.FAILED.getValue())){

                        }
                    });
                    delClassUserCache(collegeId,ClassPositionEnum.STUDENT.getType());
                    return Result.ok("批量调班成功");
                }

            } else {
                return Result.fail(CodeEnum.FAILED.getValue(),"重复提交");
            }
        }catch (Exception e){
            log.error("{}",ExceptionUtils.getStackTrace(e));
            throw new RuntimeException("调班失败");
        }finally {
            if(token!=null) {
                redisLock.unlock(key, token);
            }
        }
        return Result.fail(CodeEnum.FAILED.getValue(),"调班失败");
    }

    /**
     * 获取机构根结点
     * @param mechanismId
     * @return
     */
    @Override
    public Result getMechanismCollegeRoot(Integer mechanismId) {
        String key = RedisKeyGenerator.getKey(CollegeService.class, GET_MECHANISM_COLLEGE_ROOT, mechanismId);
        CollegeDTO rootNode = (CollegeDTO) redisUtils.get(key);
        if(rootNode==null){
            rootNode = collegeMapper.getMechanismCollegeRoot(mechanismId);
            redisUtils.set(key,rootNode,TimeUnit.MINUTES.toSeconds(1));
        }
        return Result.ok(rootNode);
    }

    /**
     * 根据学号查询机构用户
     * @param mechanismId
     * @param jobNumber
     * @return
     */
    @Override
    public UserDO findUserByJobNumber(Integer mechanismId,String jobNumber) {
        String key = RedisKeyGenerator.getKey(CollegeService.class, FIND_USER_BY_JOB_NUMBER,mechanismId, jobNumber);
        UserDO user = (UserDO) redisUtils.get(key);
        if(user==null){
            user = userMapper.findUserByJobNumber(mechanismId,jobNumber);
            redisUtils.set(key,user,TimeUnit.MINUTES.toSeconds(1));
        }
        return user;
    }


}