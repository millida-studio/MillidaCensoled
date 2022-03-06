package net.millida.inventory.api;

import net.millida.CensurePlugin;
import net.millida.inventory.api.handler.impl.InventoryClickHandler;
import net.millida.inventory.api.handler.impl.InventoryDisplayableHandler;
import net.millida.inventory.api.item.InventoryClickItem;
import net.millida.inventory.api.item.InventorySelectItem;
import net.millida.util.ItemUtil;
import net.millida.util.WeakObjectCache;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;

public class InventoryListener implements Listener {

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent inventoryOpenEvent) {
        Player player = (Player) inventoryOpenEvent.getPlayer();
        CustomInventory customInventory = CensurePlugin.INSTANCE.getInventoryManager().getPlayerInventory(player);

        if (customInventory == null) {
            return;
        }

        WeakObjectCache weakObjectCache = WeakObjectCache.create();

        weakObjectCache.addObject("player", player);
        weakObjectCache.addObject("isOpen", true);
        weakObjectCache.addObject("event", inventoryOpenEvent);

        customInventory.getInventoryInfo().handleHandlers(InventoryDisplayableHandler.class, weakObjectCache);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent inventoryCloseEvent) {
        Player player = (Player) inventoryCloseEvent.getPlayer();
        CustomInventory customInventory = CensurePlugin.INSTANCE.getInventoryManager().getPlayerInventory(player);

        if (customInventory == null) {
            return;
        }

        CensurePlugin.INSTANCE.getInventoryManager().removeInventory(player, customInventory);
        CensurePlugin.INSTANCE.getInventoryManager().removeInventoryUpdateTask(customInventory);

        WeakObjectCache weakObjectCache = WeakObjectCache.create();

        weakObjectCache.addObject("player", player);
        weakObjectCache.addObject("isOpen", false);
        weakObjectCache.addObject("event", inventoryCloseEvent);

        customInventory.getInventoryInfo().handleHandlers(InventoryDisplayableHandler.class, weakObjectCache);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent inventoryClickEvent) {
        Player player = (Player) inventoryClickEvent.getWhoClicked();
        CustomInventory customInventory = CensurePlugin.INSTANCE.getInventoryManager().getPlayerInventory(player);

        if (customInventory == null) {
            return;
        }

        int itemSlot = (inventoryClickEvent.getSlot() - 1);
        InventoryItem inventoryItem = customInventory.getInventoryInfo().getItem(itemSlot);

        if (inventoryClickEvent.getClickedInventory() instanceof PlayerInventory && customInventory.getInventorySettings().isUseOnlyCacheItems()) {
            return;
        }

        if (inventoryItem == null) {
            inventoryClickEvent.setCancelled(!customInventory.getInventorySettings().isUseOnlyCacheItems());

        } else {

            inventoryClickEvent.setCancelled(true);
        }

        if (inventoryItem instanceof InventoryClickItem) {
            ((InventoryClickItem) inventoryItem).getInventoryClickHandler().onClick(customInventory, inventoryClickEvent);
        }

        if (inventoryItem instanceof InventorySelectItem) {
            InventorySelectItem inventorySelectItem = ((InventorySelectItem) inventoryItem);

            inventorySelectItem.setSelected(!inventorySelectItem.isSelected());
            inventorySelectItem.getInventoryClickHandler().onClick(customInventory, inventoryClickEvent);

            if (inventorySelectItem.isEnchanting()) {
                ItemStack itemStack = ItemUtil.newBuilder(inventoryClickEvent.getCurrentItem())
                        .addEnchantment(inventorySelectItem.isSelected() ? Enchantment.ARROW_DAMAGE : null, 1)
                        .build();

                inventoryClickEvent.setCurrentItem(itemStack);
            }
        }

        if (!inventoryClickEvent.isRightClick()) player.playSound(player.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1, 1);

        WeakObjectCache weakObjectCache = WeakObjectCache.create();
        weakObjectCache.addObject("slot", itemSlot);
        weakObjectCache.addObject("player", player);
        weakObjectCache.addObject("event", inventoryClickEvent);

        customInventory.getInventoryInfo().handleHandlers(InventoryClickHandler.class, weakObjectCache);
    }

}
