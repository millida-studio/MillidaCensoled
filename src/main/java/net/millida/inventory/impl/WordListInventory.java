package net.millida.inventory.impl;

import lombok.NonNull;
import net.millida.CensurePlugin;
import net.millida.inventory.api.impl.SimplePaginatedCustomInventory;
import net.millida.inventory.api.markup.InventoryBlockMarkup;
import net.millida.player.CensurePlayer;
import net.millida.util.ItemUtil;
import net.millida.util.MaterialsRegistry;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

public class WordListInventory extends SimplePaginatedCustomInventory {

    public WordListInventory() {
        super(6, CensurePlugin.INSTANCE.getLangConfiguration().getString("Gui.Word.title"));
    }

    @Override
    public void drawInventory(@NonNull Player player) {
        setItemMarkup(new InventoryBlockMarkup(6));
        Material material = MaterialsRegistry.matchMaterial(CensurePlugin.INSTANCE.getLangConfiguration().getString("Gui.Word.material"));

        CensurePlayer censurePlayer = CensurePlayer.by(player);

        for (String word : censurePlayer.getCensureWordsList()) {
            addItemToMarkup(ItemUtil.newBuilder(material)
                    .setName(CensurePlugin.INSTANCE.getLangConfiguration().getString("Gui.Word.name")
                            .replace("{word}", word))

                    .setLore(CensurePlugin.INSTANCE.getLangConfiguration().getStringList("Gui.Word.lore"))

                    .build(), (customInventory, inventoryClickEvent) -> {
                if (inventoryClickEvent.isLeftClick()) {
                    return;
                }

                if (!player.hasPermission("censure.remove")) {
                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', CensurePlugin.INSTANCE.getLangConfiguration().getString("NoPermMessage")));

                    return;
                }

                censurePlayer.removeCensure(word);
                player.sendMessage(ChatColor.translateAlternateColorCodes('&', CensurePlugin.INSTANCE.getLangConfiguration().getString("RemovedMessage"))
                        .replace("{word}", word));

                updateInventory(player);
            });
        }

        drawItem(50, ItemUtil.newBuilder(MaterialsRegistry.matchMaterial(CensurePlugin.INSTANCE.getLangConfiguration().getString("Gui.Word.AddWordItem.material")))

                .setDurability(CensurePlugin.INSTANCE.getLangConfiguration().getInt("Gui.Word.AddWordItem.data", 0))
                .setName(CensurePlugin.INSTANCE.getLangConfiguration().getString("Gui.Word.AddWordItem.name"))
                .setLore(CensurePlugin.INSTANCE.getLangConfiguration().getStringList("Gui.Word.AddWordItem.lore"))

                .build(), (customInventory, inventoryClickEvent) -> {
            if (player.hasMetadata("censure_add")) {
                return;
            }

            player.sendMessage(ChatColor.translateAlternateColorCodes('&', CensurePlugin.INSTANCE.getLangConfiguration().getString("AddWordMessage")));
            player.setMetadata("censure_add", new FixedMetadataValue(CensurePlugin.INSTANCE, ""));
            player.closeInventory();
        });

        if (censurePlayer.getCensureWordsList().isEmpty()) {
            drawItem(23, ItemUtil.newBuilder(MaterialsRegistry.matchMaterial(CensurePlugin.INSTANCE.getLangConfiguration().getString("Gui.Word.EmptyListItem.material")))

                    .setName(CensurePlugin.INSTANCE.getLangConfiguration().getString("Gui.Word.EmptyListItem.name"))
                    .setLore(CensurePlugin.INSTANCE.getLangConfiguration().getStringList("Gui.Word.EmptyListItem.lore"))

                    .build());
        }

        drawItem(46, ItemUtil.newBuilder(MaterialsRegistry.matchMaterial(CensurePlugin.INSTANCE.getLangConfiguration().getString("Gui.ExitItem.material")))

                .setName(CensurePlugin.INSTANCE.getLangConfiguration().getString("Gui.ExitItem.name"))

                .build(), (customInventory, inventoryClickEvent) -> {
            new CensureInventory().openInventory(player);
        });
    }
}
