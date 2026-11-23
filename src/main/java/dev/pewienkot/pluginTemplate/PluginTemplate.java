package dev.pewienkot.pluginTemplate;

import dev.pewienkot.boxMoneyConverter.config.PluginConfiguration;
import dev.pewienkot.pluginTemplate.messages.Messages;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public final class Plugintemplate extends JavaPlugin {

    // ------------------------------------------------ FIELDS

    /** Plugin's configuration **/
    private PluginConfiguration configuration;
    /** Plugin's messages **/
    private Messages messages;


    /** Plugin's listenerHandler **/
    private ListenerHandler listenerHandler;

    // ================================================= STARTUP


    // --------------------------- ENABLE
    @Override
    public void onEnable() {
        initialize();
    }

    // --------------------------- DISABLE
    @Override
    public void onDisable() {

    }

    // ================================================= INITIALIZATION

    public void initialize() {
        configuration = new PluginConfiguration(this);
        messages = new Messages(this);

        initializeEconomy();



        getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> event.registrar().register(new BoxMoneyConverter(this).create()));

        getLogger().info("Plugin launched successfully! debug="+configuration.isDebug());
    }

    // ================================================= RELOAD

    public void reload() {

        payoutManager.flushAllCache();
        payoutManager.reload();

        configuration.reload();
        blockExchangeManager.reload();

        messages = new Messages(this);

        listenerHandler.reload();

    }

    // ================================================= REGISTRY

    private void initializeEconomy() {

        try {

            try {
                if (getServer().getPluginManager().getPlugin("Vault") == null) {
                    throw new PluginException(messages.VAULT_NOT_FOUND);
                }

                RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
                if (rsp == null) {
                    throw new PluginException(messages.VAULT_NOT_FOUND);
                }

                ECONOMY = rsp.getProvider();
            } catch (Error error) {
                Bukkit.getConsoleSender().sendMessage(Component.text("Plugin FATAL Error:", NamedTextColor.DARK_RED));
                error.printStackTrace();
            }

        } catch (Exception e) {
            if (e instanceof PluginException pluginException) {
                fatalErrorStop(pluginException.component());
                return;
            }

            fatalErrorStop(Component.text(e.getMessage()));
        }

    }

    // ================================================= UTILITY

    public void fatalErrorStop(Component component) {
        Bukkit.getConsoleSender().sendMessage(component);
        getServer().getPluginManager().disablePlugin(this);
    }


    // ================================================= ACCESS

    public PluginConfiguration configuration() {
        return configuration;
    }

    public Messages messages() {
        return messages;
    }

    public Economy economy() {
        return ECONOMY;
    }

    public BlockExchangeManager blockExchangeManager() {
        return blockExchangeManager;
    }

    public PayoutManager payoutManager() {
        return payoutManager;
    }
}
