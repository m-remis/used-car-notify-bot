package com.michal.car.notify.service.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author Michal Remis
 */
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "global-app-props")
public class GlobalAppProperties {

    private String baseUrl;
    private String suffixTemplate;
    private Boolean disableJob;
    private String adminChatId;

    public String getSuffixTemplate() {
        return suffixTemplate;
    }

    public void setSuffixTemplate(String suffixTemplate) {
        this.suffixTemplate = suffixTemplate;
    }

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public boolean getDisableJob() {
        return disableJob;
    }

    public void setDisableJob(Boolean disableJob) {
        this.disableJob = disableJob;
    }

    public String getAdminChatId() {
        return adminChatId;
    }

    public void setAdminChatId(String adminChatId) {
        this.adminChatId = adminChatId;
    }

}