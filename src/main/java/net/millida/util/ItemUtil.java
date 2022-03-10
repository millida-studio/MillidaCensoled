package net.millida.util;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.experimental.UtilityClass;
import org.bukkit.Color;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.LeatherArmorMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.material.MaterialData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@UtilityClass
public class ItemUtil {

    public final Material EMPTY_ITEM_TYPE       = Material.AIR;


    public static ItemStack getNamedItemStack(@NonNull Material material, int durability,
                                       @NonNull String displayName) {

        return newBuilder(material)

                .setDurability(durability)
                .setName(displayName)
                .build();
    }

    public static ItemStack getNamedItemStack(@NonNull ItemStack itemStack,
                                       @NonNull String displayName) {

        return newBuilder(itemStack)
                .setName(displayName)

                .build();
    }

    public static ItemStack getNamedItemStack(@NonNull MaterialData materialData,
                                       @NonNull String displayName) {

        return newBuilder(materialData.toItemStack(1))
                .setName(displayName)

                .build();
    }


    public static ItemStack getPotion(@NonNull PotionEffect... potionEffects) {
        return getColouredPotion(Color.PURPLE, potionEffects);
    }

    public static ItemStack getColouredPotion(@NonNull Color potionColor,
                                       @NonNull PotionEffect... potionEffects) {

        ItemBuilder itemBuilder = newBuilder(Material.POTION);
        itemBuilder.setPotionColor(potionColor);

        for (PotionEffect potionEffect : potionEffects) {
            itemBuilder.addCustomPotionEffect(potionEffect, true);
        }

        return itemBuilder.build();
    }


    public static ItemBuilder newBuilder() {
        return newBuilder(EMPTY_ITEM_TYPE);
    }

    public static ItemBuilder newBuilder(@NonNull MaterialData materialData) {
        return new ItemBuilder(materialData.toItemStack(1));
    }

    public static ItemBuilder newBuilder(@NonNull Material material) {
        return new ItemBuilder(new ItemStack(material));
    }

    public static ItemBuilder newBuilder(@NonNull ItemStack itemStack) {
        return new ItemBuilder(itemStack.clone());
    }


    @RequiredArgsConstructor
    public class ItemBuilder {

        private final ItemStack itemStack;


        public ItemBuilder setDurability(int durability) {
            itemStack.setDurability((byte) durability);
            return this;
        }

        public ItemBuilder setUnbreakable(boolean flag) {
            ItemMeta itemMeta = itemStack.getItemMeta();

            itemMeta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE);

            itemStack.setItemMeta(itemMeta);
            return this;
        }

        public ItemBuilder setMaterial(Material material) {
            itemStack.setType(material);
            return this;
        }

        public ItemBuilder setAmount(int amount) {
            itemStack.setAmount(amount);
            return this;
        }

        public ItemBuilder setName(String name) {
            if (name == null) {
                return this;
            }

            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setDisplayName(name);

            itemStack.setItemMeta(itemMeta);
            return this;
        }

        public ItemBuilder setLore(String... loreArray) {
            if (loreArray == null) {
                return this;
            }

            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setLore(Arrays.asList(loreArray));

            itemStack.setItemMeta(itemMeta);
            return this;
        }

        public ItemBuilder setLore(List<String> loreList) {
            if (loreList == null) {
                return this;
            }

            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.setLore(loreList);

            itemStack.setItemMeta(itemMeta);
            return this;
        }

        public ItemBuilder addLore(String lore) {
            ItemMeta itemMeta = itemStack.getItemMeta();

            List<String> loreList = itemMeta.getLore();

            if (loreList == null) {
                loreList = new ArrayList<>();
            }

            loreList.add(lore);
            return setLore(loreList);
        }

        public ItemBuilder setGlowing(boolean glowing) {
            Enchantment glowEnchantment = Enchantment.ARROW_DAMAGE;

            if (glowing) {
                addItemFlag(ItemFlag.HIDE_ENCHANTS);
                addEnchantment(glowEnchantment, 1);
            } else {
                removeItemFlag(ItemFlag.HIDE_ENCHANTS);
                removeEnchantment(glowEnchantment);
            }

            return this;
        }

        public ItemBuilder addEnchantment(Enchantment enchantment, int level) {
            if (enchantment == null) {
                return this;
            }

            ItemMeta itemMeta = itemStack.getItemMeta();

            if (itemMeta == null) {
                return this;
            }

            itemMeta.addEnchant(enchantment, level, true);

            itemStack.setItemMeta(itemMeta);
            return this;
        }

        public ItemBuilder removeEnchantment(Enchantment enchantment) {
            if (enchantment == null) {
                return this;
            }

            ItemMeta itemMeta = itemStack.getItemMeta();

            if (!itemMeta.hasEnchant(enchantment) || itemMeta.hasConflictingEnchant(enchantment)) {
                return this;
            }

            itemMeta.removeEnchant(enchantment);

            itemStack.setItemMeta(itemMeta);
            return this;
        }

        public ItemBuilder addCustomPotionEffect(PotionEffect potionEffect, boolean isAdd) {
            if (potionEffect == null) {
                return this;
            }

            PotionMeta potionMeta = (PotionMeta) itemStack.getItemMeta();
            potionMeta.addCustomEffect(potionEffect, isAdd);

            itemStack.setItemMeta(potionMeta);
            return this;
        }

        public ItemBuilder setMainPotionEffect(PotionEffectType potionEffectType) {
            if (potionEffectType == null) {
                return this;
            }

            PotionMeta potionMeta = (PotionMeta) itemStack.getItemMeta();
            potionMeta.setMainEffect(potionEffectType);

            itemStack.setItemMeta(potionMeta);
            return this;
        }

        public ItemBuilder setPotionColor(Color color) {
            if (color == null) {
                return this;
            }

            PotionMeta potionMeta = (PotionMeta) itemStack.getItemMeta();
            potionMeta.setColor(color);

            itemStack.setItemMeta(potionMeta);
            return this;
        }

        public ItemBuilder setPlayerSkull(String playerSkull) {
            if (playerSkull == null) {
                return this;
            }

            SkullMeta skullMeta = (SkullMeta) itemStack.getItemMeta();
            skullMeta.setOwner(playerSkull);

            itemStack.setItemMeta(skullMeta);
            return this;
        }

        public ItemBuilder setLeatherColor(Color color) {
            if (color == null) {
                return this;
            }

            LeatherArmorMeta leatherArmorMeta = (LeatherArmorMeta) itemStack.getItemMeta();
            leatherArmorMeta.setColor(color);

            itemStack.setItemMeta(leatherArmorMeta);
            return this;
        }

        public ItemBuilder addItemFlag(ItemFlag itemFlag) {
            if (itemFlag == null) {
                return this;
            }

            ItemMeta itemMeta = itemStack.getItemMeta();
            itemMeta.addItemFlags(itemFlag);

            itemStack.setItemMeta(itemMeta);
            return this;
        }

        public ItemBuilder removeItemFlag(ItemFlag itemFlag) {
            if (itemFlag == null) {
                return this;
            }

            ItemMeta itemMeta = itemStack.getItemMeta();

            if (!itemMeta.hasItemFlag(itemFlag)) {
                return this;
            }

            itemMeta.removeItemFlags(itemFlag);

            itemStack.setItemMeta(itemMeta);
            return this;
        }

        public ItemStack build() {
            return itemStack;
        }
    }

}
