package net.millida.inventory.api.handler;

import lombok.NonNull;
import net.millida.inventory.api.CustomInventory;
import net.millida.util.WeakObjectCache;

public interface InventoryHandler {

    void handle(@NonNull CustomInventory customInventory, WeakObjectCache objectCache);
}
