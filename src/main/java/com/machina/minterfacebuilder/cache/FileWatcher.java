package com.machina.minterfacebuilder.cache;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.machina.minterfacebuilder.cache.TemplateCache;

/**
 * File watcher for monitoring file changes and automatically reloading templates.
 * <p>
 * This class monitors files using Java's WatchService API and automatically clears
 * the cache when files are modified, allowing templates to be reloaded from disk.
 * </p>
 * <p>
 * <b>Usage:</b>
 * </p>
 * <pre>
 * // Start watching a file
 * FileWatcher.watchFile(path);
 *
 * // Stop watching a file
 * FileWatcher.stopWatching(path);
 *
 * // Stop all watchers
 * FileWatcher.stopAll();
 * </pre>
 */
public class FileWatcher {
    /**
     * WatchService for monitoring file system events.
     */
    private static WatchService watchService = null;

    /**
     * Scheduled executor for running the watch service in a background thread.
     */
    private static ScheduledExecutorService executorService = null;

    /**
     * Map of watched paths to their WatchKeys.
     */
    private static final Map<Path, WatchKey> watchedPaths = new ConcurrentHashMap<>();

    /**
     * Map of WatchKeys to their parent directories (for reverse lookup).
     */
    private static final Map<WatchKey, Path> watchKeyToParentDir = new ConcurrentHashMap<>();

    /**
     * Flag to track if the watcher is initialized.
     */
    private static boolean initialized = false;

