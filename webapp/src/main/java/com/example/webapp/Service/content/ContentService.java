package com.example.webapp.Service.content;

import com.example.webapp.DO.ContentDO;
import com.example.webapp.Query.ContentQuery;
import com.example.webapp.result.Result;
import com.example.webapp.result.ResultPage;

public interface ContentService {

    /**
     * 分页查询
     * @param query
     * @return
     */
    ResultPage list(ContentQuery query);

    /**
     * 新建
     * @return
     */
    Result insert(ContentDO contentDO);

    /**
     * 更新
     * @param contentDO
     * @return
     */
    Result update(ContentDO contentDO);

    /**
     * 删除
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
     * 上下架
     * @param id
     * @param status
     * @return
     */
    Result updateStatus(int id, int status);

    /**
     * 是否置顶:0未置顶 1.置顶
     * @param id
     * @param top
     * @return
     */
    Result top(int id, int top);

    /**
     * 是否推荐:0否 1.是
     * @param id
     * @param recommend
     * @return
     */
    Result recommend(int id, int recommend);

    /**
     * 前台--列表(通用新闻资讯)
     * @param query
     * @return
     */
    ResultPage portalList(ContentQuery query);

    /**
     * 前台--详情(通用新闻资讯)
     * @param id
     * @return
     */
    Result portalHtDetail(int id);




    /**
     * 获取部分列表数据
     * @param query
     * @return
     */
    Result portalPortionList(ContentQuery query);
}
