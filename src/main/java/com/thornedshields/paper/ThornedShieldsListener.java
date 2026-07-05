package com.thornedshields.paper;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.Registry;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.Damageable;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Random;

/**
 * Ports the behaviour of "ales' Thorned Shields" (Fabric) to Paper:
 *  - Lets Thorns be combined onto a Shield in an anvil.
 *  - When a player blocks a hit with a Thorns-enchanted shield, has a
 *    chance to reflect damage back onto the attacker and (optionally)
 *    consume shield durability.
 */
public final class ThornedShieldsListener implements Listener {

    private final ThornedShieldsPlugin plugin;
    private final Random random = new Random();

    public ThornedShieldsListener(ThornedShieldsPlugin plugin) {
        this.plugin = plugin;
    }

    private Enchantment thorns() {
        return Registry.ENCHANTMENT.get(NamespacedKey.minecraft("thorns"));
    }

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    public void onPrepareAnvil(PrepareAnvilEvent event) {
        ThornedShieldsConfig cfg = plugin.getThornedShieldsConfig();
        if (!cfg.isEnabled() || !cfg.isAllowAnvilApplication()) {
            return;
        }

        ItemStack left = event.getInventory().getItem(0);
        ItemStack right = event.getInventory().getItem(1);
        if (left == null || right == null) {
            return;
        }
        if (left.getType() != Material.SHIELD) {
            return;
        }

        Enchantment thorns = thorns();
        if (thorns == null) {
            return;
        }

        int bookLevel = right.getEnchantmentLevel(thorns);
        if (bookLevel <= 0) {
            return;
        }

        ItemStack result = event.getResult();
        if (result == null) {
            // Vanilla refused to produce a result (shield isn't a valid target
            // for Thorns) - build one ourselves off a clone of the shield.
            result = left.clone();
        }

        int existing = result.getEnchantmentLevel(thorns);
        int newLevel = Math.max(existing, bookLevel);
        if (existing >= bookLevel) {
            return;
        }

        result.addUnsafeEnchantment(thorns, newLevel);
        event.setResult(result);
    }

    @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        ThornedShieldsConfig cfg = plugin.getThornedShieldsConfig();
        if (!cfg.isEnabled()) {
            return;
        }

        if (!(event.getEntity() instanceof Player victim)) {
            return;
        }
        if (!victim.isBlocking()) {
            return;
        }

        Enchantment thorns = thorns();
        if (thorns == null) {
            return;
        }

        // Figure out which hand is holding the shield.
        ItemStack mainHand = victim.getInventory().getItemInMainHand();
        ItemStack offHand = victim.getInventory().getItemInOffHand();

        EquipmentSlot shieldSlot;
        ItemStack shield;
        if (offHand.getType() == Material.SHIELD) {
            shieldSlot = EquipmentSlot.OFF_HAND;
            shield = offHand;
        } else if (mainHand.getType() == Material.SHIELD) {
            shieldSlot = EquipmentSlot.HAND;
            shield = mainHand;
        } else {
            return;
        }

        int thornsLevel = shield.getEnchantmentLevel(thorns);
        if (thornsLevel <= 0) {
            return;
        }

        LivingEntity attacker = resolveAttacker(event);
        if (attacker == null) {
            return;
        }

        int chance = cfg.chanceFor(thornsLevel);
        if (random.nextInt(100) >= chance) {
            return;
        }

        double damage = cfg.damageFor(thornsLevel);
        attacker.damage(damage, victim);

        if (cfg.isDurabilityConsumptionEnabled()) {
            damageShield(victim, shieldSlot, shield, cfg.getDurabilityConsumptionAmount());
        }

        victim.getWorld().playSound(victim.getLocation(), Sound.ENCHANT_THORNS_HIT, 1.0f, 1.0f);
    }

    private LivingEntity resolveAttacker(EntityDamageByEntityEvent event) {
        if (event.getDamager() instanceof LivingEntity livingEntity) {
            return livingEntity;
        }
        if (event.getDamager() instanceof Projectile projectile
                && projectile.getShooter() instanceof LivingEntity shooter) {
            return shooter;
        }
        return null;
    }

    private void damageShield(Player victim, EquipmentSlot slot, ItemStack shield, int amount) {
        ItemMeta meta = shield.getItemMeta();
        if (!(meta instanceof Damageable damageable)) {
            return;
        }

        int newDamage = damageable.getDamage() + amount;
        int maxDurability = shield.getType().getMaxDurability();

        if (maxDurability > 0 && newDamage >= maxDurability) {
            // Shield breaks.
            if (slot == EquipmentSlot.OFF_HAND) {
                victim.getInventory().setItemInOffHand(null);
            } else {
                victim.getInventory().setItemInMainHand(null);
            }
            victim.getWorld().playSound(victim.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0f, 1.0f);
            return;
        }

        damageable.setDamage(newDamage);
        shield.setItemMeta(meta);
        if (slot == EquipmentSlot.OFF_HAND) {
            victim.getInventory().setItemInOffHand(shield);
        } else {
            victim.getInventory().setItemInMainHand(shield);
        }
    }
}
