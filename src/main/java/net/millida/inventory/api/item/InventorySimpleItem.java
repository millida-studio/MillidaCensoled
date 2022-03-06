package net.millida.inventory.api.item;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.millida.inventory.api.CustomInventory;
import net.millida.inventory.api.InventoryItem;
import org.bukkit.inventory.ItemStack;

@AllArgsConstructor
@Getter
public class InventorySimpleItem implements InventoryItem {

    @Setter
    private int slot;

    private final ItemStack itemStack;

    @Override
    public void onDraw(@NonNull CustomInventory customInventory) {
        // не важно
    }

}
