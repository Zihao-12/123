package com.example.webapp.query;

import com.example.webapp.result.ResultPage;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import org.springframework.util.DigestUtils;

import java.io.Serializable;

@ApiModel( "课程详情用户查询")
@Data
public class UserLearnRecordQuery extends ResultPage implements Serializable {
    private static final long serialVersionUID = -3118985077175688750L;
    @ApiModelProperty(hidden = true)
    private Integer mechanismId;
    @ApiModelProperty(name = "courseId",notes = "课程ID")
    private Integer courseId;
    @ApiModelProperty(notes = " 课程包id",hidden = true)
    private Integer coursePackageId;
    @ApiModelProperty(name = "userId",notes = "用户ID")
    private Integer userId;
    @ApiModelProperty(name = "complete",notes = "0未完成 1已完成")
    private Integer complete;

    public static String getQueryKey(Integer pageSize,Integer pageNo,Integer mechanismId,Integer courseId){
        String template = "pageSize=%d&pageNo=%d&mechanismId=%d&courseId=%d";
        String str = String.format(template,pageSize, pageNo,mechanismId,courseId);
        String token  = DigestUtils.md5DigestAsHex(str.getBytes());
        return token;
    }
}