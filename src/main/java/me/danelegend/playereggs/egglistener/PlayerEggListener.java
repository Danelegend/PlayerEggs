package me.danelegend.playereggs.egglistener;

import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerEggThrowEvent;

public interface PlayerEggListener extends Listener {

    void onEggThrow(PlayerEggThrowEvent e);

}
