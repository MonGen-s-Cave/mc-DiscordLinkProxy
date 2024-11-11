package hu.kxtsoo.discordlinkproxy.common;

public class DiscordLinkProxy {

    public static void main(String[] args) {
        DiscordLinkInterface instance;

        if (isVelocity()) {
            instance = loadInstance("hu.kxtsoo.discordlinkproxy.velocity.DiscordLinkVelocity");
        } else {
            instance = loadInstance("hu.kxtsoo.discordlinkproxy.bungeecord.DiscordLinkBungee");
        }

        if (instance != null) {
            instance.init();
        } else {
            System.err.println("Failed to initialize plugin: Unsupported platform.");
        }
    }

    private static boolean isVelocity() {
        try {
            Class.forName("com.velocitypowered.api.proxy.ProxyServer");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    private static DiscordLinkInterface loadInstance(String className) {
        try {
            Class<?> clazz = Class.forName(className);
            return (DiscordLinkInterface) clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}