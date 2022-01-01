package me.danelegend.playereggs;

import me.danelegend.playereggs.listener.PlayerEggListener_1_14_R1;
import me.danelegend.playereggs.listener.PlayerEggListener_1_15_R1;
import me.danelegend.playereggs.managers.PlayerSkinManager;
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

        this.psm = new PlayerSkinManager();

        // Register the listeners
        if (version.equals("v1_15_R1")) {
            Bukkit.getPluginManager().registerEvents(new PlayerEggListener_1_15_R1(psm), this);
        } else if (version.equals("v1_14_R1")) {
            Bukkit.getPluginManager().registerEvents(new PlayerEggListener_1_14_R1(psm), this);
        }

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
