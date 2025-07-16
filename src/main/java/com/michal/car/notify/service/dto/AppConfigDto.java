package com.michal.car.notify.service.dto;

import java.util.List;

/**
 * @author Michal Remis
 */
public record AppConfigDto(List<String> watchCars,
                           Integer upperPrice,
                           Boolean disableJob,
                           String adminChatId) {
}
