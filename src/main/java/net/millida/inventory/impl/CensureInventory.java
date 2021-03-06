package net.millida.inventory.impl;

import lombok.NonNull;
import net.millida.CensurePlugin;
import net.millida.inventory.api.impl.SimpleCustomInventory;
import net.millida.player.CensurePlayer;
import net.millida.storage.StorageManager;
import net.millida.util.ItemUtil;
import net.millida.util.MaterialsRegistry;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.LinkedList;
import java.util.List;

public class CensureInventory extends SimpleCustomInventory {

    public CensureInventory() {
        super(3, CensurePlugin.INSTANCE.getLangConfiguration().getString("Gui.Censure.title"));
    }

    @Override
    public void drawInventory(@NonNull Player player) {
        CensurePlayer censurePlayer = CensurePlayer.by(player);

        List<String> lore = new LinkedList<>();

        for (String arg : CensurePlugin.INSTANCE.getLangConfiguration().getStringList("Gui.Censure.CensureInfoItem.lore")) {
            lore.add(arg.replace("{status}", censurePlayer.isEnableCensure() ? "§a✓" : "§c✖"));
        }

        drawItem(CensurePlugin.INSTANCE.getLangConfiguration().getInt("Gui.Censure.CensureInfoItem.slot"),
                ItemUtil.newBuilder(MaterialsRegistry.matchMaterial(CensurePlugin.INSTANCE.getLangConfiguration().getString("Gui.Censure.CensureInfoItem.material").split(":")[0]))
                        .setDurability(CensurePlugin.INSTANCE.getLangConfiguration().getInt("Gui.Censure.CensureInfoItem.data", 0))

                        .setName(CensurePlugin.INSTANCE.getLangConfiguration().getString("Gui.Censure.CensureInfoItem.name"))
                        .setLore(lore)

                        .build(), (customInventory, inventoryClickEvent) -> {
                    if (!player.hasPermission("censure.toggle")) {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', CensurePlugin.INSTANCE.getLangConfiguration().getString("NoPermMessage")));

                        return;
                    }

                    censurePlayer.setEnableCensure(!censurePlayer.isEnableCensure());
                    StorageManager.INSTANCE.savePlayer(censurePlayer);

                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', CensurePlugin.INSTANCE.getLangConfiguration().getString("ToggleMessage", "§fCensure: {status}")
                            .replace("{status}", censurePlayer.isEnableCensure() ? "§a✓" : "§c✖")));
                    player.closeInventory();
                });

        drawItem(CensurePlugin.INSTANCE.getLangConfiguration().getInt("Gui.Censure.CensureWordListItem.slot"),
                ItemUtil.newBuilder(MaterialsRegistry.matchMaterial(CensurePlugin.INSTANCE.getLangConfiguration().getString("Gui.Censure.CensureWordListItem.material")))
                        .setDurability(CensurePlugin.INSTANCE.getLangConfiguration().getInt("Gui.Censure.CensureWordListItem.data", 0))

                        .setName(CensurePlugin.INSTANCE.getLangConfiguration().getString("Gui.Censure.CensureWordListItem.name"))
                        .setLore(CensurePlugin.INSTANCE.getLangConfiguration().getStringList("Gui.Censure.CensureWordListItem.lore"))

                        .build(), (customInventory, inventoryClickEvent) -> {
                    new WordListInventory().openInventory(player);
                });
    }
}
