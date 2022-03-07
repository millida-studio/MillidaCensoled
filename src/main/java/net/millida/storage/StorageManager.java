package net.millida.storage;

import lombok.Getter;
import lombok.NonNull;
import net.millida.CensurePlugin;
import net.millida.player.CensurePlayer;
import net.millida.storage.mysql.MysqlConnection;
import net.millida.storage.mysql.MysqlExecutor;
import net.millida.storage.yml.impl.PlayerDataConfiguration;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;

import java.sql.ResultSet;
import java.util.stream.Collectors;

@Getter
public final class StorageManager {

    public static final StorageManager INSTANCE = new StorageManager();





    protected StorageType storageType;

    protected PlayerDataConfiguration playerDataConfiguration = new PlayerDataConfiguration();
    protected MysqlExecutor mysqlConnection;

    // =============================================== MYSQL ========================================== \\

    private static final String DISABLE_QUERY = "INSERT IGNORE INTO `CensureEnabled` (`Name`) VALUES (?)";
    private static final String ENABLE_QUERY = "DELETE FROM `CensureEnabled` WHERE `Name`=?";
    private static final String IS_ENABLE_QUERY = "SELECT * FROM `CensureEnabled` WHERE `Name`=?";

    private static final String LOAD_WORDS_QUERY = "SELECT * FROM `CensureWords` WHERE `Name`=?";
    private static final String ADD_WORD_QUERY = "INSERT INTO `CensureWords` (`Name`, `Word`, `Remove`) VALUES (?,?,?)";
    private static final String DELETE_WORD_QUERY = "DELETE FROM `CensureWords` WHERE `Name`=? AND `Word`=?";

    private static final String MENTIONS_SAVE_QUERY = "INSERT INTO `Mentions` (`Name`, `Enable`, `Sound`) VALUES (?,?,?) ON DUPLICATE KEY UPDATE `Enable`=?, `Sound`=?";
    private static final String MENTIONS_LOAD_QUERY = "SELECT * FROM `Mentions` WHERE `Name`=?";

    public StorageManager() {
        this.storageType = StorageType.valueOf(CensurePlugin.INSTANCE.getConfig().getString("StorageType").toUpperCase());

        if (storageType == null) {
            Bukkit.getLogger().info(ChatColor.RED + "Тип хранения: " + CensurePlugin.INSTANCE.getConfig().getString("StorageType")  + " не найден!");

            this.storageType = StorageType.YML;
        }
    }

    public void init(@NonNull FileConfiguration configuration) {
        switch (storageType) {
            case YML: {
                playerDataConfiguration.createIfNotExists();
                break;
            }

            case MYSQL: {
                mysqlConnection = MysqlConnection.newBuilder()
                        .setHost(configuration.getString("mysql.host"))
                        .setPort(configuration.getInt("mysql.port"))
                        .setUsername(configuration.getString("mysql.user"))
                        .setPassword(configuration.getString("mysql.pass"))
                        .setDatabase(configuration.getString("mysql.database"))

                        .createTable("CensureEnabled", "`Name` VARCHAR(256) NOT NULL PRIMARY KEY")
                        .createTable("Mentions", "`Name` VARCHAR(256) NOT NULL PRIMARY KEY, `Enable` BOOLEAN NOT NULL, `Sound` TEXT NOT NULL")
                        .createTable("CensureWords", "`Name` VARCHAR(256) NOT NULL, `Word` TEXT NOT NULL, `Remove` BOOLEAN NOT NULL")

                        .build().getExecutor();
            }
        }
    }

    public void savePlayer(@NonNull CensurePlayer censurePlayer) {
        switch (storageType) {
            case YML: {
                playerDataConfiguration.savePlayer(censurePlayer);
                break;
            }

            case MYSQL: {
                mysqlConnection.execute(true, censurePlayer.isEnableCensure() ? ENABLE_QUERY : DISABLE_QUERY, censurePlayer.getPlayerName().toLowerCase());

                censurePlayer.getRemovedWordsList().forEach(word -> {
                    mysqlConnection.execute(true, DELETE_WORD_QUERY, censurePlayer.getPlayerName().toLowerCase(), word.toLowerCase());
                    mysqlConnection.execute(true, ADD_WORD_QUERY, censurePlayer.getPlayerName().toLowerCase(), word.toLowerCase(), true);
                });

                censurePlayer.getAddedWordsList().forEach(word -> {
                    mysqlConnection.execute(true, DELETE_WORD_QUERY, censurePlayer.getPlayerName().toLowerCase(), word.toLowerCase());
                    mysqlConnection.execute(true, ADD_WORD_QUERY, censurePlayer.getPlayerName().toLowerCase(), word.toLowerCase(), false);
                });

                mysqlConnection.execute(true, MENTIONS_SAVE_QUERY, censurePlayer.getPlayerName().toLowerCase(), censurePlayer.isEnableMentions(), censurePlayer.getMentionsSound().name(),
                        censurePlayer.isEnableMentions(), censurePlayer.getMentionsSound().name());
            }
        }
    }

    public void loadPlayer(@NonNull CensurePlayer censurePlayer) {
        censurePlayer.getCensureWordsList().addAll(CensurePlugin.INSTANCE.getDefaultCensuredWords());
        censurePlayer.getRemovedWordsList().addAll(CensurePlugin.INSTANCE.getLangConfiguration().getStringList("RemovedWords").stream()
                .map(String::toLowerCase)
                .collect(Collectors.toList()));

        switch (storageType) {
            case YML: {
                //Уже лоадиться в onInstall
                break;
            }

            case MYSQL: {
                mysqlConnection.executeQuery(true, IS_ENABLE_QUERY, o -> {
                    censurePlayer.setEnableCensure(!o.next());
                    return null;
                }, censurePlayer.getPlayerName().toLowerCase());

                mysqlConnection.executeQuery(true, LOAD_WORDS_QUERY, rs -> {

                    while (rs.next()) {
                        String word = rs.getString("Word");
                        boolean removed = rs.getBoolean("Remove");

                        if (removed) {
                            censurePlayer.getRemovedWordsList().add(word.toLowerCase());
                            censurePlayer.getCensureWordsList().remove(word.toLowerCase());
                            continue;
                        }

                        censurePlayer.getAddedWordsList().add(word.toLowerCase());
                        if (!censurePlayer.getCensureWordsList().contains(word.toLowerCase())) censurePlayer.getCensureWordsList().add(word.toLowerCase());
                    }

                    return null;
                }, censurePlayer.getPlayerName().toLowerCase());

                mysqlConnection.executeQuery(true, MENTIONS_LOAD_QUERY, rs -> {

                    if (rs.next()) {
                        censurePlayer.setEnableMentions(rs.getBoolean("Enable"));

                        Sound sound = Sound.valueOf(rs.getString("Sound"));
                        censurePlayer.setMentionsSound(sound == null ? Sound.ENTITY_PLAYER_LEVELUP : sound);
                    }

                    return null;
                }, censurePlayer.getPlayerName().toLowerCase());
            }
        }
    }


}
