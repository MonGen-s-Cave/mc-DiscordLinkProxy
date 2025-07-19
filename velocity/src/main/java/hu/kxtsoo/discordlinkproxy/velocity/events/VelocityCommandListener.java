package hu.kxtsoo.discordlinkproxy.velocity.events;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.command.CommandExecuteEvent;
import com.velocitypowered.api.proxy.Player;
import com.velocitypowered.api.proxy.ServerConnection;
import hu.kxtsoo.discordlinkproxy.common.events.AbstractCommandListener;
import hu.kxtsoo.discordlinkproxy.common.util.ConfigUtil;
import hu.kxtsoo.discordlinkproxy.velocity.DiscordLinkVelocity;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public class VelocityCommandListener extends AbstractCommandListener {

    private final ConfigUtil configUtil;

    public VelocityCommandListener(Set<UUID> restrictedPlayers) {
        super(restrictedPlayers);
        this.configUtil = DiscordLinkVelocity.getInstance().getConfigUtil();
    }

    @Subscribe
    public void onCommandExecute(CommandExecuteEvent event) {
        if (!(event.getCommandSource() instanceof Player player)) return;

        UUID playerUUID = player.getUniqueId();
        String command = event.getCommand();

        ServerConnection serverConnection = player.getCurrentServer().orElse(null);
        if (serverConnection == null) return;

        if (configUtil.isAuthServer(serverConnection.getServerInfo().getName()) && restrictedPlayers.contains(playerUUID)) {
            if (configUtil.isWhitelistedCommand(command)) {
                event.setResult(CommandExecuteEvent.CommandResult.allowed());
                return;
            }

            event.setResult(CommandExecuteEvent.CommandResult.denied());

            if (configUtil.getConfig().getBoolean("whitelisted-commands.player-kick.enabled")) {
                List<Component> kickMessageComponents = configUtil.getKickMessage();

                Component kickMessage = Component.empty();
                for (Component line : kickMessageComponents) {
                    kickMessage = kickMessage.append(line).append(Component.newline());
                }

                player.disconnect(kickMessage);
            } else player.sendMessage(MiniMessage.miniMessage().deserialize(configUtil.getMessage("messages.command-use.deny")));
        }
    }
}
