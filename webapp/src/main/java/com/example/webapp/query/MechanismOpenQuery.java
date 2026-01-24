package com.example.webapp.query;

import com.example.webapp.result.ResultPage;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import java.io.Serializable;

@ApiModel( "机构开通查询")
@Data
public class MechanismOpenQuery extends ResultPage implements Serializable {
    private static final long serialVersionUID = -3226636602328151880L;
    @ApiModelProperty(name = "name",notes = "查询关键字",example = "北大",required = false)
    private String name;
    @ApiModelProperty(name = "status",notes = "启用状态：-1 全部 0已停用 1已启用",example = "-1",required = true)
    private Integer status;
    @ApiModelProperty(name = "open",notes = "开通状态：-1 全部 0已结束 1待开始 2进行中",example = "-1",required = true)
    private Integer open;
    @ApiModelProperty(name = "openType",notes = "开通类型：0实训开通 1微软开通",example = "0",required = true)
    private Integer openType;

    public static String getQueryKey(String name,Integer status,Integer open,Integer pageSize,Integer pageNo,Integer openType){
        name = StringUtils.isEmpty(name)?"":name;
        String template = "name=%s&pageSize=%d&pageNo=%d&status=%d&open=%d&openType=%d";
        String str = String.format(template, name,pageSize, pageNo,status,open,openType);
        String token  = DigestUtils.md5DigestAsHex(str.getBytes());
        return token;
    }
}
