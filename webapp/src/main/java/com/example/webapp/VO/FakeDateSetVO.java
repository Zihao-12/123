package com.example.webapp.VO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@Data
@ApiModel( "造数据配置vo")
public class FakeDateSetVO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "首页访问量(书房)")
    private FakeSetVO homepageVisits;
    @ApiModelProperty(value = "课程访问量（课程详情）")
    private FakeSetVO courseVisits;
    @ApiModelProperty(value = "活动访问量（活动列表和详情）")
    private FakeSetVO activityVisits;
    @ApiModelProperty(value = "课程学习时长")
    private FakeSetVO studyVisits;
    @ApiModelProperty(value = "我的访问量")
    private FakeSetVO myVisits;
    @ApiModelProperty(value = "分类配置数组")
    private List<CategoryFakeSetVO> categroyList;

}

