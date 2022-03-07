package net.millida;

import com.comphenix.protocol.ProtocolLibrary;
import lombok.Getter;
import net.millida.command.CensureCommand;
import net.millida.command.api.SimpleCommandManager;
import net.millida.inventory.api.InventoryListener;
import net.millida.listener.ChatListener;
import net.millida.storage.StorageManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import net.millida.inventory.api.InventoryManager;

import java.io.File;

public class CensurePlugin extends JavaPlugin {

    public static CensurePlugin INSTANCE; {
        INSTANCE = this;
    }

    @Getter private final InventoryManager inventoryManager             = new InventoryManager();
    protected final SimpleCommandManager commandManager                 = new SimpleCommandManager();

    @Getter
    protected FileConfiguration langConfiguration;
    private final File langFolder = new File(getDataFolder(), "lang");


    @Override
    public void onEnable() {
        saveDefaultConfig();

        saveLangFolder();

        saveResource("lang" + File.separator + "eng.yml", false);
        saveResource("lang" + File.separator + "ru.yml", false);

        loadLangConfiguration();


        StorageManager.INSTANCE.init(getConfig());

        commandManager.registerCommand(new CensureCommand());
        registerListener();
    }

    private void saveLangFolder() {
        if (langFolder.exists()) {
            return;
        }

        langFolder.mkdir();
    }

    protected void registerListener() {
        ChatListener chatListener = new ChatListener();

        ProtocolLibrary.getProtocolManager().addPacketListener(chatListener);
        getServer().getPluginManager().registerEvents(chatListener, this);
        getServer().getPluginManager().registerEvents(new InventoryListener(), this);
    }

    protected void loadLangConfiguration() {
        String lang = getConfig().getString("Lang", "eng").toLowerCase();

        File langFile = new File(langFolder, lang + ".yml");
        if (!langFile.exists()) {
            Bukkit.getLogger().info(ChatColor.YELLOW + "Lang file " + lang + ".yml does not exists! Set default ENG lang");
            langFile = new File(langFolder,"eng.yml");
        }

        this.langConfiguration = YamlConfiguration.loadConfiguration(langFile);
    }
}
