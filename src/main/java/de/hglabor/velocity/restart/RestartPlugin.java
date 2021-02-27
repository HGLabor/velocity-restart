package de.hglabor.velocity.restart;

import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.text.Component;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;

import javax.inject.Inject;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.temporal.ChronoField;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

@Plugin(id = "hglabor_restart", name = "HGLabor proxy restart", version = "0.1.1")
public final class RestartPlugin {
    @Inject
    public RestartPlugin(final ProxyServer server, final @DataDirectory Path dataDir) throws IOException {
        final var loader = HoconConfigurationLoader
            .builder()
            .setPath(dataDir.resolve("config.hocon"))
            .build();
        final var root = loader.load();
        final int hour = root.getNode("hour").act(node -> {
            if (node.isVirtual()) {
                node.setValue(3);
            }
        }).getInt();
        loader.save(root);

        final var nextRestart = LocalDateTime.now().with((temp) ->
            (temp.get(ChronoField.HOUR_OF_DAY) > hour ? temp.plus(Period.ofDays(1)) : temp)
                .with(ChronoField.HOUR_OF_DAY, hour)
        );
        server
            .getScheduler()
            .buildTask(this, () -> server.shutdown(Component.text("Periodic restart")))
            .delay(LocalDateTime.now().until(nextRestart, ChronoUnit.MILLIS), TimeUnit.MILLISECONDS)
            .schedule();
    }
}
