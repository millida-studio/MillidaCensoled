package net.millida.storage.yml;

import com.google.common.base.Preconditions;
import lombok.Getter;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Getter
public abstract class BaseConfiguration {

    protected Plugin plugin;

    protected Path configurationPath;
    protected FileConfiguration loadedConfiguration;


    private BaseConfiguration(@NonNull Plugin plugin, @NonNull Path configurationPath) {
        this.plugin = plugin;
        this.configurationPath = configurationPath;
    }

    public BaseConfiguration(@NonNull Plugin plugin, @NonNull String resourceName) {
        this (plugin, plugin.getDataFolder().toPath().resolve(resourceName));
    }

    @SneakyThrows
    public void createIfNotExists() {
        if (!Files.exists(configurationPath)) {

            if (this.plugin.getClass().getClassLoader().getResourceAsStream(configurationPath.toFile().getName()) != null) {
                this.plugin.saveResource(configurationPath.toFile().getName(), false);
            } else {
                Files.createDirectories(Paths.get( new File(configurationPath.toString().substring(0, configurationPath.toString().lastIndexOf(File.separator))).toURI() ));
                Files.createFile(configurationPath);
            }
        }

        this.loadedConfiguration = YamlConfiguration.loadConfiguration(configurationPath.toFile());
        onInstall(loadedConfiguration);
    }

    protected abstract void onInstall(@NonNull FileConfiguration fileConfiguration);


    @SneakyThrows
    public void saveConfiguration() {
        Preconditions.checkArgument(Files.exists(configurationPath), "Configuration '%s' is`nt exists", configurationPath.toFile().getName());
        Preconditions.checkArgument(loadedConfiguration != null, "Configuration '%s' can`t be null", configurationPath.toFile().getName());

        this.loadedConfiguration.save(configurationPath.toFile());
    }

    public void reloadConfiguration() {
        Preconditions.checkArgument(Files.exists(configurationPath), "Configuration '%s' is`nt exists", configurationPath.toFile().getName());

        this.loadedConfiguration = YamlConfiguration.loadConfiguration(configurationPath.toFile());
    }
}
