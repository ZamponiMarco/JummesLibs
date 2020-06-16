package com.github.jummes.libs.core;

import org.bukkit.configuration.serialization.ConfigurationSerialization;
import org.bukkit.plugin.java.JavaPlugin;

import com.github.jummes.libs.listener.PluginInventoryHolderClickListener;
import com.github.jummes.libs.listener.StringSettingChangeChatListener;
import com.github.jummes.libs.localization.PluginLocale;
import com.github.jummes.libs.model.math.IntRange;
import com.github.jummes.libs.model.wrapper.ItemMetaWrapper;
import com.github.jummes.libs.model.wrapper.ItemStackWrapper;
import com.github.jummes.libs.model.wrapper.LocationWrapper;
import com.github.jummes.libs.wrapper.VersionWrapper;

import lombok.Getter;

@Getter
public class Libs {

    private static final String PACKAGE_PREFIX = "com.github.jummes.libs.wrapper.VersionWrapper_";

    @Getter
    private static JavaPlugin plugin;

    @Getter
    private static VersionWrapper wrapper;

    @Getter
    private static PluginLocale locale;

    public static void registerSerializables() {
        ConfigurationSerialization.registerClass(LocationWrapper.class);
        ConfigurationSerialization.registerClass(ItemStackWrapper.class);
        ConfigurationSerialization.registerClass(ItemMetaWrapper.class);
        ConfigurationSerialization.registerClass(IntRange.class);
    }

    public static void initializeLibrary(JavaPlugin thisPlugin, PluginLocale thisLocale) {
        plugin = thisPlugin;
        setUpWrapper(plugin);
        locale = thisLocale;
        registerEvents(plugin);
    }

    private static void registerEvents(JavaPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(new PluginInventoryHolderClickListener(), plugin);
        plugin.getServer().getPluginManager().registerEvents(new StringSettingChangeChatListener(plugin), plugin);
    }

    private static void setUpWrapper(JavaPlugin plugin) {
        String serverVersion = plugin.getServer().getClass().getPackage().getName();
        String version = serverVersion.substring(serverVersion.lastIndexOf('.') + 1);

        try {
            wrapper = (VersionWrapper) Class.forName(PACKAGE_PREFIX + version).getConstructor().newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
