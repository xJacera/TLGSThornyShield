package com.thornedshields.paper;

import org.bukkit.configuration.file.FileConfiguration;

/**
 * Thin wrapper around config.yml, mirroring the options from the original
 * Fabric mod's cloth-config screen.
 */
public final class ThornedShieldsConfig {

    private final boolean enabled;
    private final int chanceThorns1;
    private final int chanceThorns2;
    private final int chanceThorns3;
    private final double damageThorns1;
    private final double damageThorns2;
    private final double damageThorns3;
    private final boolean durabilityConsumption;
    private final int durabilityConsumptionAmount;
    private final boolean allowAnvilApplication;

    public ThornedShieldsConfig(FileConfiguration cfg) {
        this.enabled = cfg.getBoolean("enabled", true);
        this.chanceThorns1 = cfg.getInt("blocking-chance-thorns-1", 50);
        this.chanceThorns2 = cfg.getInt("blocking-chance-thorns-2", 75);
        this.chanceThorns3 = cfg.getInt("blocking-chance-thorns-3", 100);
        this.damageThorns1 = cfg.getDouble("blocking-damage-thorns-1", 2.0);
        this.damageThorns2 = cfg.getDouble("blocking-damage-thorns-2", 4.0);
        this.damageThorns3 = cfg.getDouble("blocking-damage-thorns-3", 6.0);
        this.durabilityConsumption = cfg.getBoolean("blocking-durability-consumption", true);
        this.durabilityConsumptionAmount = cfg.getInt("blocking-durability-consumption-amount", 2);
        this.allowAnvilApplication = cfg.getBoolean("allow-anvil-application", true);
    }

    public boolean isEnabled() {
        return enabled;
    }

    public int chanceFor(int thornsLevel) {
        return switch (thornsLevel) {
            case 1 -> chanceThorns1;
            case 2 -> chanceThorns2;
            default -> chanceThorns3;
        };
    }

    public double damageFor(int thornsLevel) {
        return switch (thornsLevel) {
            case 1 -> damageThorns1;
            case 2 -> damageThorns2;
            default -> damageThorns3;
        };
    }

    public boolean isDurabilityConsumptionEnabled() {
        return durabilityConsumption;
    }

    public int getDurabilityConsumptionAmount() {
        return durabilityConsumptionAmount;
    }

    public boolean isAllowAnvilApplication() {
        return allowAnvilApplication;
    }
}
