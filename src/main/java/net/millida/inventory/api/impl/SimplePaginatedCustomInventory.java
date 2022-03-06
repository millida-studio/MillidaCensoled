package net.millida.inventory.api.impl;

import lombok.Getter;
import lombok.NonNull;
import net.millida.CensurePlugin;
import net.millida.inventory.api.CustomInventory;
import net.millida.inventory.api.InventoryItem;
import net.millida.inventory.api.handler.impl.InventoryClickHandler;
import net.millida.inventory.api.handler.impl.InventoryDisplayableHandler;
import net.millida.inventory.api.item.InventoryClickItem;
import net.millida.inventory.api.item.InventorySelectItem;
import net.millida.util.ItemUtil;
import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import net.millida.inventory.api.InventoryMarkup;
import net.millida.inventory.api.handler.impl.InventoryUpdateHandler;
import net.millida.inventory.api.item.InventorySimpleItem;
import net.millida.inventory.api.update.InventoryUpdateTask;

import java.util.LinkedList;
import java.util.List;

@Getter
public abstract class SimplePaginatedCustomInventory implements CustomInventory {

    protected final int inventoryRows;
    protected final String inventoryTitle;

    protected int currentPage;

    protected LattyInventoryInfo inventoryInfo;
    protected org.bukkit.inventory.Inventory inventory;

    protected InventoryMarkup inventoryMarkup;
    protected final List<InventoryItem> pageButtonList = new LinkedList<>();

    protected final LattyInventorySettings inventorySettings = new LattyInventorySettings();


    public SimplePaginatedCustomInventory(int inventoryRows, @NonNull String inventoryTitle) {
        this.inventoryRows = inventoryRows;
        this.inventoryTitle = inventoryTitle;

        this.inventoryInfo = new LattyInventoryInfo(this, inventoryTitle, inventoryRows * 9, inventoryRows);

        createInventory();
    }

    private void createInventory() {
        this.inventory = Bukkit.createInventory(null, inventoryInfo.getInventorySize(), inventoryTitle);
    }


    protected void backwardPage(@NonNull Player player) {
        if (currentPage - 1 < 0) {
            throw new RuntimeException(String.format("Page cannot be < 0 (%s - 1 < 0)", currentPage));
        }

        this.currentPage--;

        //createInventory();
        //openInventory(player);
        updateInventory(player);
    }

    protected void forwardPage(@NonNull Player player, int allPagesCount) {
        if (currentPage >= allPagesCount) {
            throw new RuntimeException(String.format("Page cannot be >= max pages count (%s >= %s)", currentPage, allPagesCount));
        }

        this.currentPage++;

        //createInventory();
        //openInventory(player);
        updateInventory(player);
    }

    protected void drawPage(@NonNull Player player) {
        pageButtonList.clear();

        if (inventoryMarkup != null) {
            inventoryMarkup.getMarkupList().clear();
        }

        clearInventory();
        drawInventory(player);

        int pagesCount = pageButtonList.size() / inventoryMarkup.getMarkupList().size();

        if (!(currentPage >= pagesCount)) {
            drawItem(inventoryInfo.getInventorySize() - 3, ItemUtil.newBuilder(Material.ARROW)

                            .addItemFlag(ItemFlag.HIDE_POTION_EFFECTS)

                            .setName("§c➥")

                            .build(),

                    (lattyInventory, event) -> forwardPage(player, pagesCount));
        }

        if (!(currentPage - 1 < 0)) {
            drawItem(inventoryInfo.getInventorySize() - 5, ItemUtil.newBuilder(Material.ARROW)
                            .addItemFlag(ItemFlag.HIDE_POTION_EFFECTS)

                            .setName("§c⮨")

                            .build(),

                    (lattyInventory, event) -> backwardPage(player));
        }

        for (int i = 0; i < inventoryMarkup.getMarkupList().size(); i++) {
            int itemIndex = currentPage * inventoryMarkup.getMarkupList().size() + i;

            if (pageButtonList.size() > itemIndex) {
                int buttonSlot = inventoryMarkup.getMarkupList().get(i);

                InventoryItem inventoryItem = pageButtonList.get(itemIndex);
                inventoryItem.setSlot(buttonSlot - 1);

                drawItem(inventoryItem);
            }
        }
    }

    @Override
    public void openInventory(@NonNull Player player) {
        closeInventory(player);
        drawPage(player);

        for (InventoryItem inventoryItem : inventoryInfo.getInventoryItemMap().valueCollection()) {

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
        drawPage(player);

        for (InventoryItem inventoryItem : inventoryInfo.getInventoryItemMap().valueCollection()) {

            inventory.setItem(inventoryItem.getSlot(), inventoryItem.getItemStack());
        }
    }

    @Override
    public void updateInventory(@NonNull Player player, @NonNull InventoryUpdateHandler inventoryUpdateHandler) {
        updateInventory(player);

        inventoryUpdateHandler.onUpdate(this, player);
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


    public void addItemToMarkup(@NonNull InventoryItem inventoryItem) {
        pageButtonList.add(inventoryItem);
    }

    public void addItemToMarkup(@NonNull ItemStack itemStack) {
        addItemToMarkup(new InventorySimpleItem(0, itemStack));
    }

    public void addItemToMarkup(@NonNull ItemStack itemStack, @NonNull InventoryClickHandler inventoryClickHandler) {
        addItemToMarkup(new InventoryClickItem(0, itemStack, inventoryClickHandler));
    }

    public void addItemSelectToMarkup(@NonNull ItemStack itemStack, @NonNull InventoryClickHandler inventoryClickHandler, boolean isEnchanting) {
        addItemToMarkup(new InventorySelectItem(0, itemStack, inventoryClickHandler, isEnchanting, false));
    }

    public void addItemSelectToMarkup(@NonNull ItemStack itemStack, @NonNull InventoryClickHandler inventoryClickHandler) {
        addItemSelectToMarkup(itemStack, inventoryClickHandler, true);
    }


    @Override
    public abstract void drawInventory(@NonNull Player player);

}
