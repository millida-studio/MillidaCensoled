package net.millida.command;

import net.millida.CensurePlugin;
import net.millida.command.api.SimpleCommand;
import net.millida.inventory.impl.CensureInventory;
import net.millida.player.CensurePlayer;
import net.millida.storage.StorageManager;
import net.millida.storage.StorageType;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CensureCommand extends SimpleCommand {

    public CensureCommand() {
        super("censure", "цензура");

        setOnlyPlayers(true);
    }

    @Override
    public void execute(CommandSender commandSender, String... args) {
        Player player = (Player) commandSender;
        CensurePlayer censurePlayer = CensurePlayer.by(player);

        if (args.length == 0) {
            if (!commandSender.hasPermission("censure.inventory")) {
                commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', CensurePlugin.INSTANCE.getLangConfiguration().getString("NoPermMessage")));

                return;
            }

            new CensureInventory().openInventory(player);

            return;
        }

        switch (args[0].toLowerCase()) {
            case "add": {
                if (!commandSender.hasPermission("censure.add")) {
                    commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', CensurePlugin.INSTANCE.getLangConfiguration().getString("NoPermMessage")));

                    return;
                }

                if (args.length < 2) {
                    if (!commandSender.hasPermission("censure.inventory")) {
                        commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', CensurePlugin.INSTANCE.getLangConfiguration().getString("NoPermMessage")));

                        return;
                    }

                    new CensureInventory().openInventory(player);
                    return;
                }

                String word = args[1].toLowerCase();

                if (censurePlayer.getCensureWordsList().contains(word)) {
                    commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', CensurePlugin.INSTANCE.getLangConfiguration().getString("WordAlredyCensured")));
                    return;
                }

                if (word.length() < CensurePlugin.INSTANCE.getConfig().getInt("MinWordLenght") || word.length() > CensurePlugin.INSTANCE.getConfig().getInt("MaxWordLenght")) {
                    commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', CensurePlugin.INSTANCE.getLangConfiguration().getString("WordLenghtLimit")));
                    return;
                }

                commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', CensurePlugin.INSTANCE.getLangConfiguration().getString("AddedMessage"))
                        .replace("{word}", word));
                censurePlayer.addCensure(word);

                break;
            }

            case "remove": {
                if (!commandSender.hasPermission("censure.remove")) {
                    commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', CensurePlugin.INSTANCE.getLangConfiguration().getString("NoPermMessage")));

                    return;
                }

                if (args.length < 2) {
                    if (!commandSender.hasPermission("censure.inventory")) {
                        commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', CensurePlugin.INSTANCE.getLangConfiguration().getString("NoPermMessage")));

                        return;
                    }

                    new CensureInventory().openInventory(player);
                    return;
                }

                String word = args[1].toLowerCase();

                if (!censurePlayer.getCensureWordsList().contains(word)) {
                    commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', CensurePlugin.INSTANCE.getLangConfiguration().getString("WordNotCensured")));
                    return;
                }

                censurePlayer.removeCensure(word);
                commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', CensurePlugin.INSTANCE.getLangConfiguration().getString("RemovedMessage"))
                        .replace("{word}", word));

                break;
            }

            case "toggle": {
                if (!commandSender.hasPermission("censure.toggle")) {
                    commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', CensurePlugin.INSTANCE.getLangConfiguration().getString("NoPermMessage")));
                    return;
                }

                censurePlayer.setEnableCensure(!censurePlayer.isEnableCensure());
                StorageManager.INSTANCE.savePlayer(censurePlayer);

                commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', CensurePlugin.INSTANCE.getLangConfiguration().getString("ToggleMessage")
                        .replace("{status}", censurePlayer.isEnableCensure() ? "§a✓" : "§c✖")));
                return;
            }

            case "reload": {
                if (!commandSender.hasPermission("censure.reload")) {
                    commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', CensurePlugin.INSTANCE.getLangConfiguration().getString("NoPermMessage")));
                    return;
                }

                CensurePlugin.INSTANCE.reloadConfig();
                CensurePlugin.INSTANCE.loadLangConfiguration();

                StorageType storageType = StorageType.valueOf(CensurePlugin.INSTANCE.getConfig().getString("StorageType").toUpperCase().replace("YML", "LOCAL"));

                if (storageType == null) {
                    Bukkit.getLogger().info(ChatColor.RED + "Storage type " + CensurePlugin.INSTANCE.getConfig().getString("StorageType") + " not found!");

                    storageType = StorageType.LOCAL;
                }

                StorageManager.INSTANCE.setStorageType(storageType);
                StorageManager.INSTANCE.init(CensurePlugin.INSTANCE.getConfig());

                player.sendMessage(ChatColor.GOLD + "Plugin reloaded!");

                break;
            }

            default: {
                if (!commandSender.hasPermission("censure.inventory")) {
                    commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', CensurePlugin.INSTANCE.getLangConfiguration().getString("NoPermMessage")));

                    return;
                }

                new CensureInventory().openInventory(player);
            }
        }
    }
}
