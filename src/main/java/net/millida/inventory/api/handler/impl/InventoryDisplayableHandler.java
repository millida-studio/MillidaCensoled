package net.millida.inventory.api.handler.impl;

import lombok.NonNull;
import net.millida.util.WeakObjectCache;
import org.bukkit.entity.Player;
import net.millida.inventory.api.CustomInventory;
import net.millida.inventory.api.handler.InventoryHandler;

public interface InventoryDisplayableHandler extends InventoryHandler {

    void onOpen(@NonNull Player player);
    void onClose(@NonNull Player player);

    @Override
    default void handle(@NonNull CustomInventory customInventory,
                        WeakObjectCache objectCache) {

        Player player = objectCache.getObject(Player.class, "player");
        boolean isOpen = objectCache.getObject(boolean.class, "isOpen");

        if (isOpen) {
            onOpen(player);
        } else {
            onClose(player);
        }
    }
}
