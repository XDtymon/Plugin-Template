package dev.pewienkot.pluginTemplate.messages;

import dev.pewienkot.pluginTemplate.PluginTemplate;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.configuration.file.FileConfiguration;

/**
 * Class for holding all plugin's
 * command / info messages
 */
public class Messages {


    private final FileConfiguration langFile;
    private final FileConfiguration configFile;

    /**
     * Used to resolve 'prefix' placeholder
     * In all messages
     **/
    private final TagResolver prefix;

    // ========================================= MAIN MESSAGES
    public final Message PLUGIN_RELOAD;
    public final Message PLUGIN_ABORT;

    // ========================================= DATABASE MESSAGES

    // ---------- DATABASE ERRORS
    public final Message DATABASE_ERROR_CONFIG;
    public final Message DATABASE_ERROR_JDBC;
    public final Message DATABASE_ERROR_TYPE;
    public final Message DATABASE_ERROR_TABLES;
    public final Message DATABASE_ERROR_CONNECT;

    // ---------- DATABASE CONNECTION
    public final Message DATABASE_CONNECTED;
    public final Message DATABASE_DISCONNECTED;
    public final Message DATABASE_RELOAD;
    public final Message DATABASE_RELOAD_ERROR;

    // ---------- DATABASE TOGGLING
    public final Message DATABASE_LOCKED;
    public final Message DATABASE_TOGGLE;
    public final Message DATABASE_STILL_DISCONNECTED;

    // ======================================================== REGISTRY
    public Messages(PluginTemplate plugin) {

        langFile = plugin.configuration().lang();
        configFile = plugin.getConfig();

        // ---- PREFIX
        prefix = plugin.configuration().prefix();

        // ------------------------------------------------------------------------------------------------------------------ MAIN
        PLUGIN_RELOAD = textFromLang(
                "plugin.reload",
                "<prefix> <gold>Plugin <green>has been successfully reloaded!"
        );

        PLUGIN_ABORT = textFromLang(
                "plugin.abort",
                "<prefix> <red>Plugin had <dark_red><b>crashed</b>"
        );

        // ------------------------------------------------------------------------------------------------------------------ DATABASE MAIN
        // ---------- DATABASE ERRORS
        DATABASE_ERROR_CONFIG = textFromLang(
                "database.error-config",
                "<prefix> <gold>DB<gray>'s <red>config <dark_grey>[database section in config.yml] <red>is missing!"
        );

        DATABASE_ERROR_JDBC = textFromLang(
                "database.error-jdbc",
                "<prefix> <gold>JDBC driver <red>has not been found for <white><type>"
        );

        DATABASE_ERROR_TYPE = textFromLang(
                "database.error-type",
                "<prefix> <red>Unknown <gold>DB <red>type <white><type><gray>. <red>Use <gold>MY_SQL <red>or <gold>SQLITE! <dark_red>Falling back to SQLITE...."
        );

        DATABASE_ERROR_TABLES = textFromLang(
                "database.error-tables",
                "<prefix> <red>Failed to create <gold>DB <red>tables! <dark_red>StackTrace:<reset> <e>"
        );

        DATABASE_ERROR_CONNECT = textFromLang(
                "database.error-connect",
                "<prefix> <red>Failed to connect to the <gold>DB<red>! <dark_red>StackTrace:<reset> <e>"
        );

        // ---------- DATABASE CONNECTION
        DATABASE_CONNECTED = textFromLang(
                "database.connected",
                "<prefix> <gold>DB <green>has successfully connected!"
        );

        DATABASE_DISCONNECTED = textFromLang(
                "database.disconnected",
                "<prefix> <gold>DB <green>has successfully disconnected!"
        );

        DATABASE_RELOAD = textFromLang(
                "database.reload",
                "<prefix> <green>The <gold>DB <green>has been reloaded!"
        );

        DATABASE_RELOAD_ERROR = textFromLang(
                "database.reload-error",
                "<prefix> <red>The <gold>DB <red>reload has failed! Features stay locked."
        );

        // ---------- DATABASE TOGGLING
        DATABASE_LOCKED = textFromLang(
                "database.locked",
                "<prefix> <red>Any features using the <gold>DB <red>were locked!"
        );

        DATABASE_TOGGLE = textFromLang(
                "database.toggle",
                "<prefix> <gray>The <gold>DB <gray>has been toggled. <red>Locked: <white><state></white>"
        );

        DATABASE_STILL_DISCONNECTED = textFromLang(
                "database.still-disconnected",
                "<prefix> <red>Failed to toggle the <gold>DB<red>. It's still disconnected!"
        );
    }

    // ================================================ UTILITY

    private Message textFromLang(String path, String defaultMsg) {
        return new Message(langFile.getString(path, defaultMsg), prefix);
    }

    public Message textFromConfig(String path, String defaultMsg) {
        return new Message(configFile.getString(path, defaultMsg), prefix);
    }

    public Message valueOf(String component, String defaultMsg) {
        return new Message(defaultText(component, defaultMsg), prefix);
    }

    public Message valueOf(String component) {
        return new Message(component, prefix);
    }

    public String defaultText(String component, String defaultMsg) {
        return (component == null || component.isBlank()) ? defaultMsg : component;
    }
}