package dev.pewienkot.pluginTemplate;

import dev.pewienkot.pluginTemplate.command.pluginTemplate.PluginTemplateCommandTree;
import dev.pewienkot.pluginTemplate.configuration.PluginConfiguration;
import dev.pewienkot.pluginTemplate.handler.ListenerHandler;
import dev.pewienkot.pluginTemplate.messages.Messages;
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public final class PluginTemplate extends JavaPlugin {

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

        listenerHandler = new ListenerHandler(this);

        getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS, event -> {
            event.registrar().register(new PluginTemplateCommandTree(this).create());
        });

        if (configuration.isDebug()) {
            getComponentLogger().info(Component.text("Plugin launched in debug mode!", NamedTextColor.GOLD));
        }
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
