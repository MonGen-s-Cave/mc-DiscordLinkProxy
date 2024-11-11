package hu.kxtsoo.discordlinkproxy.bungeecord;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;
import hu.kxtsoo.discordlinkproxy.common.DiscordLinkInterface;
import hu.kxtsoo.discordlinkproxy.common.util.ConfigUtil;
import hu.kxtsoo.discordlinkproxy.bungeecord.commands.ReloadCommand;
import hu.kxtsoo.discordlinkproxy.bungeecord.events.BungeeCommandListener;
import hu.kxtsoo.discordlinkproxy.bungeecord.events.BungeePluginMessageListener;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;

public class DiscordLinkBungee extends Plugin implements DiscordLinkInterface {

    private static DiscordLinkBungee instance;

    private final Set<UUID> restrictedPlayers = new HashSet<>();
    private ConfigUtil configUtil;

    public static DiscordLinkBungee getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        instance = this;
        init();
    }

    @Override
    public void init() {
        Logger logger = getLogger();
        ProxyServer proxy = getProxy();

        String purple = "\u001B[38;5;99m";
        String yellow = "\u001B[33m";
        String reset = "\u001B[0m";

        System.out.println(" ");
        System.out.println(purple + "   ____ ___ ____   ____ ___  ____  ____  _     ___ _   _ _  __" + reset);
        System.out.println(purple + "   |  _ \\_ _/ ___| / ___/ _ \\|  _ \\|  _ \\| |   |_ _| \\ | | |/ /" + reset);
        System.out.println(purple + "   | | | | |\\___ \\| |  | | | | |_) | | | | |    | ||  \\| | ' / " + reset);
        System.out.println(purple + "   | |_| | | ___) | |__| |_| |  _ <| |_| | |___ | || |\\  | . \\ " + reset);
        System.out.println(purple + "   |____/___|____/ \\____\\___/|_| \\_\\____/|_____|___|_| \\_|_|\\_\\" + reset);
        System.out.println(" ");
        System.out.println(purple + "   The plugin successfully started." + reset);
        System.out.println(yellow + "   Discord @ dc.mongenscave.com" + reset);
        System.out.println(" ");

        configUtil = new ConfigUtil(getLogger(), getDataFolder().toPath());
        configUtil.reloadConfig();

        PluginManager pluginManager = proxy.getPluginManager();
        pluginManager.registerCommand(this, new ReloadCommand());
        pluginManager.registerListener(this, new BungeeCommandListener(restrictedPlayers));
        pluginManager.registerListener(this, new BungeePluginMessageListener(restrictedPlayers, getLogger()));

        proxy.registerChannel("discordlink:main");
    }

    @Override
    public Set<UUID> getRestrictedPlayers() {
        return restrictedPlayers;
    }

    @Override
    public void saveDefaultResource(String resourcePath) {
        configUtil.saveDefaultResource(resourcePath);
    }

    @Override
    public void loadConfiguration() {
        configUtil.reloadConfig();
    }

    public ConfigUtil getConfigUtil() {
        return configUtil;
    }
}