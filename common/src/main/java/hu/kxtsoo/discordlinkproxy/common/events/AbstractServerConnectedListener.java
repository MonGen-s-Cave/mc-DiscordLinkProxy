package hu.kxtsoo.discordlinkproxy.common.events;

import java.util.Set;
import java.util.UUID;

public class AbstractServerConnectedListener {
    protected final Set<UUID> restrictedPlayers;

    protected AbstractServerConnectedListener(Set<UUID> restrictedPlayers) {
        this.restrictedPlayers = restrictedPlayers;
    }

    protected boolean isPlayerRestricted(UUID playerUUID) {
        return restrictedPlayers.contains(playerUUID);
    }
}
