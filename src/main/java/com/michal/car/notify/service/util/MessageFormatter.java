package com.michal.car.notify.service.util;

import com.michal.car.notify.service.model.Car;

/**
 * @author Michal Remis
 */
public class  MessageFormatter {

    private static final String MESSAGE_TEMPLATE = """
        *%s*
        
        [Zobraziť ponuku](%s)
        
        *Cena:* %s €
        """;

    private MessageFormatter() {
        // not to be instantiated
    }

    public static String formatCarMessage(Car car) {
        return MESSAGE_TEMPLATE.formatted(car.title(), car.url(), car.price());
    }
}
