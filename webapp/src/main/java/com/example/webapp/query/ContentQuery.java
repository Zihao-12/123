package com.example.webapp.query;

import com.example.webapp.result.ResultPage;
import com.example.webapp.utils.Md5Util;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel( "内容query")
public class ContentQuery extends ResultPage implements Serializable {
    private static final String SPLIT = "_";
    private static final long serialVersionUID = 1L;
    private static final String REDIS_KEY_CONTENT_LIST = "content_list_";
    private static final String REDIS_KEY_RECOMMEND_LIST = "content_recommend_list_";
    private static final String REDIS_KEY_PORTION_LIST = "content_portion_list_";
    @ApiModelProperty(notes = "名称")
    private String title;
    @ApiModelProperty(notes = "类型1.通用新闻资讯")
    private Integer type;
    @ApiModelProperty(notes = "内容类型1.图文 2.视频")
    private Integer contentType;
    @ApiModelProperty(notes = "0下架 1上架")
    private Integer status;
    @ApiModelProperty(notes = "分类id")
    private Integer categoryId;
    @ApiModelProperty(notes = "展示数据条数,获取部分列表数据时使用")
    private Integer portionCount;

    public static String getRedisKey(Integer type, Integer categoryId, Integer pageNo, Integer pageSize) {
        if(categoryId==null){
            categoryId = 0;
        }
        String temp = "pageSize=%d&pageNo=%d&type=%d&categoryId=%d";
        String str = String.format(temp,pageSize, pageNo,type,categoryId);
        return REDIS_KEY_CONTENT_LIST + Md5Util.MD5(str);
    }

    public static String getRecommendRedisKey(Integer type) {
        return REDIS_KEY_RECOMMEND_LIST + type;
    }

    public static String getPortionRedisKey(Integer type,Integer portionCount) {
        return REDIS_KEY_RECOMMEND_LIST + type + SPLIT + portionCount;
    }
}
