package com.michal.car.notify.service.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @author Michal Remis
 */
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "global-app-props")
public class GlobalAppProperties {

    private String baseUrl;
    private String suffixTemplate;
    private List<String> watchCars;
    private Integer upperPrice;
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

    public List<String> getWatchCars() {
        return watchCars;
    }

    public void setWatchCars(List<String> watchCars) {
        this.watchCars = watchCars;
    }

    public Integer getUpperPrice() {
        return upperPrice;
    }

    public void setUpperPrice(Integer upperPrice) {
        this.upperPrice = upperPrice;
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

    @Override
    public String toString() {
        return "GlobalAppProperties{" +
                "adminClient=" + adminChatId +
                ", disableJob=" + disableJob +
                ", upperPrice=" + upperPrice +
                ", watchCars=" + watchCars +
                ", suffixTemplate='" + suffixTemplate + '\'' +
                ", baseUrl='" + baseUrl + '\'' +
                '}';
    }
}