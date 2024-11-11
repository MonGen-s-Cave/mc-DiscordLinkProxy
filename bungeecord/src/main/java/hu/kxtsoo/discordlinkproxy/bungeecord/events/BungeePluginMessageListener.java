package hu.kxtsoo.discordlinkproxy.bungeecord.events;

import hu.kxtsoo.discordlinkproxy.common.events.AbstractPluginMessageListener;
import hu.kxtsoo.discordlinkproxy.bungeecord.DiscordLinkBungee;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;

public class BungeePluginMessageListener extends AbstractPluginMessageListener implements Listener {

    private final Logger logger;

    public BungeePluginMessageListener(Set<UUID> restrictedPlayers, Logger logger) {
        super(restrictedPlayers, logger);
        this.logger = logger;
    }

    @EventHandler
    public void onPluginMessageReceived(PluginMessageEvent event) {
        if (!event.getTag().equals("discordlink:main")) {
            return;
        }

        ProxiedPlayer player = (ProxiedPlayer) event.getReceiver();
        UUID playerUUID = player.getUniqueId();
        boolean require2FA = event.getData()[0] != 0;

        String serverName = player.getServer().getInfo().getName();
        if (DiscordLinkBungee.getInstance().getConfigUtil().isAuthServer(serverName)) {
            if (require2FA) {
                restrictedPlayers.add(playerUUID);
                logger.info("Requires 2FA confirmation: " + playerUUID);
            } else {
                restrictedPlayers.remove(playerUUID);
                logger.info("Successful 2FA confirmation: " + playerUUID);
            }
        }
    }
}