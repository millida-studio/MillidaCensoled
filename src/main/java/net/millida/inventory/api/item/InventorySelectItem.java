package net.millida.inventory.api.item;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.millida.inventory.api.InventoryItem;
import org.bukkit.inventory.ItemStack;
import net.millida.inventory.api.CustomInventory;
import net.millida.inventory.api.handler.impl.InventoryClickHandler;

@AllArgsConstructor
@Getter
public class InventorySelectItem implements InventoryItem {

    @Setter
    private int slot;

    private final ItemStack itemStack;

    private final InventoryClickHandler inventoryClickHandler;
    private final boolean enchanting;

    @Setter
    private boolean selected;

    @Override
    public void onDraw(@NonNull CustomInventory customInventory) {
        // не важно
    }

}
