package hu.kxtsoo.discordlinkproxy.velocity.events;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.player.ServerConnectedEvent;
import com.velocitypowered.api.proxy.Player;
import hu.kxtsoo.discordlinkproxy.common.events.AbstractServerConnectedListener;
import hu.kxtsoo.discordlinkproxy.common.util.ConfigUtil;
import hu.kxtsoo.discordlinkproxy.velocity.DiscordLinkVelocity;
import net.kyori.adventure.text.Component;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public class VelocityServerConnectedListener extends AbstractServerConnectedListener {

    private final ConfigUtil configUtil;

    public VelocityServerConnectedListener(Set<UUID> restrictedPlayers) {
        super(restrictedPlayers);
        this.configUtil = DiscordLinkVelocity.getInstance().getConfigUtil();
    }

    @Subscribe
    public void onServerSwitch(ServerConnectedEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();

        if (!isPlayerRestricted(uuid)) return;
        if (event.getPreviousServer().isEmpty()) return;

        String previous = event.getPreviousServer().get().getServerInfo().getName();
        if (!configUtil.isAuthServer(previous)) return;

        if (configUtil.getConfig().getBoolean("whitelisted-commands.player-kick.enabled")) {
            List<Component> kickLines = configUtil.getKickMessage();
            Component kickMessage = Component.empty();
            for (Component line : kickLines) {
                kickMessage = kickMessage.append(line).append(Component.newline());
            }
            player.disconnect(kickMessage);
        }
    }
}
