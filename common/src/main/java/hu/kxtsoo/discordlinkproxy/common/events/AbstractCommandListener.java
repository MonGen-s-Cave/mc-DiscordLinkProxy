package hu.kxtsoo.discordlinkproxy.common.events;

import java.util.UUID;
import java.util.Set;

public abstract class AbstractCommandListener {
    protected final Set<UUID> restrictedPlayers;

    protected AbstractCommandListener(Set<UUID> restrictedPlayers) {
        this.restrictedPlayers = restrictedPlayers;
    }

    protected boolean isPlayerRestricted(UUID playerUUID) {
        return restrictedPlayers.contains(playerUUID);
    }
}
