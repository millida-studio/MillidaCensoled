package net.millida.inventory.api;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.millida.inventory.api.handler.impl.InventoryDisplayableHandler;
import net.millida.util.WeakObjectCache;
import org.bukkit.entity.Player;
import net.millida.inventory.api.handler.InventoryHandler;
import net.millida.inventory.api.handler.impl.InventoryUpdateHandler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public interface CustomInventory {

    /**
     * Добавить новый обработчик
     * событий данному инвентарю
     *
     * @param lattyInventoryHandler - обработчик событий
     */
    default <T extends InventoryHandler> void addHandler(@NonNull Class<T> handlerClass, @NonNull T lattyInventoryHandler) {
        getInventoryInfo().addHandler(handlerClass, lattyInventoryHandler);
    }


    /**
     * Открыть инвентарь игроку
     */
    void openInventory(@NonNull Player player);

    /**
     * Открыть инвентарь игроку, используя
     * обработчик открытия и закрытия данного инвентаря
     *
     * @param inventoryDisplayableHandler - обработчик закрытия и открытия инвентаря
     */
    void openInventory(@NonNull Player player, @NonNull InventoryDisplayableHandler inventoryDisplayableHandler);


    /**
     * Единаразово очистить инвентарь
     */
    void clearInventory();


    /**
     * Единоразово обновить инвентарь и
     * все предметы внутри игроку
     */
    void updateInventory(@NonNull Player player);

    /**
     * Единоразово обновить инвентарь и
     * все предметы внутри игроку, используя
     * обработчик обновления инвентаря
     *
     * @param player - игрок
     * @param inventoryUpdateHandler - обработчик обновления инвентаря
     */
    void updateInventory(@NonNull Player player, @NonNull InventoryUpdateHandler inventoryUpdateHandler);

    /**
     * Включить цикличное автообновление инвентаря
     * до закрытия его игроком
     *
     * @param inventoryUpdateHandler - обработчик обновления инвентаря
     */
    void enableAutoUpdate(@NonNull Player player, InventoryUpdateHandler inventoryUpdateHandler, long duration);


    /**
     * Переопределяющийся метод
     * <p>
     * Отрисовка и настройка инвентаря, установка
     * предметов и разметка
     *
     * @param player - игрок
     */
    void drawInventory(@NonNull Player player);

    /**
     * Закрыть инвентарь игроку, вызывав
     * при наличии обработчик закрытия инвентаря
     */
    void closeInventory(@NonNull Player player);


    /**
     * Отрисовать предмет в инвентаре
     *
     * @param inventoryItem - предмет и его функции
     */
    void drawItem(@NonNull InventoryItem inventoryItem);


    /**
     * Установить разметку инвентаря для
     * разрешенных мест в установке предметов
     *
     * @param inventoryMarkup - разметка инвентаря
     */
    void setItemMarkup(@NonNull InventoryMarkup inventoryMarkup);


    /**
     * Получить обработчик информации инвентаря,
     * который хранит в себе как базовые поля,
     * так и различные списки предметов, хандлеров и т.д.
     */
    LattyInventoryInfo getInventoryInfo();

    LattyInventorySettings getInventorySettings();


    @Getter
    @RequiredArgsConstructor
    class LattyInventoryInfo {

        private final CustomInventory customInventory;

        private final String inventoryTitle;
        private final int inventorySize, inventoryRows;

        private final Multimap<Class<? extends InventoryHandler>, InventoryHandler> inventoryHandlerMap   = HashMultimap.create();
        private final HashMap<Integer, InventoryItem> inventoryItemMap                                    = new HashMap<>();


        public <T extends InventoryHandler> void addHandler(@NonNull Class<T> handlerClass,
                                                            @NonNull T lattyInventoryHandler) {

            inventoryHandlerMap.put(handlerClass, lattyInventoryHandler);
        }

        public void addItem(int itemSlot, @NonNull InventoryItem inventoryItem) {
            inventoryItemMap.put(itemSlot, inventoryItem);
        }


        public <T extends InventoryHandler> T getFirstHandler(@NonNull Class<T> inventoryHandlerClass) {
            return getHandlers(inventoryHandlerClass).stream().findFirst().orElse(null);
        }

        public <T extends InventoryHandler> Collection<T> getHandlers(@NonNull Class<T> inventoryHandlerClass) {
            Class<T> handlerClassKey = (Class<T>) inventoryHandlerMap.keySet().stream()
                    .filter(handlerClass -> handlerClass.isAssignableFrom(inventoryHandlerClass))
                    .findFirst()
                    .orElse(null);

            if (handlerClassKey == null) {
                return new ArrayList<>();
            }

            return (Collection<T>) inventoryHandlerMap.get(handlerClassKey);
        }

        public InventoryItem getItem(int itemSlot) {
            return inventoryItemMap.get(itemSlot);
        }

        public <T extends InventoryHandler> void handleHandlers(@NonNull Class<T> inventoryHandlerClass,
                                                                WeakObjectCache objectCache) {

            Collection<T> inventoryHandlerCollection = getHandlers(inventoryHandlerClass);

            for (InventoryHandler inventoryHandler : inventoryHandlerCollection) {
                inventoryHandler.handle(customInventory, objectCache);
            }
        }
    }

    @Getter
    @Setter
    class LattyInventorySettings {

        protected boolean useOnlyCacheItems = false;
    }

}
