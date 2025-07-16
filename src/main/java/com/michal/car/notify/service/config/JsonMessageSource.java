package com.michal.car.notify.service.config;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;

/**
 * @author Michal Remis
 */
public class JsonMessageSource {

    private final Map<String, Map<String, String>> messages = new HashMap<>();

    public JsonMessageSource() {
        loadMessages("messages_sk.json", Locale.forLanguageTag("sk"));
        // Add messages_en.json later if needed
    }

    private void loadMessages(String resource, Locale locale) {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(resource)) {
            if (is == null) throw new FileNotFoundException("Missing: " + resource);

            ObjectMapper mapper = new ObjectMapper();
            Map<String, Map<String, String>> root = mapper.readValue(is, new TypeReference<>() {});
            messages.put(locale.toLanguageTag(), root.get("bot"));
        } catch (IOException e) {
            throw new UncheckedIOException("Failed to load JSON messages", e);
        }
    }

    public String get(String key) {
        return get(key, Locale.forLanguageTag("sk")); // default
    }

    public String get(String key, Locale locale) {
        Map<String, String> localeMessages = messages.get(locale.toLanguageTag());
        if (localeMessages == null) throw new MissingResourceException("Locale not loaded", "JsonMessageSource", key);
        String val = localeMessages.get(key);
        if (val == null) throw new MissingResourceException("Missing message key", "JsonMessageSource", key);
        return val;
    }
}
