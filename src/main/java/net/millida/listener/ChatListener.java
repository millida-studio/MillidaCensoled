package net.millida.listener;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import lombok.NonNull;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
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

        if (!censurePlayer.isEnableCensure() && !censurePlayer.isEnableMentions()) {
            return;
        }

        WrappedChatComponent wrappedChatComponent =
                event.getPacket().getChatComponents().read(0);

        if (wrappedChatComponent == null) {
            Bukkit.getLogger().info(ChatColor.RED + "ChatComponent is null");
            Bukkit.getLogger().info(event.getPacket().getChatComponents().read(0).getJson());
            return;
        }

        BaseComponent[] baseComponents = ComponentSerializer.parse(wrappedChatComponent.getJson());

        String message = baseComponents[0].toLegacyText();
        String newMessage = String.valueOf(message);

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
                            .replaceAll(Matcher.quoteReplacement(StringUtils.repeat(CensurePlugin.INSTANCE.getConfig().getString("CensureChar"), word.length())));
                }
            }
        }

        baseComponents = TextComponent.fromLegacyText(newMessage);
        if (censured && CensurePlugin.INSTANCE.getConfig().getBoolean("HoverEnable")) {
            for (BaseComponent baseComponent : baseComponents) {
                baseComponent.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText(message)));
            }
        }

        event.getPacket().getChatComponents().write(0, WrappedChatComponent.fromJson(ComponentSerializer.toString(baseComponents)));
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
