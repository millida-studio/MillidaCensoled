package net.millida.listener;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.chat.ComponentSerializer;
import net.millida.CensurePlugin;
import net.millida.player.CensurePlayer;
import org.apache.commons.lang.StringUtils;
import org.bukkit.entity.Player;

public class ChatListener extends PacketAdapter {

    public ChatListener() {
        super(CensurePlugin.getPlugin(CensurePlugin.class),
                PacketType.Play.Server.CHAT);
    }

    @Override
    public void onPacketSending(PacketEvent event) {
        Player player = event.getPlayer();
        CensurePlayer censurePlayer = CensurePlayer.by(player);

        if (!censurePlayer.isEnableCensure()) {
            return;
        }

        WrappedChatComponent wrappedChatComponent = event.getPacket().getChatComponents().read(0);

        BaseComponent[] baseComponents = ComponentSerializer.parse(wrappedChatComponent.getJson());
        String newMessage = baseComponents[0].toLegacyText();

        boolean censured = false;
        for (String world : censurePlayer.getCensureWordsList()) {
            if (!newMessage.contains(world)) {
                continue;
            }

            for (String arg : newMessage.split(" ")) {
                if (!arg.contains(world)) {
                    continue;
                }

                if (censurePlayer.getRemovedWordsList().contains(arg.toLowerCase())) {
                    continue;
                }

                censured = true;
                newMessage = newMessage.replace(world, StringUtils.repeat(CensurePlugin.INSTANCE.getConfig().getString("CensureChar"), world.length()));
            }
        }

        ComponentBuilder censureComponentBuilder = new ComponentBuilder(newMessage);

        if (censured && CensurePlugin.INSTANCE.getConfig().getBoolean("HoverEnable")) {
            censureComponentBuilder.event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new ComponentBuilder(baseComponents[0].toLegacyText()).create()));
        }

        event.getPacket().getChatComponents().write(0, WrappedChatComponent.fromJson(ComponentSerializer.toString(censureComponentBuilder.create())));
    }
}
