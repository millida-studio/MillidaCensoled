package net.millida.inventory.api.impl;

import lombok.Getter;
import lombok.NonNull;
import net.millida.CensurePlugin;
import net.millida.inventory.api.InventoryItem;
import net.millida.inventory.api.handler.impl.InventoryClickHandler;
import net.millida.inventory.api.handler.impl.InventoryDisplayableHandler;
import net.millida.inventory.api.item.InventoryClickItem;
import net.millida.inventory.api.item.InventorySelectItem;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import net.millida.inventory.api.CustomInventory;
import net.millida.inventory.api.InventoryMarkup;
import net.millida.inventory.api.handler.impl.InventoryUpdateHandler;
import net.millida.inventory.api.item.InventorySimpleItem;
import net.millida.inventory.api.update.InventoryUpdateTask;

@Getter
public abstract class SimpleCustomInventory implements CustomInventory {

    protected final int inventoryRows;
    protected final String inventoryTitle;

    protected LattyInventoryInfo inventoryInfo;
    protected org.bukkit.inventory.Inventory inventory;

    protected InventoryMarkup inventoryMarkup;

    protected final LattyInventorySettings inventorySettings = new LattyInventorySettings();


    public SimpleCustomInventory(int inventoryRows, @NonNull String inventoryTitle) {
        this.inventoryRows = inventoryRows;
        this.inventoryTitle = inventoryTitle;

        this.inventoryInfo = new LattyInventoryInfo(this, inventoryTitle, inventoryRows * 9, inventoryRows);

        this.inventory = Bukkit.createInventory(null, inventoryInfo.getInventorySize(), inventoryTitle);
    }

    @Override
    public void openInventory(@NonNull Player player) {
        closeInventory(player);
        drawInventory(player);

        for (InventoryItem inventoryItem : inventoryInfo.getInventoryItemMap().values()) {

            inventory.setItem(inventoryItem.getSlot(), inventoryItem.getItemStack());
        }

        CensurePlugin.INSTANCE.getInventoryManager()
                .createInventory(player, this);

        player.openInventory(inventory);
    }

    @Override
    public void openInventory(@NonNull Player player, @NonNull InventoryDisplayableHandler inventoryDisplayableHandler) {
        addHandler(InventoryDisplayableHandler.class, inventoryDisplayableHandler);

        openInventory(player);
    }

    @Override
    public void clearInventory() {
        inventoryInfo.getInventoryItemMap().clear();
        inventory.clear();
    }

    @Override
    public void updateInventory(@NonNull Player player) {
        clearInventory();
        drawInventory(player);

        for (InventoryItem inventoryItem : inventoryInfo.getInventoryItemMap().values()) {

            inventory.setItem(inventoryItem.getSlot(), inventoryItem.getItemStack());
        }
    }

    @Override
    public void updateInventory(@NonNull Player player, @NonNull InventoryUpdateHandler inventoryUpdateHandler) {
        inventoryUpdateHandler.onUpdate(this, player);
        updateInventory(player);
    }

    @Override
    public void enableAutoUpdate(@NonNull Player player, InventoryUpdateHandler inventoryUpdateHandler, long secondDelay) {
        CensurePlugin.INSTANCE.getInventoryManager().addInventoryUpdateTask(this, new InventoryUpdateTask(this, secondDelay, () -> {

            if (inventoryUpdateHandler != null)
                updateInventory(player, inventoryUpdateHandler);
            else
                updateInventory(player);
        }));
    }

    @Override
    public void closeInventory(@NonNull Player player) {
        if (player.getOpenInventory() == null) {
            return;
        }

        player.closeInventory();

        CensurePlugin.INSTANCE.getInventoryManager().removeInventory(player, this);
        CensurePlugin.INSTANCE.getInventoryManager().removeInventoryUpdateTask(this);
    }

    @Override
    public void drawItem(@NonNull InventoryItem inventoryItem) {
        if (inventoryMarkup != null && !inventoryMarkup.hasInventorySlot(inventoryItem.getSlot())) {
            return;
        }

        inventoryItem.onDraw(this);

        inventoryInfo.addItem(inventoryItem.getSlot() - 1, inventoryItem);
    }

    @Override
    public void setItemMarkup(@NonNull InventoryMarkup inventoryMarkup) {
        this.inventoryMarkup = inventoryMarkup;
    }

    public void drawItem(int itemSlot, @NonNull ItemStack itemStack) {
        drawItem(new InventorySimpleItem(itemSlot - 1, itemStack));
    }

    public void drawItem(int itemSlot, @NonNull ItemStack itemStack, @NonNull InventoryClickHandler inventoryClickHandler) {
        drawItem(new InventoryClickItem(itemSlot - 1, itemStack, inventoryClickHandler));
    }

    public void drawItemSelect(int itemSlot, @NonNull ItemStack itemStack, @NonNull InventoryClickHandler inventoryClickHandler, boolean isEnchanting) {
        drawItem(new InventorySelectItem(itemSlot - 1, itemStack, inventoryClickHandler, isEnchanting, false));
    }

    public void drawItemSelect(int itemSlot, @NonNull ItemStack itemStack, @NonNull InventoryClickHandler inventoryClickHandler) {
        drawItemSelect(itemSlot, itemStack, inventoryClickHandler, true);
    }

    @Override
    public abstract void drawInventory(@NonNull Player player);

}
