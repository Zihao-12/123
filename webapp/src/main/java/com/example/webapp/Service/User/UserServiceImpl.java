package com.example.webapp.Service.User;

import com.example.webapp.DO.UserDO;
import com.example.webapp.Mapper.UserMapper;
import com.example.webapp.Query.UserQuery;
import com.example.webapp.common.Constant;
import com.example.webapp.enums.GenderTypeEnum;
import com.example.webapp.enums.UserRoleEnum;
import com.example.webapp.result.Result;
import com.example.webapp.utils.DateTimeUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

import static com.example.webapp.common.Constant.PAGE_SIZE;

@Service
@Slf4j
@Data
public class UserServiceImpl implements UserService,Serializable{
    public static final int LENGTH_20 = 20;
    public static final int LENGTH_15 = 15;
    public static final int RANDOM_COUNT = 50;

    @Autowired
    private UserMapper userMapper;

    @Override
    public Result insert(UserDO user){
        try {
            user.setGender(GenderTypeEnum.parseGender(user.getGender()));
            user.setPosition(UserRoleEnum.NO_ROLE.getType());
            user.setType(Constant.USER_TYPE);
            userMapper.insertUser(user);
        }catch (Exception e){
            log.error("save error");
            throw e;
        }
        return  Result.ok(user.getId());
    }

    @Override
    public UserDO findUserByPhone(String phone) {
        return null;
    }

    @Override
    public UserDO getUserByName(String userName) {
        return null;
    }

    @Override
    public PageInfo queryUseryList(UserQuery query) {
        return null;
    }

    @Override
    public Integer updateUserInfo(UserDO u) {
        return null;
    }

    @Override
    public String getRandomUserName() {
        return null;
    }

    @Override
    public UserDO findUserByOpenId(String openId) {
        return null;
    }

    @Override
    public UserDO findUserByReaderBadge(String readerBadge, Integer mechanismId, String dockingType) {
        return null;
    }

    @Override
    public PageInfo<List<UserDO>> queryUserList(UserQuery query) {
        try {
            query.setPageNo(query.getPageNo()==null?0:query.getPageNo());
            query.setPageSize(query.getPageSize()==null?PAGE_SIZE:query.getPageSize());
            if(StringUtils.isNotBlank(query.getNickName())){
                query.setNickName("%"+query.getNickName()+"%");
            }
            if(query.getRegisterTime()!=null){
                Date begin  = DateTimeUtil.parse(DateTimeUtil.format(new Date(query.getRegisterTime()),DateTimeUtil.DATE_FORMAT_YYYYMMDD));
                query.setBeginTime(begin);
                query.setEndTime(DateTimeUtil.plusDays(query.getBeginTime(),1));
                //使用分页插件,核心代码就这一行 #分页配置#
                Page page = PageHelper.startPage(query.getPageNo(), query.getPageSize());
                List<UserDO> list = userMapper.findUserList(query);
                PageInfo pageInfo = new PageInfo(list);
                return pageInfo;
            }
        }catch (Exception e){
            log.error("{}", ExceptionUtils.getStackTrace(e));
        }
        return null;
    }
}
