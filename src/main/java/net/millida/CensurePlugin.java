package net.millida;

import com.comphenix.protocol.ProtocolLibrary;
import lombok.Getter;
import lombok.SneakyThrows;
import net.millida.command.CensureCommand;
import net.millida.command.api.SimpleCommandManager;
import net.millida.inventory.api.InventoryListener;
import net.millida.listener.ChatListener;
import net.millida.storage.StorageManager;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import net.millida.inventory.api.InventoryManager;

import java.io.File;
import java.io.FileReader;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

public class CensurePlugin extends JavaPlugin {

    public static CensurePlugin INSTANCE; {
        INSTANCE = this;
    }

    @Getter private final InventoryManager inventoryManager             = new InventoryManager();
    protected final SimpleCommandManager commandManager                 = new SimpleCommandManager();

    @Getter
    protected FileConfiguration langConfiguration;
    private final File langFolder = new File(getDataFolder(), "lang");

    @Getter
    private final List<String> defaultCensuredWords = new LinkedList<>();

    private final static int PLUGIN_ID = 14575;


    @Override
    public void onEnable() {
        //Хуй его пойми что с этой хуйней делать, но и похуй
        Metrics metrics = new Metrics(this, PLUGIN_ID);

        saveDefaultConfig();

        saveLangFolder();

        saveResource("bad-words.txt", false);
        saveResource("lang" + File.separator + "lang_en.yml", false);
        saveResource("lang" + File.separator + "lang_ru.yml", false);

        loadBadWords();
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
        String lang = getConfig().getString("Lang", "en").toLowerCase();

        File langFile = new File(langFolder, "lang_" + lang + ".yml");
        if (!langFile.exists()) {
            Bukkit.getLogger().info(ChatColor.YELLOW + "Lang file lang_" + lang + ".yml does not exists! Set default English lang");
            langFile = new File(langFolder,"lang_en.yml");
        }

        this.langConfiguration = YamlConfiguration.loadConfiguration(langFile);
    }

    @SneakyThrows
    protected void loadBadWords() {
        FileReader badWordsFileReader = new FileReader(new File(getDataFolder(), "bad-words.txt"));
        Scanner scanner = new Scanner(badWordsFileReader);

        while (scanner.hasNextLine()) {
            String badWordLine = scanner.nextLine();

            defaultCensuredWords.addAll(Arrays.asList(badWordLine.toLowerCase().split(", ")));
        }

        badWordsFileReader.close();
    }
}
