package net.millida.listener;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import lombok.NonNull;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.chat.ComponentSerializer;
import net.millida.CensurePlugin;
import net.millida.player.CensurePlayer;
import org.apache.commons.lang.StringUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.HashMap;

public class ChatListener extends PacketAdapter
        implements Listener {

    public ChatListener() {
        super(CensurePlugin.getPlugin(CensurePlugin.class),
                PacketType.Play.Server.CHAT);
    }

    protected final HashMap<Player, String> lastSendedMessage = new HashMap<>();

    @Override
    public void onPacketSending(PacketEvent event) {
        Player player = event.getPlayer();
        CensurePlayer censurePlayer = CensurePlayer.by(player);

        if (!censurePlayer.isEnableCensure() && !censurePlayer.isEnableMentions()) {
            return;
        }

        WrappedChatComponent wrappedChatComponent = event.getPacket().getChatComponents().read(0);

        BaseComponent[] baseComponents = ComponentSerializer.parse(wrappedChatComponent.getJson());
        String newMessage = baseComponents[0].toLegacyText();

        if (censurePlayer.isEnableMentions() && newMessage.contains(player.getName())) {
            if (lastSendedMessage.get(player) == null || !newMessage.contains(lastSendedMessage.get(player))) {
                Bukkit.getScheduler().runTask(CensurePlugin.INSTANCE, () -> {
                    player.playSound(player.getLocation(), censurePlayer.getMentionsSound(), 1, 1);
                });

                newMessage = underlineWord(player.getName(), newMessage);
            }
        }

        boolean censured = false;
        if (censurePlayer.isEnableCensure()) {
            for (String word : censurePlayer.getCensureWordsList()) {
                if (!newMessage.contains(word)) {
                    continue;
                }

                for (String arg : newMessage.split(" ")) {
                    if (!arg.contains(word)) {
                        continue;
                    }

                    if (censurePlayer.getRemovedWordsList().contains(arg.toLowerCase())) {
                        continue;
                    }

                    censured = true;
                    newMessage = newMessage.replace(word, StringUtils.repeat(CensurePlugin.INSTANCE.getConfig().getString("CensureChar"), word.length()));
                }
            }
        }

        ComponentBuilder censureComponentBuilder = new ComponentBuilder(newMessage);
        if (censured && CensurePlugin.INSTANCE.getConfig().getBoolean("HoverEnable")) {
            censureComponentBuilder.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(baseComponents[0].toLegacyText()).create()));
        }

        event.getPacket().getChatComponents().write(0, WrappedChatComponent.fromJson(ComponentSerializer.toString(censureComponentBuilder.create())));
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        lastSendedMessage.put(event.getPlayer(), event.getMessage());

        if (!event.getPlayer().hasMetadata("censure_add")) {
            return;
        }

        CensurePlayer censurePlayer = CensurePlayer.by(event.getPlayer());
        String word = event.getMessage().split(" ")[0];

        if (censurePlayer.getCensureWordsList().contains(word)) {
            event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', CensurePlugin.INSTANCE.getConfig().getString("WordAlredyCensured")));
            return;
        }

        if (word.length() < CensurePlugin.INSTANCE.getConfig().getInt("MinWordLenght") || word.length() > CensurePlugin.INSTANCE.getConfig().getInt("MaxWordLenght")) {
            event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', CensurePlugin.INSTANCE.getConfig().getString("WordLenghtLimit")));
            return;
        }

        event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', CensurePlugin.INSTANCE.getConfig().getString("AddedMessage"))
                .replace("{word}", word));
        censurePlayer.addCensure(word);

        event.setCancelled(true);
        event.getPlayer().removeMetadata("censure_add", CensurePlugin.INSTANCE);
    }

    protected String underlineWord(@NonNull String foundWord, @NonNull String message) {
        if (!message.contains(foundWord)) {
            return message;
        }

        String[] separatedMessage = message.split(foundWord);

        for (int index = 0; index < separatedMessage.length; index++) {
            String part = separatedMessage[index];

            if (index > 0) {
                separatedMessage[index] = "§r" + ChatColor.getLastColors(separatedMessage[index - 1]).replace("§n", "") + part;
            }

            if (index != separatedMessage.length - 1) {
                separatedMessage[index] += "§n";
            }
        }

        return String.join(foundWord, separatedMessage) + (message.endsWith(foundWord) ? "§n" + foundWord : "");
    }
}