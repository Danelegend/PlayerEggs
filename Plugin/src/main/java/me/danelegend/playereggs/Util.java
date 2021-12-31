package me.danelegend.playereggs;

import org.bukkit.ChatColor;

public class Util {
    public static String c(String msg) {
        msg = ChatColor.translateAlternateColorCodes('&', msg);
        return msg;
    }
}
