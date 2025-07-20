package com.michal.car.notify.service.model;

import java.time.Instant;

/**
 * @author Michal Remis
 */
public record FoundCar(String id,
                       String title,
                       String model,
                       String url,
                       String photoUrl,
                       Integer price,
                       Instant createdAt) {
}