package net.millida.command.api;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Arrays;

public abstract class SimpleCommand extends Command implements CommandExecutor {

    protected final String command;
    protected final String[] aliases;

    protected boolean forOnlyPlayers;


    public SimpleCommand(String command, String... aliases) {
        super(command, "", "/" + command, Arrays.asList(aliases));

        this.command = command;
        this.aliases = aliases;
    }

    /**
     * Установить разрешение команде на использование
     * ее ТОЛЬКО игроками на сервере
     *
     * @param forOnlyPlayers - разрешение
     */
    protected void setOnlyPlayers(boolean forOnlyPlayers) {
        this.forOnlyPlayers = forOnlyPlayers;
    }


    public abstract void execute(CommandSender commandSender, String... args);

    @Override
    public boolean execute(CommandSender commandSender, String label, String[] args) {
        if (forOnlyPlayers && !(commandSender instanceof Player)) {
            return true;
        }

        execute(commandSender, args);
        return false;
    }

    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        return execute(commandSender, label, args);
    }
}
