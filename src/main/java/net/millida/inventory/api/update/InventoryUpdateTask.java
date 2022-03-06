package net.millida.inventory.api.update;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.millida.inventory.api.CustomInventory;

@RequiredArgsConstructor
@Getter
public class InventoryUpdateTask {

    private final CustomInventory customInventory;

    private final long updateTaskDelay;
    private final Runnable inventoryUpdateTask;
}
