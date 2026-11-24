package dev.pewienkot.pluginTemplate.configuration;

import dev.pewienkot.pluginTemplate.PluginTemplate;
import dev.pewienkot.pluginTemplate.messages.Message;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Contains all Plugin's Configuration
 * TODO: MORE COMMENTING
 */
public class PluginConfiguration {

    // ======================================================== CONSTANTS

    /** Available languages. */
    private static final List<String> BUNDLED_LANGUAGES = List.of("en", "pl");
    private static final String DEFAULT_LANGUAGE = "en";
    private static final Pattern LANGUAGE_CODE = Pattern.compile("[a-z]{2,3}([_-][a-z0-9]{2,4})?");

    private static final Set<Integer> ALLOWED_UPDATE_TIMES = Set.of(1, 2, 4, 5, 10, 20);
    private static final int DEFAULT_UPDATE_TIME = 1;

    // ======================================================== FIELDS

    private final PluginTemplate plugin;

    private TagResolver prefix;
    private String language;
    private int updateTime;
    private boolean debug;

    private FileConfiguration lang;

    // ======================================================== REGISTRY

    public PluginConfiguration(PluginTemplate plugin) {
        this.plugin = plugin;
        load();
    }

    /**
     * Loads config and lang
     * Used for {@link PluginTemplate#reload()}
     * */
    public void load() {
        plugin.saveDefaultConfig();
        plugin.reloadConfig();
        FileConfiguration config = plugin.getConfig();

        saveBundledLanguages();
        language = readLanguage(config);
        lang = loadLanguage(language);
        updateTime = readUpdateTime(config);
        debug = readDebug(config);
        prefix = Placeholder.parsed("prefix", lang.getString("prefix", "<gray>[<gold>BoxMoneyConverter</gold>]"));

        plugin.getLogger().info("Successfully loaded plugin's configuration!");

    }

    // ======================================================== RELOAD

    /**
     * Same as load <p>
     * Used in {@link PluginTemplate#reload()}
     */
    public void reload() {
        load();
    }

    // ======================================================== SETTINGS

    private int readUpdateTime(FileConfiguration config) {
        int value = config.getInt("update-time", DEFAULT_UPDATE_TIME);

        if (!ALLOWED_UPDATE_TIMES.contains(value)) {
            plugin.getLogger().warning("Invalid update-time '" + value + "' (allowed: "
                    + ALLOWED_UPDATE_TIMES.stream().sorted().toList() + "), using " + DEFAULT_UPDATE_TIME + ".");
            return DEFAULT_UPDATE_TIME;
        }

        return value;
    }

    private int readInt(FileConfiguration config, String path, int defaultValue) {
        return config.getInt(path, defaultValue);
    }

    private Message readMessage(FileConfiguration config, String path, String defaultValue) {
        return new Message(config.getString(path, defaultValue), prefix);
    }

    private boolean readDebug(FileConfiguration config) {
        return config.getBoolean("debug", false);
    }

    // ======================================================== LANGUAGE

    private File languageFolder() {
        return new File(plugin.getDataFolder(), "language");
    }

    private File languageFile(String code) {
        return new File(languageFolder(), code + ".yml");
    }

    private void saveBundledLanguages() {
        for (String code : BUNDLED_LANGUAGES) {
            String path = "language/" + code + ".yml";

            if (plugin.getResource(path) == null) continue; // nie ma go w jarze
            if (languageFile(code).exists()) continue;

            plugin.saveResource(path, false);
        }
    }

    private String readLanguage(FileConfiguration config) {
        String code = config.getString("language", DEFAULT_LANGUAGE).trim().toLowerCase(Locale.ROOT);

        if (!LANGUAGE_CODE.matcher(code).matches()) {
            plugin.getLogger().warning("Invalid language '" + code + "', using '" + DEFAULT_LANGUAGE + "'.");
            return DEFAULT_LANGUAGE;
        }

        if (!languageFile(code).exists()) {
            plugin.getLogger().warning("Language file '" + code + ".yml' not found (available: "
                    + availableLanguages() + "), using '" + DEFAULT_LANGUAGE + "'.");
            return DEFAULT_LANGUAGE;
        }

        return code;
    }

    private FileConfiguration loadLanguage(String code) {
        File file = languageFile(code);

        if (!file.exists()) {
            plugin.getLogger().warning("Language file '" + code + ".yml' is missing, using built-in messages.");
            return new YamlConfiguration();
        }

        return YamlConfiguration.loadConfiguration(file);
    }

    public List<String> availableLanguages() {
        List<String> result = new ArrayList<>();

        File[] files = languageFolder().listFiles((dir, name) -> name.endsWith(".yml"));
        if (files == null) return result;

        for (File file : files) {
            String name = file.getName();
            result.add(name.substring(0, name.length() - 4));
        }

        result.sort(String::compareTo);
        return result;
    }

    // ======================================================== ACCESS

    /** Plugin's prefix {@link TagResolver} **/
    public TagResolver prefix() {
        return prefix;
    }

    /** Current language code */
    public String language() {
        return language;
    }

    /** Current Update Time  */
    public int updateTime() {
        return updateTime;
    }

    /** @return Language configuration file */
    public FileConfiguration lang() {
        return lang;
    }

    /** @return is plugin in Debug mode? **/
    public boolean isDebug() {
        return debug;
    }

}