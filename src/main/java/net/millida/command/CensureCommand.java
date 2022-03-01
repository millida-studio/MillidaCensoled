package net.millida.command;

import net.millida.CensurePlugin;
import net.millida.command.api.SimpleCommand;
import net.millida.player.CensurePlayer;
import net.millida.storage.StorageManager;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

//Меня так напрягает Баккит Апи комманд, так что да, я для одной команды юзаю апи, хули
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
            if (!commandSender.hasPermission("censure.toggle")) {
                commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', CensurePlugin.INSTANCE.getConfig().getString("NoPermMessage")));

                return;
            }

            censurePlayer.setEnableCensure(!censurePlayer.isEnableCensure());
            StorageManager.INSTANCE.savePlayer(censurePlayer);

            commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', CensurePlugin.INSTANCE.getConfig().getString("ToggleMessage")
                    .replace("{action}", censurePlayer.isEnableCensure() ? "&aВключена" : "&cВыключена")));

            return;
        }

        switch (args[0].toLowerCase()) {

            case "add": {
                if (!commandSender.hasPermission("censure.add")) {
                    commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', CensurePlugin.INSTANCE.getConfig().getString("NoPermMessage")));

                    return;
                }

                if (args.length < 2) {
                    commandSender.sendMessage(ChatColor.RED + "Ошибка, пишите /censure add <слово>");
                    return;
                }

                String word = args[1].toLowerCase();

                if (censurePlayer.getCensureWordsList().contains(word)) {
                    commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', CensurePlugin.INSTANCE.getConfig().getString("WordAlredyCensured")));
                    return;
                }

                if (word.length() < CensurePlugin.INSTANCE.getConfig().getInt("MinWordLenght") || word.length() > CensurePlugin.INSTANCE.getConfig().getInt("MaxWordLenght")) {
                    commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', CensurePlugin.INSTANCE.getConfig().getString("WordLenghtLimit")));
                    return;
                }

                commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', CensurePlugin.INSTANCE.getConfig().getString("AddedMessage"))
                        .replace("{word}", word));
                censurePlayer.addCensure(word);

                break;
            }

            case "remove": {
                if (!commandSender.hasPermission("censure.remove")) {
                    commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', CensurePlugin.INSTANCE.getConfig().getString("NoPermMessage")));

                    return;
                }

                if (args.length < 2) {
                    commandSender.sendMessage(ChatColor.RED + "Ошибка, пишите /censure remove <слово>");
                    return;
                }

                String word = args[1].toLowerCase();

                if (!censurePlayer.getCensureWordsList().contains(word)) {
                    commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', CensurePlugin.INSTANCE.getConfig().getString("WordNotCensured")));
                    return;
                }

                censurePlayer.removeCensure(word);
                commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', CensurePlugin.INSTANCE.getConfig().getString("RemovedMessage"))
                        .replace("{word}", word));

                break;
            }

            default: {
                commandSender.sendMessage(ChatColor.translateAlternateColorCodes('&', CensurePlugin.INSTANCE.getConfig().getString("HelpMessage")));
            }
        }
    }
}
