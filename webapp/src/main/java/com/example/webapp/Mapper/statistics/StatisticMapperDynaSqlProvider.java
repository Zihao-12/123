package com.example.webapp.Mapper.statistics;

import com.example.webapp.DO.StUserFakeDataDO;

import java.text.MessageFormat;
import java.util.List;
import java.util.Map;

public class StatisticMapperDynaSqlProvider {


    /**
     *
     * @param map
     * @return
     */
    public String insertUserFakeDataList(Map map) {
        List<StUserFakeDataDO> insertList = (List<StUserFakeDataDO>) map.get("list");
        StringBuilder sb = new StringBuilder();
        sb.append("INSERT INTO st_user_fake_data ");
        sb.append("(user_id,mechanism_id ,num,real_num,date,type,category_id,category_name) ");
        sb.append("VALUES ");
        MessageFormat mf = new MessageFormat(
                "( #'{'list[{0}].userId}, #'{'list[{0}].mechanismId},#'{'list[{0}].num},#'{'list[{0}].realNum},#'{'list[{0}].date} ,#'{'list[{0}].type},#'{'list[{0}].categoryId},#'{'list[{0}].categoryName})");
        for (int i = 0; i < insertList.size(); i++) {
            sb.append(mf.format(new Object[]{i}));
            if (i < insertList.size() - 1) {
                sb.append(",");
            }
        }
        return sb.toString();
    }


}