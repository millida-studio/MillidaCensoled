package net.millida;

import com.comphenix.protocol.ProtocolLibrary;
import lombok.Getter;
import net.millida.command.CensureCommand;
import net.millida.command.api.SimpleCommandManager;
import net.millida.inventory.api.InventoryListener;
import net.millida.listener.ChatListener;
import net.millida.storage.StorageManager;
import org.bukkit.plugin.java.JavaPlugin;
import net.millida.inventory.api.InventoryManager;

public class CensurePlugin extends JavaPlugin {

    public static CensurePlugin INSTANCE; {
        INSTANCE = this;
    }

    @Getter private final InventoryManager inventoryManager             = new InventoryManager();
    protected final SimpleCommandManager commandManager                 = new SimpleCommandManager();

    @Override
    public void onEnable() {
        saveDefaultConfig();

        StorageManager.INSTANCE.init(getConfig());

        commandManager.registerCommand(new CensureCommand());
        registerListener();
    }

    protected void registerListener() {
        ChatListener chatListener = new ChatListener();

        ProtocolLibrary.getProtocolManager().addPacketListener(chatListener);
        getServer().getPluginManager().registerEvents(chatListener, this);
        getServer().getPluginManager().registerEvents(new InventoryListener(), this);
    }
}
