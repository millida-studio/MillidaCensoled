package net.millida.inventory.api.handler.impl;

import lombok.NonNull;
import net.millida.inventory.api.CustomInventory;
import net.millida.inventory.api.handler.InventoryHandler;
import net.millida.util.WeakObjectCache;
import org.bukkit.entity.Player;

public interface InventoryUpdateHandler extends InventoryHandler {

    void onUpdate(@NonNull CustomInventory customInventory, @NonNull Player player);

    @Override
    default void handle(@NonNull CustomInventory customInventory,
                        WeakObjectCache objectCache) {

        onUpdate(customInventory, objectCache.getObject(Player.class, "player"));
    }
}
