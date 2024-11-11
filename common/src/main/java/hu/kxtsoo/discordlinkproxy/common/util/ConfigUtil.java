package hu.kxtsoo.discordlinkproxy.common.util;

import dev.dejvokep.boostedyaml.YamlDocument;
import dev.dejvokep.boostedyaml.dvs.versioning.BasicVersioning;
import dev.dejvokep.boostedyaml.settings.dumper.DumperSettings;
import dev.dejvokep.boostedyaml.settings.general.GeneralSettings;
import dev.dejvokep.boostedyaml.settings.loader.LoaderSettings;
import dev.dejvokep.boostedyaml.settings.updater.UpdaterSettings;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

public class ConfigUtil {

    private final Logger logger;
    private final Path dataDirectory;
    private YamlDocument config;
    private YamlDocument messages;

    public ConfigUtil(Logger logger, Path dataDirectory) {
        this.logger = logger;
        this.dataDirectory = dataDirectory;
        setupConfig();
        setupMessages();
    }

    private void setupConfig() {
        File configFile = dataDirectory.resolve("config.yml").toFile();

        try {
            if (!configFile.exists()) {
                saveDefaultResource("config.yml");
            }

            config = YamlDocument.create(configFile,
                    Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("config.yml")),
                    GeneralSettings.builder().setUseDefaults(false).build(),
                    LoaderSettings.DEFAULT, DumperSettings.DEFAULT,
                    UpdaterSettings.builder().setKeepAll(true)
                            .setVersioning(new BasicVersioning("version")).build());

            config.update();
        } catch (IOException ex) {
            logger.severe("Error loading or creating config.yml: " + ex.getMessage());
        }
    }

    private void setupMessages() {
        File messageFile = dataDirectory.resolve("messages.yml").toFile();

        try {
            messages = YamlDocument.create(messageFile,
                    Objects.requireNonNull(getClass().getClassLoader().getResourceAsStream("messages.yml")),
                    GeneralSettings.builder().setUseDefaults(false).build(),
                    LoaderSettings.DEFAULT, DumperSettings.DEFAULT,
                    UpdaterSettings.builder()
                            .setVersioning(new BasicVersioning("version"))
                            .setKeepAll(true)
                            .build());

            messages.update();
        } catch (IOException ex) {
            logger.severe("Error loading or creating messages file: " + ex.getMessage());
        }
    }

    public void saveDefaultResource(String resourcePath) {
        File target = dataDirectory.resolve(resourcePath).toFile();
        if (!target.exists()) {
            try (InputStream in = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
                if (in == null) {
                    throw new IllegalArgumentException("Resource not found: " + resourcePath);
                }
                target.getParentFile().mkdirs();
                Files.copy(in, target.toPath());
            } catch (IOException e) {
                logger.severe("Could not save default resource: " + e.getMessage());
            }
        }
    }

    public String getMessage(String key) {
        Object messageObj = messages.get(key, "Message not found");

        String prefix = config.getString("prefix", "");
        if (messageObj instanceof String) {
            String message = (String) messageObj;
            if (message.contains("%prefix%")) {
                message = message.replace("%prefix%", prefix);
            }
            return message;
        } else if (messageObj instanceof List) {
            List<String> messageList = (List<String>) messageObj;
            messageList = messageList.stream()
                    .map(msg -> msg.contains("%prefix%") ? msg.replace("%prefix%", prefix) : msg)
                    .toList();
            return String.join("\n", messageList);
        }

        return "Invalid message format";
    }

    public YamlDocument getConfig() {
        return config;
    }

    public YamlDocument getMessages() {
        return messages;
    }

    public void reloadConfig() {
        try {
            config.reload();
            messages.reload();
        } catch (IOException ex) {
            logger.severe("Error reloading configuration files: " + ex.getMessage());
        }
    }

    public boolean isAuthServer(String serverName) {
        List<String> authServers = config.getStringList("auth-server");
        return authServers.contains(serverName.toLowerCase());
    }

    public boolean isWhitelistedCommand(String command) {
        List<String> whitelistedCommands = config.getStringList("whitelisted-commands.allowed-commands");

        String baseCommand = command.split(" ")[0].trim().toLowerCase();
        return whitelistedCommands.contains(baseCommand);
    }

    public List<Component> getKickMessage() {
        List<String> messageLines = config.getStringList("whitelisted-commands.player-kick.message");
        return messageLines.stream()
                .map(line -> MiniMessage.miniMessage().deserialize(line))
                .toList();
    }

}
