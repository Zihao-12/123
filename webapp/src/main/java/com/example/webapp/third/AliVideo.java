package com.example.webapp.third;

import ch.qos.logback.core.net.server.Client;
import com.example.webapp.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;


@Slf4j
public class AliVideo {
    private static String accessKeyId;
    private static String accessKeySecret;
    private static String regionId;
    private static String endPoint;

    private AliVideo() {
    }

    private AliVideo(String ak, String aks, String region, String endPoint) {
    }

    private static class AliVideoHolder {
        /**
         * 静态初始化器，由JVM来保证线程安全
         */
        //获取视频播放加密信息
        private static AliVideo instance = new AliVideo(accessKeyId,accessKeySecret,regionId,null);
        //获取视频明细(视频标题、描述、时长、封面URL、状态、创建时间、大小、截图、分类和标签等信息)
        private static AliVideo instanceInfo = new AliVideo(accessKeyId,accessKeySecret,null,endPoint);
    }

    /**
     * 获取视频播放加密信息
     * @param ak
     * @param aks
     * @param region
     * @return
     */
    public static AliVideo getInstance(String ak, String aks, String region) {
        accessKeyId = ak;
        accessKeySecret = aks;
        regionId = region;
        return AliVideoHolder.instance;
    }

    /**
     * 获取视频明细(视频标题、描述、时长、封面URL、状态、创建时间、大小、截图、分类和标签等信息)
     * @param ak
     * @param aks
     * @param ep
     * @return
     */
    public static AliVideo getInstanceInfo(String ak, String aks, String ep) {
        accessKeyId = ak;
        accessKeySecret = aks;
        endPoint = ep;
        return AliVideoHolder.instanceInfo;
    }

    /**
     * 获取视频播放时加密信息
     * @param videoId
     * @return
     */
    public Result getVideoInfo(String videoId){
        Result result;
        try {
            DefaultProfile profile = DefaultProfile.getProfile(regionId, accessKeyId, accessKeySecret);
            IAcsClient client = new DefaultAcsClient(profile);
            GetVideoPlayAuthRequest request = new GetVideoPlayAuthRequest();
            request.setRegionId(regionId);
            request.setVideoId(videoId);
            //播放凭证过期时间100秒
            request.setAuthInfoTimeout(100L);
            GetVideoPlayAuthResponse response = client.getAcsResponse(request);
            result = Result.ok(response);
        } catch (Exception e) {
            log.error("{}",ExceptionUtils.getStackTrace(e));
            result=Result.fail(ExceptionUtils.getStackTrace(e));
        }
        return result;
    }

    /**=====下面是获取视频时长api========*/
    /**
     * 使用AK&SK初始化账号Client
     * @param accessKeyId
     * @param accessKeySecret
     * @return Client
     * @throws Exception
     */
    public Client createClient(String accessKeyId, String accessKeySecret, String endPoint) throws Exception {
        Config config = new Config().setAccessKeyId(accessKeyId).setAccessKeySecret(accessKeySecret);
        // 访问的域名 config.endpoint = "vod.cn-shanghai.aliyuncs.com";
        config.endpoint = endPoint;
        return new Client(config);
    }

    /**
     * 获取视频明细(视频标题、描述、时长、封面URL、状态、创建时间、大小、截图、分类和标签等信息)
     * @return
     * @throws Exception
     */
    public GetVideoInfoResponse getVideoDetail(String videoId) throws Exception {
        Client client = createClient(accessKeyId, accessKeySecret,endPoint);
        GetVideoInfoRequest getVideoInfoRequest = new GetVideoInfoRequest().setVideoId(videoId);
        // 复制代码运行请自行打印 API 的返回值
        GetVideoInfoResponse videoInfoResponse = client.getVideoInfo(getVideoInfoRequest);
        return videoInfoResponse;
    }

    /**
     * 阿里云 非加密视频 videoId 播放
     * @param videoId
     */
    public GetPlayInfoResponse getPlayInfo(String videoId) throws Exception {
        Client client = createClient(accessKeyId, accessKeySecret,endPoint);
        GetPlayInfoRequest getPlayInfoRequest = new GetPlayInfoRequest()
                .setVideoId(videoId)
                .setResultType("Multiple");
        return client.getPlayInfo(getPlayInfoRequest);
    }
}
