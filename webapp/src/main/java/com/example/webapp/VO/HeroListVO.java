package com.example.webapp.VO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@ApiModel( "特色榜单vo")
public class HeroListVO implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "登录用户,未参加活动未空")
    private HeroRankingVO loggedUser;
    @ApiModelProperty(value = "榜单列表")
    private List<HeroRankingVO> rankingList;

}
