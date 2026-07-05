package com.thornedshields.paper;

import org.bukkit.plugin.java.JavaPlugin;

public final class ThornedShieldsPlugin extends JavaPlugin {

    private ThornedShieldsConfig thornedShieldsConfig;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        reloadThornedShieldsConfig();
        getServer().getPluginManager().registerEvents(new ThornedShieldsListener(this), this);
        getCommand("thornshield").setExecutor(new ThornShieldCommand(this));
        getLogger().info("ThornedShields enabled - Thorns now works on Shields.");
    }

    @Override
    public void onDisable() {
        getLogger().info("ThornedShields disabled.");
    }

    public void reloadThornedShieldsConfig() {
        reloadConfig();
        this.thornedShieldsConfig = new ThornedShieldsConfig(getConfig());
    }

    public ThornedShieldsConfig getThornedShieldsConfig() {
        return thornedShieldsConfig;
    }
}
