package net.millida.storage.yml.impl;

import lombok.NonNull;
import net.millida.CensurePlugin;
import net.millida.player.CensurePlayer;
import net.millida.storage.yml.BaseConfiguration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.List;
import java.util.stream.Collectors;

public class PlayerDataConfiguration extends BaseConfiguration {

    public PlayerDataConfiguration() {
        super(CensurePlugin.INSTANCE, "playerdata.yml");
    }

    @Override
    protected void onInstall(@NonNull FileConfiguration fileConfiguration) {
        for (String playerName : fileConfiguration.getKeys(false)) {
            ConfigurationSection configurationSection = getLoadedConfiguration().getConfigurationSection(playerName);

            boolean isEnabled = configurationSection.getBoolean("enabled", true);

            List<String> addedWords = configurationSection.getStringList("added-words");
            List<String> removedWords = configurationSection.getStringList("removed-words");

            CensurePlayer censurePlayer = new CensurePlayer(playerName.toLowerCase());

            censurePlayer.setEnableCensure(isEnabled);
            censurePlayer.getRemovedWordsList().addAll(removedWords);
            censurePlayer.getAddedWordsList().addAll(addedWords);


            censurePlayer.getCensureWordsList().removeAll(removedWords);
            censurePlayer.getCensureWordsList().addAll(addedWords);
        }
    }

    public void savePlayer(@NonNull CensurePlayer censurePlayer) {
        getLoadedConfiguration().set(censurePlayer.getPlayerName().toLowerCase() + ".enabled", censurePlayer.isEnableCensure());

        getLoadedConfiguration().set(censurePlayer.getPlayerName().toLowerCase() + ".added-words", censurePlayer.getAddedWordsList());
        getLoadedConfiguration().set(censurePlayer.getPlayerName().toLowerCase() + ".removed-words", censurePlayer.getRemovedWordsList());

        saveConfiguration();
    }
}
