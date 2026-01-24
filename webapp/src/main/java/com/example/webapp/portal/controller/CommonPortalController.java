package com.example.webapp.portal.controller;

import com.example.webapp.DTO.FileDTO;
import com.example.webapp.DTO.FrontPageDTO;
import com.example.webapp.DTO.UserDto;
import com.example.webapp.Service.mechanism.MechanismService;
import com.example.webapp.annotation.LogRecord;
import com.example.webapp.annotation.LoginRequired;
import com.example.webapp.enums.LogRecordEnum;
import com.example.webapp.result.Result;
import com.example.webapp.third.AliOSS;
import com.example.webapp.third.AttachmentTypeEnum;
import com.example.webapp.utils.EncryptUtil;
import com.example.webapp.utils.SnowflakeIdWorker;
import com.example.webapp.utils.UserThreadLocal;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@Api(tags = {"1001-公共接口"})
@RestController
@RefreshScope
@Slf4j
@RequestMapping("/")
@Controller
public class CommonPortalController {
    public static final String DELIMITER_ONE_SLASH = "/";
    public static final String DELIMITER_ONE_PERIOD = ".";

    @Autowired
    private MechanismService mechanismService;

    @ApiOperation(value = "获取机构信息")
    @ApiImplicitParams({ @ApiImplicitParam(name="aci",value="机构访问地址中获取",required=true,example = "bbbeff8fddb2e2fa65a2340b427df2ec6c81d62b9707490c")})
    @PostMapping(value = "/api/common/get-mechanism-info")
    public Result getMechanismInfo (String aci){
        try {
            String mechanismId = EncryptUtil.getUserIdBySid(aci);
            log.info("mechanismId={},aci={}",mechanismId,aci);
            FrontPageDTO frontPageDTO =  mechanismService.getMechanismAccessUrl(mechanismId);
            return Result.ok(frontPageDTO);
        } catch (Exception e) {
            log.error("{}", ExceptionUtils.getStackTrace(e));
            return Result.fail("机构信息认证失败");
        }
    }

    @LoginRequired
    @ApiOperation(value = "阿里OSS文件/图片上传", notes = "OBS附件类型 3用户资料图片")
    @PostMapping(value = "/api/portal/ali/obs/upload")
    public Result upload(MultipartFile file) {
        try {
            UserDto userDto = UserThreadLocal.get();
            if(file==null){
                return Result.fail("参数错误");
            }
            String fileName = file.getOriginalFilename();
            String extendName = fileName.substring(fileName.lastIndexOf(DELIMITER_ONE_PERIOD) + 1).toLowerCase();
            SnowflakeIdWorker idWorker = new SnowflakeIdWorker(0, 0);
            String objectKey = AttachmentTypeEnum.PROFILE_PIC.getPathPerfix()+DELIMITER_ONE_SLASH  + userDto.getId()+DELIMITER_ONE_SLASH+
                    idWorker.nextId() + DELIMITER_ONE_PERIOD + extendName;
            AliOSS.putObjectMultipart(objectKey,file);
            FileDTO attachment = new FileDTO();
            attachment.setFileName(fileName);
            attachment.setFilePath(objectKey);
            attachment.setUrl(AliOSS.getUrlDomain()+objectKey);
            attachment.setFileSize(file.getSize());
            return Result.ok(attachment);
        } catch (Exception e) {
            log.error("{}", ExceptionUtils.getStackTrace(e));
        }
        return Result.fail();
    }

    @LoginRequired
    @LogRecord(LOG_RECORD_ENUM = LogRecordEnum.MY)
    @ApiOperation(value = "我的访问")
    @PostMapping(value = "/api/portal/my-info")
    public Result getMyInfo (){
        return Result.ok(System.currentTimeMillis());
    }
}
