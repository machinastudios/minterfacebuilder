package com.machina.minterfacebuilder.cache;

import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.machina.minterfacebuilder.model.HTMLCustomUITemplate;

/**
 * Cache manager for parsed Custom UI templates.
 * Provides thread-safe caching of templates by file path.
 */
public class TemplateCache {
    /**
     * Cache storage for parsed templates by Path.
     */
    private static final Map<Path, HTMLCustomUITemplate> cache = new ConcurrentHashMap<>();

    /**
     * Get a cached template by path.
     * @param path The file path.
     * @return The cached template, or null if not found.
     */
    public static HTMLCustomUITemplate get(Path path) {
        return cache.get(path);
    }

    /**
     * Store a template in the cache.
     * @param path The file path.
     * @param template The template to cache.
     */
    public static void put(Path path, HTMLCustomUITemplate template) {
        if (path != null && template != null) {
            cache.put(path, template);
        }
    }

    /**
     * Check if a template is cached.
     * @param path The file path.
     * @return True if the template is cached.
     */
    public static boolean contains(Path path) {
        return cache.containsKey(path);
    }

    /**
     * Remove a specific path from the cache.
     * @param path The Path to remove from cache.
     */
    public static void remove(Path path) {
        cache.remove(path);
    }

    /**
     * Clear all cached templates.
     */
    public static void clear() {
        cache.clear();
    }

    /**
     * Get the current cache size.
     * @return The number of cached templates.
     */
    public static int size() {
        return cache.size();
    }
}
