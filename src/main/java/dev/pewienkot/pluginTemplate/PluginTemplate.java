package dev.pewienkot.pluginTemplate;

import dev.pewienkot.boxMoneyConverter.config.PluginConfiguration;
import dev.pewienkot.pluginTemplate.handler.ListenerHandler;
import dev.pewienkot.pluginTemplate.messages.Messages;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class PluginTemplate extends JavaPlugin {

    // ------------------------------------------------ FIELDS

    /**
     * Plugin's configuration
     **/
    private PluginConfiguration configuration;
    /**
     * Plugin's messages
     **/
    private Messages messages;


    /**
     * Plugin's listenerHandler
     **/
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

        getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> event.registrar().register(new BoxMoneyConverter(this).create()));

        getLogger().info("Plugin launched successfully! debug=" + configuration.isDebug());
    }

    // ================================================= RELOAD

    public void reload() {

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

}
