package me.danelegend.playereggs;

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

        // Register the listeners
        Bukkit.getPluginManager().registerEvents(new PlayerEggListener(this), this);

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
