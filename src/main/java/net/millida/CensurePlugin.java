package net.millida;

import com.comphenix.protocol.ProtocolLibrary;
import net.millida.command.CensureCommand;
import net.millida.command.api.SimpleCommandManager;
import net.millida.listener.ChatListener;
import net.millida.storage.StorageManager;
import org.bukkit.plugin.java.JavaPlugin;

public class CensurePlugin extends JavaPlugin {

    public static CensurePlugin INSTANCE; {
        INSTANCE = this;
    }

    protected final SimpleCommandManager commandManager = new SimpleCommandManager();

    @Override
    public void onEnable() {
        saveDefaultConfig();

        StorageManager.INSTANCE.init(getConfig());

        commandManager.registerCommand(new CensureCommand());
        ProtocolLibrary.getProtocolManager().addPacketListener(new ChatListener());
    }
}
