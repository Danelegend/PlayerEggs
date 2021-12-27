package me.danelegend.playereggs;

import me.danelegend.playereggs.egglistener.PlayerEggListener_1_15_2;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class PlayerEggs extends JavaPlugin {

    private Logger logger;
    private PlayerSkinManager psm;

    @Override
    public void onEnable() {
        this.logger = this.getLogger();

        logger.info("Starting Player Eggs");

        this.psm = new PlayerSkinManager(this);

        // Register the command
        Bukkit.getPluginCommand("playeregg").setExecutor(new PlayerEggCmd(this));

        // Check the spigot server version
        String version;

        try {
            version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        } catch (ArrayIndexOutOfBoundsException e) {
            this.getServer().getPluginManager().disablePlugin(this);
            return;
        }

        System.out.println(version);

        if (version.equals("v1_15_R1")) {
            Bukkit.getPluginManager().registerEvents(new PlayerEggListener_1_15_2(this), this);
        }

        // Register the listeners


        logger.info("Plugin Loaded");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        logger.info("Plugin shutdown successfully");
    }

    public PlayerSkinManager getPlayerSkinManager() {
        return this.psm;
    }
}
