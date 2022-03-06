package net.millida.inventory.api;

import lombok.NonNull;
import org.bukkit.inventory.ItemStack;

public interface InventoryItem {

    int getSlot();
    void setSlot(int itemSlot);

    ItemStack getItemStack();


    void onDraw(@NonNull CustomInventory customInventory);
}
