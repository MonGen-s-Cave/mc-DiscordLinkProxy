package hu.kxtsoo.discordlinkproxy.common;

import java.util.UUID;
import java.util.Set;

public interface DiscordLinkInterface {
    void init();

    Set<UUID> getRestrictedPlayers();
    void saveDefaultResource(String resourcePath);
    void loadConfiguration();
}