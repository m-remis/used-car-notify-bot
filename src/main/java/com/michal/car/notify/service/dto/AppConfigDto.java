package com.michal.car.notify.service.dto;


/**
 * @author Michal Remis
 */
public record AppConfigDto(Boolean disableJob,
                           String adminChatId) {
}
