package com.example.webapp.Query;

import com.example.webapp.result.ResultPage;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import java.io.Serializable;

@ApiModel( "机构查询")
@Data
public class MechanismQuery extends ResultPage implements Serializable {
    private static final long serialVersionUID = -3226636602328151880L;

    @ApiModelProperty(value = "查询关键字")
    private String name;

    @ApiModelProperty(value = "机构属性 2普通本科 3高职高专")
    private Integer attribute;
    @ApiModelProperty(value = "省份ID")
    private Integer province;
    @ApiModelProperty(value = "0排除有开通记录的机构 1只查询开通的机构")
    private Integer openType;

    public static String getQueryKey(String name,Integer attribute,Integer province,Integer openType,Integer pageSize,Integer pageNo){
        name = StringUtils.isEmpty(name)?"":name;
        openType =openType==null?-1:openType;
        String template = "name=%s&pageSize=%d&pageNo=%d&attribute=%d&province=%d&openType=%d";
        String str = String.format(template, name,pageSize, pageNo,attribute,province,openType);
        String token  = DigestUtils.md5DigestAsHex(str.getBytes());
        return token;
    }
}

