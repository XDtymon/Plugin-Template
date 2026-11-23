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

    // ========================================= EXCHANGE
    public final Message EXCHANGE_NOT_FOUND;

    /** Placeholders: <code>'block', 'value'</code> **/
    public final Message EXCHANGE_ILLEGAL_MONEY_VALUE;
    /** Placeholders: <code>'block'</code> **/
    public final Message EXCHANGE_ILLEGAL_BLOCK_VALUE;

    // ========================================= VAULT
    public final Message VAULT_NOT_FOUND;

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

        // ------------------------------------------------------------------------------------------------------------------ EXCHANGE
        EXCHANGE_NOT_FOUND = textFromLang(
                "exchange.not-found",
                "<prefix> <white>exchange <red>section in <white>config.yml <red>not found"
        );

        EXCHANGE_ILLEGAL_MONEY_VALUE = textFromLang(
                "exchange.illegal-money-value",
                "<prefix> <red>Block <white><block> <red>has an illegal money value <white><value><red>!"
        );

        EXCHANGE_ILLEGAL_BLOCK_VALUE = textFromLang(
                "exchange.illegal-block-value",
                "<prefix> <red>Block <white><block> <red>is not a block!"
        );

        // ------------------------------------------------------------------------------------------------------------------ VAULT
        VAULT_NOT_FOUND = textFromLang(
                "vault-not-found",
                "<prefix> <gold>Vault <red>plugin not found!"
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