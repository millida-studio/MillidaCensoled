package net.millida.inventory.api.handler.impl;

import lombok.NonNull;
import net.millida.util.WeakObjectCache;
import org.bukkit.event.inventory.InventoryClickEvent;
import net.millida.inventory.api.CustomInventory;
import net.millida.inventory.api.handler.InventoryHandler;

public interface InventoryClickHandler extends InventoryHandler {

    void onClick(@NonNull CustomInventory customInventory, @NonNull InventoryClickEvent inventoryClickEvent);

    @Override
    default void handle(@NonNull CustomInventory customInventory,
                        WeakObjectCache objectCache) {

        onClick(customInventory, objectCache.getObject(InventoryClickEvent.class, "event"));
    }
}
