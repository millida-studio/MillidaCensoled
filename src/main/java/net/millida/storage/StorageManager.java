package net.millida.storage;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import net.millida.CensurePlugin;
import net.millida.player.CensurePlayer;
import net.millida.storage.mysql.MysqlConnection;
import net.millida.storage.mysql.MysqlExecutor;
import net.millida.storage.yml.impl.PlayerDataConfiguration;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.stream.Collectors;

@Getter
public final class StorageManager {

    public static final StorageManager INSTANCE = new StorageManager();





    @Setter
    protected StorageType storageType;

    protected PlayerDataConfiguration playerDataConfiguration = new PlayerDataConfiguration();
    protected MysqlExecutor mysqlConnection;

    // =============================================== MYSQL ========================================== \\

    private static final String UPDATE_STATUS_QUERY = "INSERT IGNORE INTO `CensureStatus` (`Name`, `Enable`) VALUES (?,?) ON DUPLICATE KEY UPDATE `Enable`=?";
    private static final String LOAD_STATUS_QUERY = "SELECT * FROM `CensureEnabled` WHERE `Name`=?";

    private static final String LOAD_WORDS_QUERY = "SELECT * FROM `CensureWords` WHERE `Name`=?";
    private static final String ADD_WORD_QUERY = "INSERT INTO `CensureWords` (`Name`, `Word`, `Remove`) VALUES (?,?,?)";
    private static final String DELETE_WORD_QUERY = "DELETE FROM `CensureWords` WHERE `Name`=? AND `Word`=?";

    public StorageManager() {
        this.storageType = StorageType.valueOf(CensurePlugin.INSTANCE.getConfig().getString("StorageType").toUpperCase().replace("YML", "LOCAL"));

        if (storageType == null) {
            Bukkit.getLogger().info(ChatColor.RED + "Storage type " + CensurePlugin.INSTANCE.getConfig().getString("StorageType")  + " not found!");

            this.storageType = StorageType.LOCAL;
        }
    }

    public void init(@NonNull FileConfiguration configuration) {
        switch (storageType) {
            case LOCAL: {
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

                        .createTable("CensureStatus", "`Name` VARCHAR(256) NOT NULL PRIMARY KEY, `Enable` BOOLEAN NOT NULL")
                        .createTable("CensureWords", "`Name` VARCHAR(256) NOT NULL, `Word` TEXT NOT NULL, `Remove` BOOLEAN NOT NULL")

                        .build().getExecutor();
            }
        }
    }

    public void savePlayer(@NonNull CensurePlayer censurePlayer) {
        switch (storageType) {
            case LOCAL: {
                playerDataConfiguration.savePlayer(censurePlayer);
                break;
            }

            case MYSQL: {
                mysqlConnection.execute(true, UPDATE_STATUS_QUERY, censurePlayer.getPlayerName().toLowerCase(), censurePlayer.isEnableCensure(), censurePlayer.isEnableCensure());

                censurePlayer.getRemovedWordsList().forEach(word -> {
                    mysqlConnection.execute(true, DELETE_WORD_QUERY, censurePlayer.getPlayerName().toLowerCase(), word.toLowerCase());
                    mysqlConnection.execute(true, ADD_WORD_QUERY, censurePlayer.getPlayerName().toLowerCase(), word.toLowerCase(), true);
                });

                censurePlayer.getAddedWordsList().forEach(word -> {
                    mysqlConnection.execute(true, DELETE_WORD_QUERY, censurePlayer.getPlayerName().toLowerCase(), word.toLowerCase());
                    mysqlConnection.execute(true, ADD_WORD_QUERY, censurePlayer.getPlayerName().toLowerCase(), word.toLowerCase(), false);
                });
            }
        }
    }

    public void loadPlayer(@NonNull CensurePlayer censurePlayer) {
        censurePlayer.getCensureWordsList().addAll(CensurePlugin.INSTANCE.getDefaultCensuredWords());
        censurePlayer.getRemovedWordsList().addAll(CensurePlugin.INSTANCE.getLangConfiguration().getStringList("RemovedWords").stream()
                .map(String::toLowerCase)
                .collect(Collectors.toList()));

        switch (storageType) {
            case LOCAL: {
                //Уже лоадиться в onInstall, но так как loadPlayer вызывается в случае, когда игрока нет в кэше, то значит его нет в файле, соответсвенно сохраняем его. Такая вот история
                playerDataConfiguration.savePlayer(censurePlayer);
                break;
            }

            case MYSQL: {
                mysqlConnection.executeQuery(true, LOAD_STATUS_QUERY, rs -> {

                    if (rs.next()) {
                        censurePlayer.setEnableCensure(rs.getBoolean("Enable"));
                    }


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
            }
        }
    }


}
