package com.machina.minterfacebuilder.util;

import java.nio.file.Path;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Utility class to resolve asset paths from JavaPlugin instances.
 * <p>
 * This class provides a convenient way to access assets from plugins/mod
 * that have an AssetPack registered. It resolves paths relative to the plugin's
 * AssetPack root, which is the recommended way to load assets from mods/plugins.
 * </p>
 * <p>
 * <b>Requirements:</b>
 * </p>
 * <ul>
 * <li>The plugin must have {@code "includesAssetPack": true} in its manifest.json</li>
 * <li>The plugin must be loaded and its AssetPack must be registered</li>
 * </ul>
 * <p>
 * <b>Example usage:</b>
 * </p>
 * <pre>
 * // In your JavaPlugin class
 * public class MyPlugin extends JavaPlugin {
 *     
 *     public void init() {
 *         // Resolve asset path using PluginAsset
 *         Path uiPath = PluginAsset.of(this, "Common/UI/Test.html");
 *         
 *         // Parse the template using InterfaceBuilder.parse() (not parseAsset)
 *         // because we already have a resolved Path
 *         try {
 *             ParsedCustomUITemplate template = InterfaceBuilder.parse(uiPath);
 *             // Use template...
 *         } catch (Exception e) {
 *             getLogger().severe("Failed to load UI: " + e.getMessage());
 *         }
 *     }
 * }
 * </pre>
 */
public class PluginAsset {
    /**
     * Resolve an asset path from a JavaPlugin instance.
     * <p>
     * This method obtains the plugin's AssetPack and resolves the given path
     * relative to the pack's root. This is the recommended way to load assets
     * from plugins/mods, as {@code AssetModule.findAssetPackForPath()} may not
     * work correctly for mod assets.
     * </p>
     * <p>
     * <b>Note:</b> This method requires the plugin to have {@code "includesAssetPack": true}
     * in its manifest.json. If the AssetPack is not found, returns null.
     * </p>
     *
     * @param plugin The JavaPlugin instance to resolve the asset from.
     * @param assetPath The relative path to the asset within the plugin's AssetPack (e.g., "Common/UI/Test.html").
     * @return The resolved Path to the asset, or null if the AssetPack is not found or AssetModule is unavailable.
     * @throws IllegalArgumentException If plugin or assetPath is null.
     */
    @Nullable
    public static Path of(@Nonnull Object plugin, @Nonnull String assetPath) {
        if (plugin == null) {
            throw new IllegalArgumentException("Plugin cannot be null");
        }
        if (assetPath == null) {
            throw new IllegalArgumentException("Asset path cannot be null");
        }

        try {
            // Get AssetModule
            Object assetModule = Class.forName("com.hypixel.hytale.server.core.asset.AssetModule")
                .getMethod("get")
                .invoke(null);

            if (assetModule == null) {
                // AssetModule not initialized yet
                return null;
            }

            // Get plugin identifier
            Object identifier = plugin.getClass()
                .getMethod("getIdentifier")
                .invoke(plugin);

            if (identifier == null) {
                return null;
            }

            // Convert identifier to string
            String pluginId = identifier.toString();

            // Get AssetPack for this plugin
            Object assetPack = assetModule.getClass()
                .getMethod("getAssetPack", String.class)
                .invoke(assetModule, pluginId);

            if (assetPack == null) {
                // AssetPack not found for this plugin
                return null;
            }

            // Get the root path of the asset pack
            Path packRoot = (Path) assetPack.getClass()
                .getMethod("getRoot")
                .invoke(assetPack);

            // Resolve the asset path relative to the pack root
            return packRoot.resolve(assetPath).normalize();
        } catch (Exception e) {
            // AssetModule not available, reflection failed, or plugin doesn't have AssetPack
            // Return null to indicate failure
            return null;
        }
    }
}
