package me.danelegend.playereggs.listener;

import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerEggThrowEvent;
import org.bukkit.event.player.PlayerMoveEvent;

public interface PlayerEggListener extends Listener {

    void onEggThrow(PlayerEggThrowEvent e);

    void onPlayerMovement(PlayerMoveEvent e);

}
