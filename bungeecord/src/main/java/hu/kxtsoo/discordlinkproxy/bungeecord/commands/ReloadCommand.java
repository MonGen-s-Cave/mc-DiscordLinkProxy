package hu.kxtsoo.discordlinkproxy.bungeecord.commands;

import hu.kxtsoo.discordlinkproxy.common.util.ConfigUtil;
import hu.kxtsoo.discordlinkproxy.bungeecord.DiscordLinkBungee;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.plugin.Command;

public class ReloadCommand extends Command {

    private final ConfigUtil configUtil;

    public ReloadCommand() {
        super("discordlinkreload");
        this.configUtil = DiscordLinkBungee.getInstance().getConfigUtil();
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission("discordlinkproxy.reload")) {
            sender.sendMessage((BaseComponent) MiniMessage.miniMessage().deserialize(configUtil.getMessage("messages.reload-command.no-permission")));
            return;
        }

        configUtil.reloadConfig();
        sender.sendMessage((BaseComponent) MiniMessage.miniMessage().deserialize(configUtil.getMessage("messages.reload-command.successfully")));
    }
}
