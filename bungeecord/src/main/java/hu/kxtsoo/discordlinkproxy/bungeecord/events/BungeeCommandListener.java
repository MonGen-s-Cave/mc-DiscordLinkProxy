package hu.kxtsoo.discordlinkproxy.bungeecord.events;

import hu.kxtsoo.discordlinkproxy.common.events.AbstractCommandListener;
import hu.kxtsoo.discordlinkproxy.common.util.ConfigUtil;
import hu.kxtsoo.discordlinkproxy.bungeecord.DiscordLinkBungee;
import net.kyori.adventure.text.Component;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public class BungeeCommandListener extends AbstractCommandListener implements Listener {

    private final ConfigUtil configUtil;

    public BungeeCommandListener(Set<UUID> restrictedPlayers) {
        super(restrictedPlayers);
        this.configUtil = DiscordLinkBungee.getInstance().getConfigUtil();
    }

    @EventHandler
    public void onCommandExecute(ChatEvent event) {
        if (!(event.getSender() instanceof ProxiedPlayer player)) return;

        UUID playerUUID = player.getUniqueId();
        String command = event.getMessage().split(" ")[0].replace("/", "");

        if (configUtil.isWhitelistedCommand(command)) {
            return;
        }

        String serverName = player.getServer().getInfo().getName();
        if (!configUtil.isAuthServer(serverName)) {
            return;
        }

        if (restrictedPlayers.contains(playerUUID)) {
            if (configUtil.getConfig().getBoolean("whitelisted-commands.player-kick.enabled")) {
                List<Component> kickMessageComponents = configUtil.getKickMessage();

                Component kickMessage = Component.empty();
                for (Component line : kickMessageComponents) {
                    kickMessage = kickMessage.append(line).append(Component.newline());
                }

                player.disconnect((BaseComponent) kickMessage);
            } else {
                player.sendMessage(configUtil.getMessage("messages.command-use.deny"));
            }
            event.setCancelled(true);
        }
    }
}