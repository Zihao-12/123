package com.example.webapp.Service.excel;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.example.webapp.DO.UserDO;
import com.example.webapp.Service.college.CollegeService;
import com.example.webapp.enums.UserRoleEnum;
import com.example.webapp.result.CodeEnum;
import com.example.webapp.result.Result;
import com.example.webapp.utils.SpringContextUtil;
import jakarta.annotation.Resource;
import jakarta.servlet.annotation.WebListener;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@WebListener
public class UserDataListener extends AnalysisEventListener<UserDO> {

    private static final int BATCH_COUNT = 500;
    private List<UserDO> list = new ArrayList<>();
    private Integer mechanismId;
    private Integer collegeId;
    private Integer position;


    public UserDataListener(){}
    public UserDataListener(Integer mechanismId,Integer collegeId,Integer position){
        this.mechanismId = mechanismId;
        this.collegeId   = collegeId;
        this.position = position;
    }
    @Resource
    CollegeService collegeService = SpringContextUtil.getContext().getBean(CollegeService.class);

    /**
     * 每隔500条存储数据库，实际使用中可以3000条，然后清理list ，方便内存回收
     */

    @Override
    public void invoke(UserDO data, AnalysisContext context) {
        list.add(data);
        if (list.size() >= BATCH_COUNT) {
            saveData();
            list.clear();
        }

    }

    /**
     * 所有数据解析完执行的方法
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
        AtomicBoolean flag = new AtomicBoolean(true);
        if (!CollectionUtils.isEmpty(list)) {
            list.stream().forEach(u->{
                Result result;
                if(UserRoleEnum.STUDENT.getType().equals(position)){
                    result = collegeService.saveUserStudent(mechanismId,collegeId,u);
                }else {
                    result = collegeService.saveUserTeacher(mechanismId,u);
                }
                if(!CodeEnum.SUCCESS.equals(result.getCode())){
                    log.error("机构ID:{},学院ID:{},角色:{},error:{}",mechanismId,collegeId,u.getType(),result.getMessage());
                    flag.set(false);
                }
            });
        }
        if(flag.get()){
            log.info("存储数据库成功！");
        }
    }


}