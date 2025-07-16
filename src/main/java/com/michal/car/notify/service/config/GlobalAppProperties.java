package com.michal.car.notify.service.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.Set;

@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "global-app-props")
public class GlobalAppProperties {

    private String baseUrl;
    private String suffixTemplate;
    private List<String> watchCars;
    private Integer upperPrice;
    private boolean disableScraping;
    private Set<String> clients;

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

    public boolean getDisableScraping() {
        return disableScraping;
    }

    public void setDisableScraping(boolean disableScraping) {
        this.disableScraping = disableScraping;
    }

    public boolean isDisableScraping() {
        return disableScraping;
    }

    public Set<String> getClients() {
        return clients;
    }

    public void setClients(Set<String> clients) {
        this.clients = clients;
    }

    @Override
    public String toString() {
        return "GlobalAppProperties{" +
                "clients=" + clients +
                ", disableScraping=" + disableScraping +
                ", upperPrice=" + upperPrice +
                ", watchCars=" + watchCars +
                ", suffixTemplate='" + suffixTemplate + '\'' +
                ", baseUrl='" + baseUrl + '\'' +
                '}';
    }
}