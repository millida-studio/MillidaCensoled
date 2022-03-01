package net.millida.player;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.millida.storage.StorageManager;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

@RequiredArgsConstructor
@Getter
public class CensurePlayer {

    private final String playerName;

    @Setter
    private boolean enableCensure = true;

    private final List<String> censureWordsList = new LinkedList<>();
    private final List<String> addedWordsList = new LinkedList<>();
    private final List<String> removedWordsList = new LinkedList<>();

    public void addCensure(@NonNull String word) {
        if (censureWordsList.contains(word.toLowerCase())) {
            return;
        }

        censureWordsList.add(word.toLowerCase());

        addedWordsList.add(word.toLowerCase());
        removedWordsList.remove(word.toLowerCase());

        StorageManager.INSTANCE.savePlayer(this);
    }

    public void removeCensure(@NonNull String word) {
        if (!censureWordsList.contains(word.toLowerCase())) {
            return;
        }

        censureWordsList.remove(word.toLowerCase());

        addedWordsList.remove(word.toLowerCase());
        removedWordsList.add(word.toLowerCase());

        StorageManager.INSTANCE.savePlayer(this);
    }


    public static final HashMap<String, CensurePlayer> CENSURE_PLAYER_MAP = new HashMap<>();

    public static CensurePlayer by(@NonNull Player player) {
        CensurePlayer censurePlayer = CENSURE_PLAYER_MAP.get(player.getName().toLowerCase());

        if (censurePlayer == null) {
            censurePlayer = new CensurePlayer(player.getName().toLowerCase());
            StorageManager.INSTANCE.loadPlayer(censurePlayer);

            CENSURE_PLAYER_MAP.put(censurePlayer.getPlayerName().toLowerCase(), censurePlayer);
        }

        return censurePlayer;
    }

}
