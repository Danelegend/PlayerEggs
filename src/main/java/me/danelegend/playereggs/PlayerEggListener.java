package me.danelegend.playereggs;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.server.v1_15_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.craftbukkit.v1_15_R1.CraftServer;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerEggThrowEvent;

import java.util.UUID;


public class PlayerEggListener implements Listener {

    private PlayerEggs plugin;

    public PlayerEggListener(PlayerEggs plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onEggThrow(PlayerEggThrowEvent e) {
        // Get the display name of the egg with no colors on it
        String eggName = ChatColor.stripColor(e.getEgg().getItem().getItemMeta().getDisplayName());

        // If the egg is not of the form we want, do not perform any of this logic
        if (!eggName.contains(" Spawn Egg")) {
            return;
        }

        e.setHatching(false);

        // Get the name of the player whose skin we want to get
        String skinName = eggName.replace(" Spawn Egg", "");

        // Get the texture value and signature
        String[] skin = plugin.getPlayerSkinManager().getPlayerTexture(skinName);

        // If skin is null, then the inputted player does not exist.
        if (skin == null) {
            return;
        }

        // Get the x, y and z coords of where the egg landed
        int x = (int) e.getEgg().getLocation().getX();
        int y = (int) e.getEgg().getLocation().getY();
        int z = (int) e.getEgg().getLocation().getZ();

        MinecraftServer server = ((CraftServer) Bukkit.getServer()).getServer();

        // Create a new profile for the user that we want to spawn in
        GameProfile profile = new GameProfile(UUID.randomUUID(), plugin.getPlayerSkinManager().getActualPlayerName(skinName));

        // Give the profile the appropriate skin
        profile.getProperties().put("textures", new Property("textures", skin[0], skin[1]));

        // Spawn in the npc in the provided coords
        EntityPlayer npc = new EntityPlayer(server, server.getWorldServer(DimensionManager.a(0)),
                profile, new PlayerInteractManager(server.getWorldServer(DimensionManager.a(0))));
        npc.setPosition(x, y, z);

        // For each player, add the npc to the tab.
        // This is required for the npc to be visible.
        for (Player player : Bukkit.getOnlinePlayers()) {
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, npc));
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutNamedEntitySpawn(npc));
        }

    }

}
