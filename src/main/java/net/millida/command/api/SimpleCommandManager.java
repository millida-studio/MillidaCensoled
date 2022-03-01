package net.millida.command.api;

import lombok.Getter;
import net.millida.CensurePlugin;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public final class SimpleCommandManager {

    @Getter
    private final List<SimpleCommand> commandList = new ArrayList<>();

    private static CommandMap commandMap;


    public void registerCommand(SimpleCommand simpleCommand) {
        registerCommand(CensurePlugin.INSTANCE, simpleCommand);
    }

    /**
     * Регистрация комманд при помощи org.bukkit.command.CommandMap
     *
     *  (Код старый, переписывать его было лень, так как он и так
     *   стабильно и правильно работает. Сделал его только чуток красивее)
     *
     * @param plugin - плагин, от имени котрого регистрируется команда
     * @param simpleCommand - команда
     */
    public void registerCommand(Plugin plugin, SimpleCommand simpleCommand) {
        commandList.add(simpleCommand);

        try {
            if (commandMap == null) {
                String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];

                Class<?> craftServerClass = Class.forName("org.bukkit.craftbukkit." + version + ".CraftServer");
                Object craftServerObject = craftServerClass.cast(Bukkit.getServer());
                Field commandMapField = craftServerClass.getDeclaredField("commandMap");

                commandMapField.setAccessible(true);

                commandMap = (SimpleCommandMap)commandMapField.get(craftServerObject);
            }

            commandMap.register(plugin.getName(), simpleCommand);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
