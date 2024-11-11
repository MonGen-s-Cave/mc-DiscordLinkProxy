package hu.kxtsoo.discordlinkproxy.velocity.commands;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import hu.kxtsoo.discordlinkproxy.common.util.ConfigUtil;
import hu.kxtsoo.discordlinkproxy.velocity.DiscordLinkVelocity;
import net.kyori.adventure.text.minimessage.MiniMessage;

public class ReloadCommand implements SimpleCommand {

    private final ConfigUtil configUtil;

    public ReloadCommand() {
        this.configUtil = DiscordLinkVelocity.getInstance().getConfigUtil();
    }


    @Override
    public void execute(SimpleCommand.Invocation invocation) {
        CommandSource source = invocation.source();

        if (!(source.hasPermission("discordlinkproxy.reload"))) {
            source.sendMessage(MiniMessage.miniMessage().deserialize(configUtil.getMessage("messages.reload-command.no-permission")));
            return;
        }

        DiscordLinkVelocity.getInstance().getConfigUtil().reloadConfig();
        source.sendMessage(MiniMessage.miniMessage().deserialize(configUtil.getMessage("messages.reload-command.successfully")));
    }

}
