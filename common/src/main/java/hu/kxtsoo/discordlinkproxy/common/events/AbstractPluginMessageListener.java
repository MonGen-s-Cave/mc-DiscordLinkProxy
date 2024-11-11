package hu.kxtsoo.discordlinkproxy.common.events;

import java.util.UUID;
import java.util.Set;
import java.util.logging.Logger;

public abstract class AbstractPluginMessageListener {
    protected final Set<UUID> restrictedPlayers;
    protected final Logger logger;

    protected AbstractPluginMessageListener(Set<UUID> restrictedPlayers, Logger logger) {
        this.restrictedPlayers = restrictedPlayers;
        this.logger = logger;
    }

    protected void handlePluginMessage(UUID playerUUID, boolean require2FA) {
        if (require2FA) {
            restrictedPlayers.add(playerUUID);
            logger.info("2FA required for: " + playerUUID);
        } else {
            restrictedPlayers.remove(playerUUID);
            logger.info("2FA unlocked for: " + playerUUID);
        }
    }
}
