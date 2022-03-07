package net.millida.util;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.utility.MinecraftProtocolVersion;
import com.comphenix.protocol.utility.MinecraftVersion;
import org.bukkit.Material;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

public final class MaterialsRegistry {

	private static final boolean USE_NEW_MATERIAL_NAMES = MinecraftProtocolVersion.getVersion(ProtocolLibrary.getProtocolManager().getMinecraftVersion())
			>= MinecraftProtocolVersion.getVersion(MinecraftVersion.AQUATIC_UPDATE);

	private static final char[] IGNORE_CHARS = {'-', '_', ' '};

	private static final Map<String, Material> MATERIALS_BY_ALIAS = new HashMap<>();

	private static final Collection<Material> AIR_MATERIALS = getExistingMaterials("AIR", "CAVE_AIR", "VOID_AIR");
	private static final Collection<Material> SIGN_MATERIALS = getExistingMaterials("SIGN", "SIGN_POST", "WALL_SIGN");

	private static void addMaterialAlias(String name, Material material) {
		MATERIALS_BY_ALIAS.put(StringUtils.stripChars(name, IGNORE_CHARS).toLowerCase(), material);
	}

	private static void tryAddMaterialAlias(String name, String materialEnumName) {
		try {
			addMaterialAlias(name, Material.valueOf(materialEnumName));
		} catch (IllegalArgumentException e) {
		}
	}

	public static Material matchMaterial(String alias) {
		if (alias == null) {
			return null;
		}

		return MATERIALS_BY_ALIAS.get(StringUtils.stripChars(alias, IGNORE_CHARS).toLowerCase());
	}

	public static String formatMaterial(Material material) {
		return StringUtils.capitalizeFully(material.toString().replace("_", " "));
	}

	public static Collection<Material> getExistingMaterials(String... materialEnumNames) {
		Collection<Material> existingMaterials = new HashSet<>();

		for (String materialEnumName : materialEnumNames) {
			try {
				existingMaterials.add(Material.valueOf(materialEnumName));
			} catch (IllegalArgumentException e) {
			}
		}

		return existingMaterials;
	}

	public static boolean isAir(Material material) {
		return AIR_MATERIALS.contains(material);
	}

	public static boolean isSign(Material material) {
		return SIGN_MATERIALS.contains(material);
	}

	public static boolean useNewMaterialNames() {
		return USE_NEW_MATERIAL_NAMES;
	}

	static {
		for (Material material : Material.values()) {
			addMaterialAlias(material.name(), material);

			if (!useNewMaterialNames()) {
				addMaterialAlias(String.valueOf(material.getId()), material);
			}
		}

		if (!useNewMaterialNames()) {
			tryAddMaterialAlias("WRITABLE_BOOK", "BOOK_AND_QUILL");
			tryAddMaterialAlias("EXPERIENCE_BOTTLE", "EXP_BOTTLE");
		} else {
			addMaterialAlias("WRITABLE_BOOK", Material.WRITABLE_BOOK);
			addMaterialAlias("EXPERIENCE_BOTTLE", Material.EXPERIENCE_BOTTLE);
		}

		tryAddMaterialAlias("iron bar", "IRON_FENCE");
		tryAddMaterialAlias("iron bars", "IRON_FENCE");
		tryAddMaterialAlias("glass pane", "THIN_GLASS");
		tryAddMaterialAlias("nether wart", "NETHER_STALK");
		tryAddMaterialAlias("nether warts", "NETHER_STALK");
		tryAddMaterialAlias("slab", "STEP");
		tryAddMaterialAlias("double slab", "DOUBLE_STEP");
		tryAddMaterialAlias("stone brick", "SMOOTH_BRICK");
		tryAddMaterialAlias("stone bricks", "SMOOTH_BRICK");
		tryAddMaterialAlias("stone stair", "SMOOTH_STAIRS");
		tryAddMaterialAlias("stone stairs", "SMOOTH_STAIRS");
		tryAddMaterialAlias("potato", "POTATO_ITEM");
		tryAddMaterialAlias("carrot", "CARROT_ITEM");
		tryAddMaterialAlias("brewing stand", "BREWING_STAND_ITEM");
		tryAddMaterialAlias("cauldron", "CAULDRON_ITEM");
		tryAddMaterialAlias("carrot on stick", "CARROT_STICK");
		tryAddMaterialAlias("carrot on a stick", "CARROT_STICK");
		tryAddMaterialAlias("cobblestone wall", "COBBLE_WALL");
		tryAddMaterialAlias("acacia wood stairs", "ACACIA_STAIRS");
		tryAddMaterialAlias("dark oak wood stairs", "DARK_OAK_STAIRS");
		tryAddMaterialAlias("wood slab", "WOOD_STEP");
		tryAddMaterialAlias("double wood slab", "WOOD_DOUBLE_STEP");
		tryAddMaterialAlias("repeater", "DIODE");
		tryAddMaterialAlias("piston", "PISTON_BASE");
		tryAddMaterialAlias("sticky piston", "PISTON_STICKY_BASE");
		tryAddMaterialAlias("flower pot", "FLOWER_POT_ITEM");
		tryAddMaterialAlias("wood showel", "WOOD_SPADE");
		tryAddMaterialAlias("stone showel", "STONE_SPADE");
		tryAddMaterialAlias("gold showel", "GOLD_SPADE");
		tryAddMaterialAlias("iron showel", "IRON_SPADE");
		tryAddMaterialAlias("diamond showel", "DIAMOND_SPADE");
		tryAddMaterialAlias("steak", "COOKED_BEEF");
		tryAddMaterialAlias("cooked porkchop", "GRILLED_PORK");
		tryAddMaterialAlias("raw porkchop", "PORK");
		tryAddMaterialAlias("hardened clay", "HARD_CLAY");
		tryAddMaterialAlias("huge brown mushroom", "HUGE_MUSHROOM_1");
		tryAddMaterialAlias("huge red mushroom", "HUGE_MUSHROOM_2");
		tryAddMaterialAlias("mycelium", "MYCEL");
		tryAddMaterialAlias("poppy", "RED_ROSE");
		tryAddMaterialAlias("comparator", "REDSTONE_COMPARATOR");
		tryAddMaterialAlias("skull", "SKULL_ITEM");
		tryAddMaterialAlias("head", "SKULL_ITEM");
		tryAddMaterialAlias("redstone torch", "REDSTONE_TORCH_ON");
		tryAddMaterialAlias("redstone lamp", "REDSTONE_LAMP_OFF");
		tryAddMaterialAlias("glistering melon", "SPECKLED_MELON");
		tryAddMaterialAlias("acacia leaves", "LEAVES_2");
		tryAddMaterialAlias("acacia log", "LOG_2");
		tryAddMaterialAlias("gunpowder", "SULPHUR");
		tryAddMaterialAlias("lilypad", "WATER_LILY");
		tryAddMaterialAlias("command block", "COMMAND");
		tryAddMaterialAlias("dye", "INK_SACK");
	}

}
