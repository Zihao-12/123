package com.example.webapp.bms.controller;

import com.example.webapp.Service.json.JsonService;
import com.example.webapp.annotation.LoginRequired;
import com.example.webapp.enums.PlatformMarkEnum;
import com.example.webapp.result.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import java.io.*;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Base64;

/** 分类表
 * @author ghs 
 */
@Api(tags = {"2000-生成静态文件"})
@RefreshScope
@RestController
@Component
@Slf4j
@LoginRequired(platform= PlatformMarkEnum.ENTERPRISE)
@RequestMapping("/api/bms/json")
public class JsonStaticController {

    @Autowired
    JsonService jsonService;
    /**
     * json文件基础目录
     */
    @Value("${portal.static.json.base.path:/tol/htdocs/ziptemp}")
    private String BASE_PATH;

    @ApiOperation(value = "打包易读静态json数据",notes = "打包易读静态json数据")
    @GetMapping(value = "generate")
    public Result generateStaticJson() {
       return jsonService.generateStaticJson();
    }

    @ApiOperation(value = "查看易读静态json数据打包进度",notes = "查看易读静态json数据打包进度")
    @ApiImplicitParams({@ApiImplicitParam(name = "delCache", value = "1清除打包状态缓存，0不清除", required = true)})
    @GetMapping(value = "process/{delCache}")
    public Result process(@PathVariable int delCache) {
        return jsonService.procesds(delCache);
    }

    @ApiOperation(value = "下载易读静态json数据",notes = "下载易读静态json数据")
    @GetMapping(value = "dowload")
    public void dowload(HttpServletRequest request, HttpServletResponse response) throws Exception {
        downLoad(BASE_PATH+"/dfyd-static.zip",response,false);
    }

    /**
     * 支持在线打开文件的一种方式
     * @param filePath
     * @param response
     * @param isOnLine
     * @throws Exception
     */
    public void downLoad(String filePath, HttpServletResponse response, boolean isOnLine) throws Exception {
        File f = new File(filePath);
        if (!f.exists()) {
            response.sendError(404, "File not found!");
            return;
        }
        BufferedInputStream br = new BufferedInputStream(new FileInputStream(f));
        byte[] buf = new byte[8192];
        int len = 0;

        response.reset(); // 非常重要
        if (isOnLine) { // 在线打开方式
            URL u = new URL("file:///" + filePath);
            response.setContentType(u.openConnection().getContentType());
            response.setHeader("Content-Disposition", "inline; filename=" + f.getName());
            // 文件名应该编码成UTF-8
        } else { // 纯下载方式
            response.setContentType("application/x-msdownload");
            response.setHeader("Content-Disposition", "attachment; filename=" + f.getName());
        }
        OutputStream out = response.getOutputStream();
        while ((len = br.read(buf)) > 0){
            out.write(buf, 0, len);
        }
        br.close();
        out.close();
    }

    /**
     * 下载文件时，针对不同浏览器，进行附件名的编码
     *
     * @param filename 载文件名
     * @param request 请求request
     * @return 编码后的下载附件名
     * @throws IOException
     */
    public static String encodeDownloadFilename(String filename, HttpServletRequest request)
            throws IOException {
        String agent = request.getHeader("user-agent");
        if (agent.contains("Firefox")) {
            filename = "=?UTF-8?B?" + Base64.getEncoder().encode(filename.getBytes("utf-8")) + "?=";
            filename = filename.replaceAll("\r\n", "");
        } else {
            // IE及其他浏览器
            filename = URLEncoder.encode(filename, "utf-8");
            filename = filename.replace("+"," ");
        }
        return filename;
    }
}

