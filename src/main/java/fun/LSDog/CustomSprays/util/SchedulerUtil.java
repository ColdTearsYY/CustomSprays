package fun.LSDog.CustomSprays.util;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class SchedulerUtil {

    private static boolean isFolia = false;

    static {
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            isFolia = true;
        } catch (ClassNotFoundException e) {
            isFolia = false;
        }
    }

    public static boolean isFolia() {
        return isFolia;
    }

    public static void runTask(Plugin plugin, Runnable runnable) {
        if (isFolia) {
            Bukkit.getGlobalRegionScheduler().execute(plugin, runnable);
        } else {
            Bukkit.getScheduler().runTask(plugin, runnable);
        }
    }

    public static void runTask(Plugin plugin, Entity entity, Runnable runnable) {
        if (isFolia) {
            entity.getScheduler().run(plugin, task -> runnable.run(), null);
        } else {
            Bukkit.getScheduler().runTask(plugin, runnable);
        }
    }

    public static void runTaskAsynchronously(Plugin plugin, Runnable runnable) {
        if (isFolia) {
            Bukkit.getAsyncScheduler().runNow(plugin, task -> runnable.run());
        } else {
            Bukkit.getScheduler().runTaskAsynchronously(plugin, runnable);
        }
    }

    public static void runTaskLater(Plugin plugin, Runnable runnable, long delay) {
        if (isFolia) {
            Bukkit.getGlobalRegionScheduler().runDelayed(plugin, task -> runnable.run(), delay);
        } else {
            Bukkit.getScheduler().runTaskLater(plugin, runnable, delay);
        }
    }

    public static void runTaskLater(Plugin plugin, Entity entity, Runnable runnable, long delay) {
        if (isFolia) {
            entity.getScheduler().runDelayed(plugin, task -> runnable.run(), null, delay);
        } else {
            Bukkit.getScheduler().runTaskLater(plugin, runnable, delay);
        }
    }

    public static void runTaskLater(Plugin plugin, Location location, Runnable runnable, long delay) {
        if (isFolia) {
            Bukkit.getRegionScheduler().runDelayed(plugin, location, task -> runnable.run(), delay);
        } else {
            Bukkit.getScheduler().runTaskLater(plugin, runnable, delay);
        }
    }

    public static void runTaskLaterAsynchronously(Plugin plugin, Runnable runnable, long delay) {
        if (isFolia) {
            Bukkit.getAsyncScheduler().runDelayed(plugin, task -> runnable.run(), delay * 50, java.util.concurrent.TimeUnit.MILLISECONDS);
        } else {
            Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, runnable, delay);
        }
    }

    public static <T> T callSyncMethod(Plugin plugin, Entity entity, Callable<T> callable) throws Exception {
        if (isFolia) {
            if (Bukkit.isOwnedByCurrentRegion(entity)) {
                return callable.call();
            } else {
                CompletableFuture<T> future = new CompletableFuture<>();
                entity.getScheduler().run(plugin, task -> {
                    try {
                        future.complete(callable.call());
                    } catch (Exception e) {
                        future.completeExceptionally(e);
                    }
                }, null);
                return future.get();
            }
        } else {
            if (Bukkit.isPrimaryThread()) {
                return callable.call();
            } else {
                return Bukkit.getScheduler().callSyncMethod(plugin, callable).get();
            }
        }
    }
    
    public static void cancelTasks(Plugin plugin) {
        if (isFolia) {
            Bukkit.getAsyncScheduler().cancelTasks(plugin);
            Bukkit.getGlobalRegionScheduler().cancelTasks(plugin);
        } else {
            Bukkit.getScheduler().cancelTasks(plugin);
        }
    }
}
