package com.michal.car.notify.service.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.michal.car.notify.service.model.User;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Path;
import java.time.Instant;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

class UserServiceTest {

    @TempDir
    static Path tempDir;

    private static UserService service;

    @BeforeAll
    static void setup() {
        Path jsonFile = tempDir.resolve("users.json");
        service = new UserService(new ObjectMapper(), jsonFile.toString());
    }

    @BeforeEach
    void cleanUp() {
        service.overwriteAllUsers(List.of());
    }

    @Test
    void shouldUpdateNotificationFlag() {
        service.addNotApprovedUser("321");

        service.setEnabledNotifications("321", true);
        assertThat(service.findByChatId("321").get().getNotificationsEnabled()).isTrue();
    }

    @Test
    void shouldUpdatePriceLimitAndModels() {
        service.addNotApprovedUser("555");

        service.setUpperPriceLimitForUser("555", 15000);
        service.setWatchedCarModelsForUser("555", Set.of("rav4", "auris"));

        User updated = service.getOneByChatId("555");

        assertThat(updated.getUpperPriceLimit()).isEqualTo(15000);
        assertThat(updated.getWatchForModels()).containsExactlyInAnyOrder("rav4", "auris");
    }

    @Test
    void shouldFindPendingAndApprovedUsers() {
        service.addNotApprovedUser("999");
        service.setApprovedForUser("999", true);

        service.addNotApprovedUser("888");

        assertThat(service.findAllApprovedUsers()).hasSize(1);
        assertThat(service.findNotApprovedUsers()).hasSize(1);
    }

    @Test
    void shouldFindAllApprovedWithNotificationsEnabled() {
        service.addNotApprovedUser("444");
        service.setApprovedForUser("444", true);
        service.setEnabledNotifications("444", true);

        service.addNotApprovedUser("333");
        service.setApprovedForUser("333", true);
        service.setEnabledNotifications("333", false);

        List<User> filtered = service.findAllApprovedWithEnabledNotifications();
        assertThat(filtered).extracting(User::getChatId).containsExactly("444");
    }

    @Test
    void shouldReturnMaxPriceAcrossUsers() {
        service.overwriteAllUsers(List.of(
                new User("1", true, true, 10000, Set.of("auris"), Instant.now()),
                new User("2", true, true, 15000, Set.of("corolla"), Instant.now())
        ));

        Integer max = service.getMaxPrice();
        assertThat(max).isEqualTo(15000);
    }

    @Test
    void shouldReturnAllDistinctModels() {
        service.overwriteAllUsers(List.of(
                new User("1", true, true, 10000, Set.of("auris", "rav4"), Instant.now()),
                new User("2", true, true, 10000, Set.of("corolla", "rav4"), Instant.now())
        ));

        Set<String> distinct = service.getAllDistinctWatchedCarModels();
        assertThat(distinct).containsExactlyInAnyOrder("auris", "rav4", "corolla");
    }
}
