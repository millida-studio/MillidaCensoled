package net.millida.inventory.api.item;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.millida.inventory.api.InventoryItem;
import net.millida.inventory.api.handler.impl.InventoryClickHandler;
import org.bukkit.inventory.ItemStack;
import net.millida.inventory.api.CustomInventory;

@AllArgsConstructor
@Getter
public class InventoryClickItem implements InventoryItem {

    @Setter
    private int slot;

    private final ItemStack itemStack;
    private final InventoryClickHandler inventoryClickHandler;

    @Override
    public void onDraw(@NonNull CustomInventory customInventory) {
        // не важно
    }

}
