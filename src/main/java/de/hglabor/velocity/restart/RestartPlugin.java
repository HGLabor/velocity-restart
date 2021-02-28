package de.hglabor.velocity.restart;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.text.Component;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;

import javax.inject.Inject;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

@Plugin(id = "hglabor_restart", name = "HGLabor proxy restart", version = "0.2.0")
public final class RestartPlugin {
    private final ProxyServer server;
    private final int hourOfDay;

    @Inject
    public RestartPlugin(final ProxyServer server, final @DataDirectory Path dataDir) throws IOException {
        this.server = server;

        Files.createDirectories(dataDir);
        final var loader = HoconConfigurationLoader
            .builder()
            .setPath(dataDir.resolve("config.hocon"))
            .build();
        final var root = loader.load();
        hourOfDay = root.getNode("hourOfDay").act(node -> {
            if (node.isVirtual()) {
                node.setValue(3);
            }
        }).getInt();
        loader.save(root);
    }

    @Subscribe
    public void onProxyInitialization(final ProxyInitializeEvent event) {
        server
            .getScheduler()
            .buildTask(this, () -> server.shutdown(Component.text("Periodic restart")))
            .delay(LocalDateTime.now().until(nextHour(LocalDateTime.now(), hourOfDay), ChronoUnit.MILLIS), TimeUnit.MILLISECONDS)
            .schedule();
    }

    static LocalDateTime nextHour(final LocalDateTime now, final int hour) {
        var next = now.with(LocalTime.of(hour, 0));
        if (now.compareTo(next) >= 0) {
            next = next.plusDays(1);
        }
        return next;
    }
}