    /**
     * Initialize the watch service if not already initialized.
     * @throws IOException If the watch service cannot be created.
     */
    private static synchronized void initialize() throws IOException {
        if (!initialized) {
            watchService = FileSystems.getDefault().newWatchService();
            executorService = Executors.newSingleThreadScheduledExecutor(r -> {
                Thread t = new Thread(r, "InterfaceBuilder-FileWatcher");
                t.setDaemon(true);
                return t;
            });

            // Start the watch loop
            executorService.submit(() -> {
                try {
                    watchLoop();
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            });

            initialized = true;
        }
    }

    /**
     * Main watch loop that processes file system events.
     * @throws InterruptedException If the thread is interrupted.
     */
    private static void watchLoop() throws InterruptedException {
        while (true) {
            WatchKey key = watchService.take();

            // Process events for this key
            for (WatchEvent<?> event : key.pollEvents()) {
                WatchEvent.Kind<?> kind = event.kind();

                // Handle file modification events
                if (kind == StandardWatchEventKinds.ENTRY_MODIFY) {
                    @SuppressWarnings("unchecked")
                    WatchEvent<Path> ev = (WatchEvent<Path>) event;
                    Path filename = ev.context();

                    // Find the watched path that matches this event
                    Path watchedPath = findWatchedPath(key, filename);
                    if (watchedPath != null && Files.exists(watchedPath)) {
                        // Clear cache for this file so it will be reloaded
                        TemplateCache.remove(watchedPath);
                    }
                }

                // Handle file deletion events
                if (kind == StandardWatchEventKinds.ENTRY_DELETE) {
                    @SuppressWarnings("unchecked")
                    WatchEvent<Path> ev = (WatchEvent<Path>) event;
                    Path filename = ev.context();

                    // Find the watched path that matches this event
                    Path watchedPath = findWatchedPath(key, filename);
                    if (watchedPath != null) {
                        // Remove from cache and stop watching
                        TemplateCache.remove(watchedPath);
                        stopWatching(watchedPath);
                    }
                }
            }

            // Reset the key to receive further events
            boolean valid = key.reset();
            if (!valid) {
                // Key is no longer valid (directory deleted, etc.)
                break;
            }
        }
    }

    /**
     * Find the watched path that corresponds to a watch key and filename.
     * @param key The watch key.
     * @param filename The filename from the event.
     * @return The full watched path, or null if not found.
     */
    private static Path findWatchedPath(WatchKey key, Path filename) {
        // Get the parent directory for this watch key
        Path parentDir = watchKeyToParentDir.get(key);
        if (parentDir == null) {
            return null;
        }

        // Resolve the full path
        Path fullPath = parentDir.resolve(filename).normalize().toAbsolutePath();

        // Check if this path is being watched
        if (watchedPaths.containsKey(fullPath)) {
            return fullPath;
        }

        return null;
    }

    /**
     * Start watching a file for changes.
     * <p>
     * When the file is modified, it will be automatically removed from the cache,
     * causing it to be reloaded from disk on the next parse call.
     * </p>
     * <p>
     * <b>Example:</b>
     * </p>
     * <pre>
     * Path templatePath = Path.of("templates/mytemplate.html");
     * FileWatcher.watchFile(templatePath);
     * // File will now be automatically reloaded when changed
     * </pre>
     *
     * @param filePath The path to the file to watch. Must be an absolute path.
     * @throws IOException If the file cannot be watched (e.g., file doesn't exist or is not accessible).
     */
    public static void watchFile(Path filePath) throws IOException {
        if (filePath == null) {
            throw new IllegalArgumentException("File path cannot be null");
        }

        // Normalize the path
        filePath = filePath.normalize().toAbsolutePath();

        // Check if already watching
        if (watchedPaths.containsKey(filePath)) {
            return;
        }

        // Check if file exists
        if (!Files.exists(filePath)) {
            throw new IOException("File does not exist: " + filePath);
        }

        // Initialize if needed
        initialize();

        // Get the parent directory to watch
        Path parentDir = filePath.getParent();
        if (parentDir == null) {
            throw new IllegalArgumentException("Cannot watch root directory");
        }

        // Register the directory for watching
        WatchKey key = parentDir.register(
            watchService,
            StandardWatchEventKinds.ENTRY_MODIFY,
            StandardWatchEventKinds.ENTRY_DELETE
        );

        // Store the mapping
        watchedPaths.put(filePath, key);
        watchKeyToParentDir.put(key, parentDir);
    }

    /**
     * Stop watching a file for changes.
     * <p>
     * This will stop monitoring the file, but will not remove it from the cache.
     * The cached template will remain until explicitly removed or cleared.
     * </p>
     *
     * @param filePath The path to the file to stop watching.
     */
    public static void stopWatching(Path filePath) {
        if (filePath == null) {
            return;
        }

        // Normalize the path
        filePath = filePath.normalize().toAbsolutePath();

        // Remove from watched paths
        WatchKey key = watchedPaths.remove(filePath);
        if (key != null) {
            key.cancel();
            watchKeyToParentDir.remove(key);
        }
    }

    /**
     * Check if a file is currently being watched.
     * @param filePath The path to check.
     * @return True if the file is being watched, false otherwise.
     */
    public static boolean isWatching(Path filePath) {
        if (filePath == null) {
            return false;
        }

        // Normalize the path
        filePath = filePath.normalize().toAbsolutePath();

        return watchedPaths.containsKey(filePath);
    }

    /**
     * Stop watching all files and shutdown the watcher service.
     * <p>
     * This will stop all file watchers and shutdown the background thread.
     * The cache will not be cleared, but files will no longer be monitored.
     * </p>
     */
    public static synchronized void stopAll() {
        // Cancel all watch keys
        for (WatchKey key : watchedPaths.values()) {
            key.cancel();
        }

        // Clear watched paths and key mappings
        watchedPaths.clear();
        watchKeyToParentDir.clear();

        // Shutdown executor
        if (executorService != null) {
            executorService.shutdown();
            try {
                if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                    executorService.shutdownNow();
                }
            } catch (InterruptedException e) {
                executorService.shutdownNow();
                Thread.currentThread().interrupt();
            }
            executorService = null;
        }

        // Close watch service
        if (watchService != null) {
            try {
                watchService.close();
            } catch (IOException e) {
                // Ignore
            }
            watchService = null;
        }

        initialized = false;
    }

    /**
     * Get the number of files currently being watched.
     * @return The number of watched files.
     */
    public static int getWatchedFileCount() {
        return watchedPaths.size();
    }
}
