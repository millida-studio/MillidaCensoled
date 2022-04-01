package net.millida.listener;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
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
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

        if (!censurePlayer.isEnableCensure()) {
            return;
        }

        WrappedChatComponent wrappedChatComponent =
                event.getPacket().getChatComponents().read(0);

        if (wrappedChatComponent == null) {
            return;
        }

        BaseComponent[] baseComponents = ComponentSerializer.parse(wrappedChatComponent.getJson());

        String message = baseComponents[0].toLegacyText();
        String newMessage = String.valueOf(message);

        boolean censured = false;

        for (String word : censurePlayer.getCensureWordsList()) {
            if (!newMessage.toLowerCase().contains(word.toLowerCase())) {
                continue;
            }

            for (String arg : newMessage.split(" ")) {
                if (!arg.toLowerCase().contains(word.toLowerCase())) {
                    continue;
                }

                if (censurePlayer.getRemovedWordsList().contains(arg.toLowerCase())) {
                    continue;
                }

                censured = true;
                newMessage = Pattern.compile(word, Pattern.LITERAL | Pattern.CASE_INSENSITIVE | Pattern.UNICODE_CASE).matcher(newMessage)
                        .replaceAll(Matcher.quoteReplacement(censureWord(word.length())));
            }
        }

        //Нет смысла ебашить дальше пакет, ведь мы ничего не нашли на цензуру
        if (!censured) {
            return;
        }

        HoverEvent hoverEvent = baseComponents[0].getHoverEvent();
        ClickEvent clickEvent = baseComponents[0].getClickEvent();

        BaseComponent[] formattedBaseComponents = TextComponent.fromLegacyText(newMessage);

        for (BaseComponent baseComponent : formattedBaseComponents) {
            if (hoverEvent != null) {
                baseComponent.setHoverEvent(hoverEvent);
            }

            if (clickEvent != null) {
                baseComponent.setClickEvent(clickEvent);
            }
        }

        if (CensurePlugin.INSTANCE.getConfig().getBoolean("HoverEnable")) {
            for (BaseComponent baseComponent : formattedBaseComponents) {
                baseComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(message)));
            }
        }

        event.getPacket().getChatComponents().write(0, WrappedChatComponent.fromJson(ComponentSerializer.toString(formattedBaseComponents)));
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        if (CensurePlugin.INSTANCE.getConfig().getBoolean("ChatFormatEnable")) {
            event.setCancelled(true);

            for (Player player : Bukkit.getOnlinePlayers()) {
                player.sendMessage(CensurePlugin.INSTANCE.getConfig().getString("ChatFormat").replace("{player}", event.getPlayer().getName())
                        .replace("{message}", event.getMessage()));
            }
        }

        lastSendedMessage.put(event.getPlayer(), event.getMessage());

        if (!event.getPlayer().hasMetadata("censure_add")) {
            return;
        }
        event.setCancelled(true);

        CensurePlayer censurePlayer = CensurePlayer.by(event.getPlayer());
        String word = event.getMessage().split(" ")[0];

        if (censurePlayer.getCensureWordsList().contains(word)) {
            event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', CensurePlugin.INSTANCE.getLangConfiguration().getString("WordAlredyCensured")));
            return;
        }

        if (word.length() < CensurePlugin.INSTANCE.getConfig().getInt("MinWordLenght") || word.length() > CensurePlugin.INSTANCE.getConfig().getInt("MaxWordLenght")) {
            event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', CensurePlugin.INSTANCE.getLangConfiguration().getString("WordLenghtLimit")));
            return;
        }

        event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&', CensurePlugin.INSTANCE.getLangConfiguration().getString("AddedMessage"))
                .replace("{word}", word));
        censurePlayer.addCensure(word);

        event.getPlayer().removeMetadata("censure_add", CensurePlugin.INSTANCE);
    }

    protected String censureWord(int lenght) {
        if (!CensurePlugin.INSTANCE.getConfig().getBoolean("RandomCharsEnable")) {
            return StringUtils.repeat(CensurePlugin.INSTANCE.getConfig().getString("CensureChar"), lenght);
        }

        List<String> censureCharsList = CensurePlugin.INSTANCE.getConfig().getStringList("RandomChars");

        StringBuilder censuredString = new StringBuilder();
        for (int i = 0; i < lenght; i++) {
            censuredString.append(censureCharsList.get(ThreadLocalRandom.current().nextInt(censureCharsList.size())));
        }

        return censuredString.toString();
    }
}
