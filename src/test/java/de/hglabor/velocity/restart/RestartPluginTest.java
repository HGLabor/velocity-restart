package de.hglabor.velocity.restart;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RestartPluginTest {
    @Test
    void testNextHour_sameDay() {
        final var now = LocalDateTime.of(2000, 1, 1, 3, 58);
        assertEquals(now.with(LocalTime.of(4, 0)), RestartPlugin.nextHour(now, 4));
    }

    @Test
    void testNextHour_nextDay() {
        final var now = LocalDateTime.of(2000, 1, 1, 4, 0, 1);
        assertEquals(now.with(LocalTime.of(4, 0)).plusDays(1), RestartPlugin.nextHour(now, 4));
    }
}
