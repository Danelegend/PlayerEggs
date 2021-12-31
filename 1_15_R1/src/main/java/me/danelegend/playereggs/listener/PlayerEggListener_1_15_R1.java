package me.danelegend.playereggs.listener;

import me.danelegend.playereggs.managers.PlayerSkinManager;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.server.v1_15_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_15_R1.CraftServer;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerEggThrowEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class PlayerEggListener_1_15_R1 implements PlayerEggListener {
    private List<EntityPlayer> npcs = new ArrayList<>();
    private HashMap<EntityPlayer, Player> distances = new HashMap<>();

    private HashMap<Player, String> parser = new HashMap<>();

    private PlayerSkinManager psm;

    public PlayerEggListener_1_15_R1(PlayerSkinManager psm) {
        this.psm = psm;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEggThrowInitial(PlayerInteractEvent e) {
        if (e.getItem() == null || !e.getItem().getType().equals(Material.EGG)) {
            return;
        }

        if (!(e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK))) {
            return;
        }

        String eggName = ChatColor.stripColor(e.getItem().getItemMeta().getDisplayName());

        if (!eggName.contains(" Spawn Egg")) {
            parser.put(e.getPlayer(), null);
            return;
        }

        String skinName = eggName.replace(" Spawn Egg", "");

        parser.put(e.getPlayer(), skinName);
    }

    @EventHandler
    public void onEggThrow(PlayerEggThrowEvent e) {
        // Get the skin name of the egg thrown
        String skinName = parser.get(e.getPlayer());

        // If the egg is not of the form we want, do not perform any of this logic
        if (skinName == null) {
            return;
        }

        e.setHatching(false);

        // Get the texture value and signature
        String[] skin = psm.getPlayerTexture(skinName);

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
        GameProfile profile = new GameProfile(UUID.randomUUID(), psm.getActualPlayerName(skinName));

        // Give the profile the appropriate skin
        profile.getProperties().put("textures", new Property("textures", skin[0], skin[1]));

        // Spawn in the npc in the provided coords
        EntityPlayer npc = new EntityPlayer(server, server.getWorldServer(DimensionManager.a(0)),
                profile, new PlayerInteractManager(server.getWorldServer(DimensionManager.a(0))));
        npc.setPosition(x, y, z);

        // Add the npc to the npc list
        npcs.add(npc);

        // For each player, add the npc to the tab.
        // This is required for the npc to be visible.
        for (Player player : Bukkit.getOnlinePlayers()) {
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutPlayerInfo(PacketPlayOutPlayerInfo.EnumPlayerInfoAction.ADD_PLAYER, npc));
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutNamedEntitySpawn(npc));
        }

        Player nearest = getNearestPlayer(npc);

        npcLookAtPlayer(nearest, npc);

        distances.put(npc, nearest);
    }

    @EventHandler
    public void onPlayerMovement(PlayerMoveEvent e) {
        // Get this players distance from each npc
        Player p = e.getPlayer();

        for (EntityPlayer npc : npcs) {
            double dist = getPlayerDistanceFromNPC(p, npc);

            if ((dist < getPlayerDistanceFromNPC(distances.get(npc), npc) || p.equals(distances.get(npc))) && dist < 25) {
                distances.replace(npc, p);

                npcLookAtPlayer(p, npc);
            }
        }
    }

    private void npcLookAtPlayer(Player p, EntityPlayer npc) {
        // Move the head to look at p
        for (Player p1 : Bukkit.getOnlinePlayers()) {
            PlayerConnection pc = ((CraftPlayer) p1).getHandle().playerConnection;

            Location loc = npc.getBukkitEntity().getLocation();
            loc.setDirection(p.getLocation().subtract(loc).toVector());

            float yaw = loc.getYaw();
            float pitch = loc.getPitch();

            pc.sendPacket(new PacketPlayOutEntityHeadRotation(npc, (byte) ((yaw % 360) * 256 / 360)));
            pc.sendPacket(new PacketPlayOutEntity.PacketPlayOutEntityLook(npc.getBukkitEntity().getEntityId(),
                    (byte) ((yaw % 360) * 256 / 360), (byte) ((pitch % 360) * 256 / 360), true));
        }
    }

    private double getPlayerDistanceFromNPC(Player p, EntityPlayer npc) {
        return Math.sqrt(Math.pow(p.getLocation().getX() - npc.locX(), 2) +
                Math.pow(p.getLocation().getY() - npc.locY(), 2) +
                Math.pow(p.getLocation().getZ() - npc.locZ(), 2));
    }

    private Player getNearestPlayer(EntityPlayer npc) {
        // Go through all players on the server and get the nearest player
        Player closest = null;
        double minDist = Integer.MAX_VALUE;

        for (Player p : Bukkit.getOnlinePlayers()) {
            double dist = getPlayerDistanceFromNPC(p, npc);

            if (dist < minDist) {
                minDist = dist;
                closest = p;
            }
        }

        return closest;
    }
}
