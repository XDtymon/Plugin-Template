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