package hu.kxtsoo.discordlinkproxy.velocity;

import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import com.velocitypowered.api.proxy.messages.MinecraftChannelIdentifier;
import hu.kxtsoo.discordlinkproxy.common.DiscordLinkInterface;
import hu.kxtsoo.discordlinkproxy.common.util.ConfigUtil;
import hu.kxtsoo.discordlinkproxy.velocity.commands.ReloadCommand;
import hu.kxtsoo.discordlinkproxy.velocity.events.VelocityCommandListener;
import hu.kxtsoo.discordlinkproxy.velocity.events.VelocityPluginMessageListener;
import hu.kxtsoo.discordlinkproxy.velocity.events.VelocityServerConnectedListener;

import javax.inject.Inject;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;

@Plugin(id = "discordlinkproxy", name = "DiscordLinkProxy", version = "1.0.0", authors = {"kxtsoo"})
public class DiscordLinkVelocity implements DiscordLinkInterface {

    private static DiscordLinkVelocity instance;

    private final Set<UUID> restrictedPlayers = new HashSet<>();

    private final ProxyServer server;
    private final Logger logger;
    private final Path dataDirectory;

    private ConfigUtil configUtil;

    public static final MinecraftChannelIdentifier IDENTIFIER = MinecraftChannelIdentifier.create("discordlink", "main");

    @Inject
    private ProxyServer proxy;

    @Inject
    public DiscordLinkVelocity(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory) {
        this.server = server;
        this.logger = logger;
        this.dataDirectory = dataDirectory;
        instance = this;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        init();
    }

    @Override
    public void init() {
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

        configUtil = new ConfigUtil(logger, dataDirectory);
        configUtil.reloadConfig();
        server.getChannelRegistrar().register(IDENTIFIER);
        System.out.println("Servers: " + server.getAllServers());

        server.getEventManager().register(this, new VelocityCommandListener(restrictedPlayers));
        server.getEventManager().register(this, new VelocityPluginMessageListener(restrictedPlayers, logger));
        server.getEventManager().register(this, new VelocityServerConnectedListener(restrictedPlayers));

        server.getCommandManager().register("discordlinkreload", new ReloadCommand());
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

    public static DiscordLinkVelocity getInstance() {
        return instance;
    }

    public ProxyServer getProxy() {
        return proxy;
    }

    public ConfigUtil getConfigUtil() {
        return configUtil;
    }
}
