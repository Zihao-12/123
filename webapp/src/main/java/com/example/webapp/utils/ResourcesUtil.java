package com.example.webapp.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.core.io.ClassPathResource;

import java.io.*;

/**
 * 配置类
 *
 * @Data
 * @Component
 * @ConfigurationProperties(prefix = "alarm-info")
 * public class AlarmInfoConfig {
 *     private String zongheTemplete;
 *    private Map<String, String> jumpMap;
 * }
 * 替换模版 org.apache.commons.lang3.text.StrSubstitutor
 *         new StrSubstitutor(map).replace(templete)
 * alarm-info:
 *   zonghe-templete: ${dataTime} ${subsystem}子系统${variableName}发生${alarmType}报警，值为${alarmValue}
 *   jumpMap:
 *       wendu: /general-info/hjwd
 */
@Slf4j
public class ResourcesUtil {

    /**
     * 通过ClassPathResource类获取，建议SpringBoot中使用
     * springboot项目中需要使用此种方法，因为jar包中没有一个实际的路径存放文件
     *
     * @param fileName  resources目录下文件名
     * @throws IOException
     */
    public static String readResourcesFile(String fileName) {
        try {
            ClassPathResource classPathResource = new ClassPathResource(fileName);
            InputStream inputStream = classPathResource.getInputStream();
            return getFileContent(inputStream);
        }catch (Exception e){
            log.error("{}", ExceptionUtils.getStackTrace(e));
        }
        return null;
    }

    /**
     * 根据文件路径读取文件内容
     *
     * @param fileInPath
     * @throws IOException
     */
    public static String getFileContent(Object fileInPath) throws IOException {
        BufferedReader br = null;
        if (fileInPath == null) {
            return null;
        }
        if (fileInPath instanceof String) {
            br = new BufferedReader(new FileReader(new File((String) fileInPath)));
        } else if (fileInPath instanceof InputStream) {
            br = new BufferedReader(new InputStreamReader((InputStream) fileInPath));
        }
        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        br.close();
        return sb.toString();
    }
}
