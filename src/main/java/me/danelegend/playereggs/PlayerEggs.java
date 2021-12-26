package me.danelegend.playereggs;

import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public final class PlayerEggs extends JavaPlugin {

    private Logger logger;

    @Override
    public void onEnable() {
        this.logger = this.getLogger();

        logger.info("[PlayerEggs]: Starting Player Eggs");


        logger.info("[PlayerEggs]: Plugin Loaded");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
