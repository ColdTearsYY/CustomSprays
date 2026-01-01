package fun.LSDog.CustomSprays.data;

import org.bukkit.entity.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class CachedData implements IData {

    private final IData backingData;
    private final Map<UUID, byte[]> imageCache = new ConcurrentHashMap<>();
    private final Map<UUID, Boolean> copyAllowedCache = new ConcurrentHashMap<>();

    public CachedData(IData backingData) {
        this.backingData = backingData;
    }

    @Override
    public int saveImageBytes(Player player, byte[] imgBytes) {
        imageCache.put(player.getUniqueId(), imgBytes);
        return backingData.saveImageBytes(player, imgBytes);
    }

    @Override
    public byte[] getImageBytes(Player player) {
        return imageCache.computeIfAbsent(player.getUniqueId(), k -> backingData.getImageBytes(player));
    }

    @Override
    public void setCopyAllowed(Player player, boolean flag) {
        copyAllowedCache.put(player.getUniqueId(), flag);
        backingData.setCopyAllowed(player, flag);
    }

    @Override
    public boolean getCopyAllowed(Player player) {
        return copyAllowedCache.computeIfAbsent(player.getUniqueId(), k -> backingData.getCopyAllowed(player));
    }

    @Override
    public void invalidateCache(Player player) {
        imageCache.remove(player.getUniqueId());
        copyAllowedCache.remove(player.getUniqueId());
    }
}
