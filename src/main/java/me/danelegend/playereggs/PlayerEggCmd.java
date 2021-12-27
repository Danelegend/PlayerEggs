package me.danelegend.playereggs;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class PlayerEggCmd implements CommandExecutor {

    private PlayerEggs plugin;

    public PlayerEggCmd(PlayerEggs plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        // Check that the user that sent the command is a player
        if (!(sender instanceof Player)) {
            return false;
        }

        Player p = (Player) sender;

        // Check that the command conforms to required syntax
        if (args.length != 2) {
            p.sendMessage(Util.c("[PlayerEggs]: Syntax is /playeregg {Player Name} {Amount}"));

            return false;
        }

        // Get the player name
        String skinName = args[0];

        // Get the amount of eggs we want
        int amount = Integer.parseInt(args[1]);

        // Add the item to the senders inventory
        p.getInventory().addItem(createItem(skinName, amount));

        // For efficiency, we get the skin once the command is run
        plugin.getPlayerSkinManager().addTexture(skinName);

        return false;
    }

    // Returns the egg that can be thrown to spawn the player
    private ItemStack createItem(String playerName, int amount) {
        ItemStack is = new ItemStack(Material.EGG);
        ItemMeta im = is.getItemMeta();

        String displayName = "&c" + playerName + " Spawn Egg";

        List<String> lore = new ArrayList<String>();

        lore.add("");
        lore.add(Util.c("&7Throw the egg the spawn the player where it lands."));

        im.setDisplayName(Util.c(displayName));
        im.setLore(lore);

        im.addEnchant(Enchantment.DURABILITY, 1, true);
        im.addItemFlags(ItemFlag.HIDE_ENCHANTS);

        is.setItemMeta(im);

        is.setAmount(amount);

        return is;
    }

}
