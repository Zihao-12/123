package com.example.webapp.bms.controller;

import com.aliyuncs.vod.model.v20170321.CreateUploadVideoResponse;
import com.example.webapp.DTO.AreaDTO;
import com.example.webapp.DTO.FileDTO;
import com.example.webapp.DTO.UserDto;
import com.example.webapp.Service.area.AreaService;
import com.example.webapp.annotation.LoginRequired;
import com.example.webapp.common.redis.RedisUtils;
import com.example.webapp.enums.PlatformMarkEnum;
import com.example.webapp.result.CodeEnum;
import com.example.webapp.result.Result;
import com.example.webapp.third.*;
import com.example.webapp.utils.SnowflakeIdWorker;
import com.example.webapp.utils.UserThreadLocal;
import com.google.gson.Gson;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Api(tags = {"1001-公共接口"})
@RefreshScope
@RestController
@Component
@Slf4j
@LoginRequired(platform= PlatformMarkEnum.ENTERPRISE)
@RequestMapping("/api/bms/common")
public class CommonController {
    @Autowired
    private AreaService areaService;
    @Autowired
    private RedisUtils redisUtils;

    @ApiOperation(value = "删除缓存",notes = "删除缓存")
    @ApiImplicitParams({@ApiImplicitParam(name="key",value="key")})
    @GetMapping(value = "del-cache")
    public Result delCache (@RequestParam("key") String key){
        try {
            redisUtils.del(key);
            return Result.ok(0);
        } catch (Exception e) {
            log.error("{}", ExceptionUtils.getStackTrace(e));
        }
        return Result.fail();
    }

    @ApiOperation(value = "获取省份列表",notes = "省/直辖/市特区列表")
    @PostMapping(value = "area/province-list")
    public Result getProvinceList (){
        try {
            List<AreaDTO> list =areaService.getAreaList(0);
            return Result.ok(list);
        } catch (Exception e) {
            log.error("{}",ExceptionUtils.getStackTrace(e));
        }
        return Result.fail();
    }

    @ApiOperation(value = "获取子地区列表",notes = "获取子地区列表")
    @ApiImplicitParams({@ApiImplicitParam(name="parentId",value="父地区ID",required=true,paramType="path",example = "110000")})
    @PostMapping(value = "area/child-list/{parentId}")
    public Result getChildAreaList (@PathVariable Integer parentId){
        try {
            List<AreaDTO> list =areaService.getAreaList(parentId);
            return Result.ok(list);
        } catch (Exception e) {
            log.error("{}",ExceptionUtils.getStackTrace(e));
        }
        return Result.fail();
    }


    public static final String DELIMITER_ONE_SLASH = "/";
    public static final String DELIMITER_ONE_PERIOD = ".";

    @ApiOperation(value = "阿里OSS文件/图片上传", notes = "OBS附件类型 0通用文件 1课程附件 2课程封面 3用户资料图片")
    @ApiImplicitParams({@ApiImplicitParam(name = "type", value = "0通用文件 1课程附件 2课程封面 3用户资料图片", required = true)})
    @PostMapping(value = "ali/obs/upload/{type}")
    public Result upload(MultipartFile file, @PathVariable Integer type) {
        try {
            String exts = "jpg,png,gif,bmp,doc,docx,rar,zip";
            UserDto userDto = UserThreadLocal.get();
            AttachmentTypeEnum typeEnum  = AttachmentTypeEnum.getEnumByType(type);
            if(typeEnum == null){
                return Result.fail("附件类型错误！"+new Gson().toJson(AttachmentTypeEnum.getEnumObjList()));
            }
            if(file==null){
                return Result.fail("参数错误");
            }
            String fileName = file.getOriginalFilename();
            String extendName = fileName.substring(fileName.lastIndexOf(DELIMITER_ONE_PERIOD) + 1).toLowerCase();
            if(!exts.contains(extendName)){
                return Result.fail("附件格式不符，请重新上传!支持格式：jpg,png,gif,bmp,doc,docx,rar,zip");
            }
            SnowflakeIdWorker idWorker = new SnowflakeIdWorker(0, 0);
            String objectKey = typeEnum.getPathPerfix()+DELIMITER_ONE_SLASH  + userDto.getId()+DELIMITER_ONE_SLASH+
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


    @ApiOperation(value = "阿里OSS删除文件", notes = "阿里OSS删除文件")
    @ApiImplicitParams({@ApiImplicitParam(name = "objectKey", value = "例如：zhihuijiaoyu/general/attachment/1001/951060736988151808.jpg", required = true)})
    @PostMapping(value = "ali/del-oss")
    public Result delete(String objectKey) {
        try {
            AliOSS.deleteObject(objectKey);
            return Result.ok("success");
        } catch (Exception e) {
            log.error("{}", ExceptionUtils.getStackTrace(e));
        }
        return Result.fail();
    }

    /**
     * 获取阿里视频上传地址和视频id
     * web端上传参见:https://help.aliyun.com/document_detail/99889.html?spm=a2c4g.11186623.6.1012.26c5b227o9KTXj
     * @param title
     * @param fileName
     * @return
     */
    @ApiOperation(value = "获取阿里视频上传地址和视频id", notes = "获取阿里视频上传地址和视频id")
    @PostMapping(value = "ali/video/upload-info")
    public Result uploadVideo(String title, String fileName) {
        try {
            CreateUploadVideoResponse response = AliVideoUpload.createUploadVideo(title, fileName);
            return Result.ok(response);
        } catch (Exception e) {
            log.error("{}", ExceptionUtils.getStackTrace(e));
        }
        return Result.fail();
    }

    @ApiOperation(value = "获取阿里云点播信息",notes = "获取阿里云点播信息(加密视频播放需要数据获取)")
    @GetMapping(value = "ali/video/{videoId}")
    public Result videoInfo(@PathVariable String videoId) {
        AliVideo video = AliVideo.getInstance(AccessKeyIdSecretEnum.ALI_VIDEO.getAk(),AccessKeyIdSecretEnum.ALI_VIDEO.getAks(),AccessKeyIdSecretEnum.ALI_VIDEO.getRegionId());
        Result result = video.getVideoInfo(videoId);
        if(CodeEnum.FAILED.getValue().equals(result.getCode())){
            return Result.fail("视频ID不存在");
        }
        return result;
    }

}
