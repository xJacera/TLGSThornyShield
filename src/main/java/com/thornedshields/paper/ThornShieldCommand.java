package com.thornedshields.paper;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

/**
 * /thornshield give <player> [level]
 *
 * Hands a player a pre-enchanted "Limited Edition" Thorned Shield directly,
 * bypassing the anvil entirely. This is the intended way to distribute the
 * shield as an event reward, since regular anvil-enchanting of shields with
 * Thorns is disabled by default.
 */
public final class ThornShieldCommand implements CommandExecutor {

    private final ThornedShieldsPlugin plugin;

    public ThornShieldCommand(ThornedShieldsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("thornedshields.give")) {
            sender.sendMessage(Component.text("You don't have permission to do that.", NamedTextColor.RED));
            return true;
        }

        if (args.length == 0 || !args[0].equalsIgnoreCase("give")) {
            sender.sendMessage(Component.text("Usage: /thornshield give <player> [level 1-3]", NamedTextColor.YELLOW));
            return true;
        }

        if (args.length < 2) {
            sender.sendMessage(Component.text("Usage: /thornshield give <player> [level 1-3]", NamedTextColor.YELLOW));
            return true;
        }

        Player target = Bukkit.getPlayerExact(args[1]);
        if (target == null) {
            sender.sendMessage(Component.text("Player '" + args[1] + "' is not online.", NamedTextColor.RED));
            return true;
        }

        int level = 3;
        if (args.length >= 3) {
            try {
                level = Integer.parseInt(args[2]);
            } catch (NumberFormatException e) {
                sender.sendMessage(Component.text("Level must be a number 1-3.", NamedTextColor.RED));
                return true;
            }
        }
        if (level < 1 || level > 3) {
            sender.sendMessage(Component.text("Level must be between 1 and 3.", NamedTextColor.RED));
            return true;
        }

        Enchantment thorns = Registry.ENCHANTMENT.get(NamespacedKey.minecraft("thorns"));
        if (thorns == null) {
            sender.sendMessage(Component.text("Could not find the Thorns enchantment on this server.", NamedTextColor.RED));
            return true;
        }

        ItemStack shield = new ItemStack(Material.SHIELD);
        ItemMeta meta = shield.getItemMeta();
        meta.displayName(
                Component.text("Thorned Shield", NamedTextColor.LIGHT_PURPLE)
                        .decoration(TextDecoration.ITALIC, false)
        );

        List<Component> lore = new ArrayList<>();
        lore.add(Component.text("Limited Edition Event Reward", NamedTextColor.GOLD)
                .decoration(TextDecoration.ITALIC, false));
        lore.add(Component.text("Thorns " + romanNumeral(level), NamedTextColor.GRAY)
                .decoration(TextDecoration.ITALIC, false));
        meta.lore(lore);

        shield.setItemMeta(meta);
        shield.addUnsafeEnchantment(thorns, level);

        target.getInventory().addItem(shield);
        target.sendMessage(Component.text("You received a Limited Edition Thorned Shield!", NamedTextColor.LIGHT_PURPLE));
        sender.sendMessage(Component.text("Gave " + target.getName() + " a Thorns " + romanNumeral(level) + " shield.", NamedTextColor.GREEN));
        return true;
    }

    private String romanNumeral(int level) {
        return switch (level) {
            case 1 -> "I";
            case 2 -> "II";
            default -> "III";
        };
    }
}
