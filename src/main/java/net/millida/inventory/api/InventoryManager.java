package net.millida.inventory.api;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.Getter;
import lombok.NonNull;
import net.millida.util.WeakObjectCache;
import org.bukkit.entity.Player;
import net.millida.inventory.api.handler.InventoryHandler;
import net.millida.inventory.api.update.InventoryUpdateTask;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

@Getter
public final class InventoryManager {

    private final LattyInventoryHandlerManager inventoryHandlerManager = new LattyInventoryHandlerManager();
    private final Cache<Player, CustomInventory> inventoryCache = CacheBuilder.newBuilder().expireAfterAccess(30, TimeUnit.MINUTES).build();

    private final Map<CustomInventory, InventoryUpdateTask> inventoryUpdateTaskMap = new ConcurrentHashMap<>();

    /**
     * Кешировать инвентарь, открытый игроку
     *
     * @param player         - игроку
     * @param customInventory - инвентарь
     */
    public void createInventory(@NonNull Player player, @NonNull CustomInventory customInventory) {
        inventoryCache.put(player, customInventory);
    }

    /**
     * Удалить из кеша инвентарь, недавно открытый игроку
     *
     * @param player         - игроку
     * @param customInventory - инвентарь
     */
    public void removeInventory(@NonNull Player player, @NonNull CustomInventory customInventory) {
        inventoryCache.asMap().remove(player, customInventory);
        player.closeInventory();
    }

    /**
     * Получить открытый инвентарь игрока
     *
     * @param player - игрок
     */
    public CustomInventory getPlayerInventory(@NonNull Player player) {
        inventoryCache.cleanUp();

        return inventoryCache.asMap().get(player);
    }

    public void addInventoryUpdateTask(@NonNull CustomInventory customInventory, @NonNull InventoryUpdateTask inventoryUpdateTask) {
        if (inventoryUpdateTaskMap.containsKey(customInventory)) {
            return;
        }

        inventoryUpdateTaskMap.put(customInventory, inventoryUpdateTask);
    }

    public void removeInventoryUpdateTask(@NonNull CustomInventory customInventory) {
        inventoryUpdateTaskMap.remove(customInventory);
    }


    @Getter
    public static final class LattyInventoryHandlerManager {

        /**
         * Добавить новый хандлер в список
         * обработчиков инвентаря
         *
         * @param customInventory        - инвентарь
         * @param lattyInventoryHandler - обработчик
         */
        public <T extends InventoryHandler> void add(@NonNull CustomInventory customInventory,
                                                     @NonNull Class<T> handlerClass,
                                                     @NonNull T lattyInventoryHandler) {

            customInventory.getInventoryInfo().addHandler(handlerClass, lattyInventoryHandler);
        }

        /**
         * Получить список хандлеров инвентаря
         * по указанному типу обработчика
         *
         * @param customInventory        - инвентарь
         * @param inventoryHandlerClass - класс обработчика
         */
        public <T extends InventoryHandler> Collection<T> get(@NonNull CustomInventory customInventory,
                                                              @NonNull Class<? extends T> inventoryHandlerClass) {

            return (Collection<T>) customInventory.getInventoryInfo().getHandlers(inventoryHandlerClass);
        }

        /**
         * Получить первый хандлер по типу его класса
         * из списка обработчиков инвентаря
         *
         * @param customInventory        - инвентарь
         * @param inventoryHandlerClass - класс обработчика
         */
        public <T extends InventoryHandler> T getFirst(@NonNull CustomInventory customInventory,
                                                       @NonNull Class<T> inventoryHandlerClass) {

            return customInventory.getInventoryInfo().getFirstHandler(inventoryHandlerClass);
        }


        public void handle(@NonNull CustomInventory customInventory,
                           @NonNull Class<? extends InventoryHandler> inventoryHandlerClass,

                           WeakObjectCache weakObjectCache) {

            customInventory.getInventoryInfo().handleHandlers(inventoryHandlerClass, weakObjectCache);
        }

    }
}
