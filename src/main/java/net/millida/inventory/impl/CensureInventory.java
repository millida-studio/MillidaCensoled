package net.millida.inventory.impl;

import lombok.NonNull;
import net.millida.CensurePlugin;
import net.millida.inventory.api.impl.SimpleCustomInventory;
import net.millida.player.CensurePlayer;
import net.millida.storage.StorageManager;
import net.millida.util.ItemUtil;
import net.millida.util.MaterialsRegistry;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.LinkedList;
import java.util.List;

public class CensureInventory extends SimpleCustomInventory {

    public CensureInventory() {
        super(3, CensurePlugin.INSTANCE.getConfig().getString("Gui.Censure.title"));
    }

    @Override
    public void drawInventory(@NonNull Player player) {
        CensurePlayer censurePlayer = CensurePlayer.by(player);

        List<String> lore = new LinkedList<>();

        for (String arg : CensurePlugin.INSTANCE.getConfig().getStringList("Gui.Censure.CensureInfoItem.lore")) {
            lore.add(arg.replace("{status}", censurePlayer.isEnableCensure() ? "§a✓" : "§c✖"));
        }

        drawItem(CensurePlugin.INSTANCE.getConfig().getInt("Gui.Censure.CensureInfoItem.slot"),
                ItemUtil.newBuilder(MaterialsRegistry.matchMaterial(CensurePlugin.INSTANCE.getConfig().getString("Gui.Censure.CensureInfoItem.material").split(":")[0]))
                        .setDurability(CensurePlugin.INSTANCE.getConfig().getInt("Gui.Censure.CensureInfoItem.data", 0))

                        .setName(CensurePlugin.INSTANCE.getConfig().getString("Gui.Censure.CensureInfoItem.name"))
                        .setLore(lore)

                        .build(), (customInventory, inventoryClickEvent) -> {
                    if (!player.hasPermission("censure.toggle")) {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', CensurePlugin.INSTANCE.getConfig().getString("NoPermMessage")));

                        return;
                    }

                    censurePlayer.setEnableCensure(!censurePlayer.isEnableCensure());
                    StorageManager.INSTANCE.savePlayer(censurePlayer);

                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', CensurePlugin.INSTANCE.getConfig().getString("ToggleMessage")
                            .replace("{status}", censurePlayer.isEnableCensure() ? "§a✓" : "§c✖")));
                    player.closeInventory();
                });

        drawItem(CensurePlugin.INSTANCE.getConfig().getInt("Gui.Censure.CensureWordListItem.slot"),
                ItemUtil.newBuilder(MaterialsRegistry.matchMaterial(CensurePlugin.INSTANCE.getConfig().getString("Gui.Censure.CensureWordListItem.material")))
                        .setDurability(CensurePlugin.INSTANCE.getConfig().getInt("Gui.Censure.CensureWordListItem.data", 0))

                        .setName(CensurePlugin.INSTANCE.getConfig().getString("Gui.Censure.CensureWordListItem.name"))
                        .setLore(CensurePlugin.INSTANCE.getConfig().getStringList("Gui.Censure.CensureWordListItem.lore"))

                        .build(), (customInventory, inventoryClickEvent) -> {
                    new WordListInventory().openInventory(player);
                });

        lore.clear();
        for (String arg : CensurePlugin.INSTANCE.getConfig().getStringList("Gui.Censure.MentionInfoItem.lore")) {
            lore.add(arg.replace("{status}", censurePlayer.isEnableMentions() ? "§a✓" : "§c✖"));
        }

        drawItem(CensurePlugin.INSTANCE.getConfig().getInt("Gui.Censure.MentionInfoItem.slot"),
                ItemUtil.newBuilder(MaterialsRegistry.matchMaterial(CensurePlugin.INSTANCE.getConfig().getString("Gui.Censure.MentionInfoItem.material")))
                        .setDurability(CensurePlugin.INSTANCE.getConfig().getInt("Gui.Censure.MentionInfoItem.data", 0))


                        .setName(CensurePlugin.INSTANCE.getConfig().getString("Gui.Censure.MentionInfoItem.name"))
                        .setLore(lore)

                        .build(), (customInventory, inventoryClickEvent) -> {
                    if (!player.hasPermission("mention.toggle")) {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', CensurePlugin.INSTANCE.getConfig().getString("NoPermMessage")));

                        return;
                    }

                    censurePlayer.setEnableMentions(!censurePlayer.isEnableMentions());
                    StorageManager.INSTANCE.savePlayer(censurePlayer);

                    player.sendMessage(ChatColor.translateAlternateColorCodes('&', CensurePlugin.INSTANCE.getConfig().getString("ToggleMentionMessage")
                            .replace("{status}", censurePlayer.isEnableMentions() ? "§a✓" : "§c✖")));
                    player.closeInventory();
                });

        drawItem(CensurePlugin.INSTANCE.getConfig().getInt("Gui.Censure.MentionSoundItem.slot"),
                ItemUtil.newBuilder(MaterialsRegistry.matchMaterial(CensurePlugin.INSTANCE.getConfig().getString("Gui.Censure.MentionSoundItem.material")))
                        .setDurability(CensurePlugin.INSTANCE.getConfig().getInt("Gui.Censure.MentionSoundItem.data", 0))

                        .setName(CensurePlugin.INSTANCE.getConfig().getString("Gui.Censure.MentionSoundItem.name"))
                        .setLore(CensurePlugin.INSTANCE.getConfig().getStringList("Gui.Censure.MentionSoundItem.lore"))

                        .build(), (customInventory, inventoryClickEvent) -> {
                    if (!player.hasPermission("censure.sound")) {
                        player.sendMessage(ChatColor.translateAlternateColorCodes('&', CensurePlugin.INSTANCE.getConfig().getString("NoPermMessage")));

                        return;
                    }

                    new MentionSoundInventory().openInventory(player);
                });
    }
}
