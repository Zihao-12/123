package com.example.webapp.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * 读者证对接配置类
 * 1.@Value("${server.port}")
 * 2.映射javaBean： @ConfigurationProperties
 */
@Data
@Configuration
@ConfigurationProperties(prefix = "reader.docking")
public class ReaderDockingConfig {

    private List<DockingMechConfig> mechanisms;

}
