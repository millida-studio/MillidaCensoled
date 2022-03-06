package net.millida.storage.yml.impl;

import lombok.NonNull;
import net.millida.CensurePlugin;
import net.millida.player.CensurePlayer;
import net.millida.storage.yml.BaseConfiguration;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;

public class PlayerDataConfiguration extends BaseConfiguration {

    public PlayerDataConfiguration() {
        super(CensurePlugin.INSTANCE, "playerdata.yml");
    }

    @Override
    protected void onInstall(@NonNull FileConfiguration fileConfiguration) {
        for (String playerName : fileConfiguration.getKeys(false)) {
            ConfigurationSection configurationSection = getLoadedConfiguration().getConfigurationSection(playerName);

            boolean isEnabledCensure = configurationSection.getBoolean("enableCensure", true);
            boolean isEnableMentions = configurationSection.getBoolean("enableMentions", true);

            List<String> addedWords = configurationSection.getStringList("added-words");
            List<String> removedWords = configurationSection.getStringList("removed-words");

            Sound sound = Sound.valueOf(configurationSection.getString("mentionsSound"));

            CensurePlayer censurePlayer = new CensurePlayer(playerName.toLowerCase());

            censurePlayer.setEnableMentions(isEnableMentions);
            censurePlayer.setMentionsSound(sound == null ? Sound.ENTITY_PLAYER_LEVELUP : sound);

            censurePlayer.setEnableCensure(isEnabledCensure);
            censurePlayer.getRemovedWordsList().addAll(removedWords);
            censurePlayer.getAddedWordsList().addAll(addedWords);


            censurePlayer.getCensureWordsList().removeAll(removedWords);
            censurePlayer.getCensureWordsList().addAll(addedWords);
        }
    }

    public void savePlayer(@NonNull CensurePlayer censurePlayer) {
        getLoadedConfiguration().set(censurePlayer.getPlayerName().toLowerCase() + ".enableCensure", censurePlayer.isEnableCensure());
        getLoadedConfiguration().set(censurePlayer.getPlayerName().toLowerCase() + ".enableMentions", censurePlayer.isEnableMentions());

        getLoadedConfiguration().set(censurePlayer.getPlayerName().toLowerCase() + ".added-words", censurePlayer.getAddedWordsList());
        getLoadedConfiguration().set(censurePlayer.getPlayerName().toLowerCase() + ".removed-words", censurePlayer.getRemovedWordsList());

        getLoadedConfiguration().set(censurePlayer.getPlayerName().toLowerCase() + ".mentionsSound", censurePlayer.getMentionsSound().name());

        saveConfiguration();
    }
}
