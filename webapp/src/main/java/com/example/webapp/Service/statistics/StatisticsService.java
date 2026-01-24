package com.example.webapp.Service.statistics;

import com.example.webapp.DO.StUserLogRecordDO;
import com.example.webapp.DO.UserLoginRecordDO;
import com.example.webapp.VO.FakeDataTJVO;
import com.example.webapp.query.CourseQuery;
import com.example.webapp.query.FakeDataQuery;
import com.example.webapp.result.Result;

import java.util.Date;

public interface StatisticsService {

    /**
     * 登录信息记录
     * @param userLoginRecordDO
     * @return
     */
    Result loginRecord(UserLoginRecordDO userLoginRecordDO);

    /**
     * 学习中心--学习信息统计
     * @param query
     * @return
     */
    Result learnInfo(CourseQuery query);

    Result saveLogRecordDo(StUserLogRecordDO lr);

    /**
     * 每天凌晨生成前一天的假数据
     * @param dataDate
     */
    void fakeData(Date dataDate);

    /**
     * 统计造假数据
     * @param query
     * @return
     */
    Result<FakeDataTJVO>  getFakeDataList(FakeDataQuery query);

    /**
     * 统计造假数据 UV
     * @param query
     * @return
     */
    Result<FakeDataTJVO> getFakeDataUVList(FakeDataQuery query);


    /**
     * 分类/年龄统计
     * @param query
     * @return
     */
    Result<FakeDataTJVO> getCategoryFakeDataList(FakeDataQuery query);


    void generateDefaultFakeData(Date dataDate);

    Result getRegCountByMid(FakeDataQuery query);
}
