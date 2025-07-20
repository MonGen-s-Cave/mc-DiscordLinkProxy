package hu.kxtsoo.discordlinkproxy.velocity.events;

import com.google.common.io.ByteArrayDataInput;
import com.google.common.io.ByteStreams;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.PluginMessageEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import hu.kxtsoo.discordlinkproxy.common.events.AbstractPluginMessageListener;
import hu.kxtsoo.discordlinkproxy.velocity.DiscordLinkVelocity;

import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;

public class VelocityPluginMessageListener extends AbstractPluginMessageListener {

    private final Logger logger;

    public VelocityPluginMessageListener(Set<UUID> restrictedPlayers, Logger logger) {
        super(restrictedPlayers, logger);
        this.logger = logger;
    }

    @Subscribe
    public void onPluginMessageReceived(PluginMessageEvent event) {
        String identifier = event.getIdentifier().getId();

        if (!identifier.equals("discordlink:main") && !identifier.equals("discordlink:main (modern)")) {
            System.out.println("channel not registered");
            return;
        }

        ByteArrayDataInput in = ByteStreams.newDataInput(event.getData());
        UUID playerUUID = UUID.fromString(in.readUTF());
        boolean require2FA = in.readBoolean();

        Player player = DiscordLinkVelocity.getInstance().getProxy().getPlayer(playerUUID).orElse(null);

        if (player != null && player.getCurrentServer().isPresent()) {
            ServerConnection serverConnection = player.getCurrentServer().get();
            String serverName = serverConnection.getServerInfo().getName();

            if (DiscordLinkVelocity.getInstance().getConfigUtil().isAuthServer(serverName)) {
                if (require2FA) {
                    restrictedPlayers.add(playerUUID);
                } else {
                    restrictedPlayers.remove(playerUUID);
                }
            }
        } else {
            logger.warning("Player not found or not connected to any server: " + playerUUID);
        }
    }
}