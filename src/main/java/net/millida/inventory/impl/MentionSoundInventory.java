package net.millida.inventory.impl;

import lombok.NonNull;
import net.millida.CensurePlugin;
import net.millida.inventory.api.impl.SimplePaginatedCustomInventory;
import net.millida.inventory.api.markup.InventoryBlockMarkup;
import net.millida.player.CensurePlayer;
import net.millida.util.ItemUtil;
import net.millida.util.MaterialsRegistry;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class MentionSoundInventory extends SimplePaginatedCustomInventory {

    public MentionSoundInventory() {
        super(6, CensurePlugin.INSTANCE.getLangConfiguration().getString("Gui.Sound.title"));
    }

    @Override
    public void drawInventory(@NonNull Player player) {
        setItemMarkup(new InventoryBlockMarkup(6));
        Material material = MaterialsRegistry.matchMaterial(CensurePlugin.INSTANCE.getLangConfiguration().getString("Gui.Sound.material"));

        CensurePlayer censurePlayer = CensurePlayer.by(player);

        for (String soundName : CensurePlugin.INSTANCE.getLangConfiguration().getStringList("MentionsSounds")) {
            Sound sound = Sound.valueOf(soundName.split(";")[0]);
            String soundColoredName = soundName.split(";")[1];

            if (sound == null) {
                Bukkit.getLogger().info(soundName + " ISN'T EXISTS");
                continue;
            }

            addItemToMarkup(ItemUtil.newBuilder(material)
                    .setName(soundColoredName)

                    .setGlowing(censurePlayer.getMentionsSound().equals(sound))

                    .setLore(!censurePlayer.getMentionsSound().equals(sound)
                            ? CensurePlugin.INSTANCE.getLangConfiguration().getStringList("Gui.Sound.disabled-lore")
                            : CensurePlugin.INSTANCE.getLangConfiguration().getStringList("Gui.Sound.enabled-lore"))

                    .build(), (customInventory, inventoryClickEvent) -> {
                if (inventoryClickEvent.isRightClick()) {
                    player.playSound(player.getLocation(), sound, 1, 1);
                    return;
                }

                if (censurePlayer.getMentionsSound().equals(sound)) {
                    return;
                }

                censurePlayer.setMentionsSound(sound);
                updateInventory(player);
            });
        }

        drawItem(46, ItemUtil.newBuilder(MaterialsRegistry.matchMaterial(CensurePlugin.INSTANCE.getLangConfiguration().getString("Gui.ExitItem.material")))

                .setName(CensurePlugin.INSTANCE.getLangConfiguration().getString("Gui.ExitItem.name"))

                .build(), (customInventory, inventoryClickEvent) -> {
            new CensureInventory().openInventory(player);
        });
    }
}
