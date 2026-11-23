package dev.pewienkot.pluginTemplate.handler;

import dev.pewienkot.pluginTemplate.PluginTemplate;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;

import java.util.ArrayList;
import java.util.List;

/**
 * TODO: comment this shi
 */
public class ListenerHandler {

    // ====================================== FIELDS

    private final PluginTemplate plugin;
    private final PluginManager pm;

    private List<Listener> listeners;

    public ListenerHandler(PluginTemplate plugin) {
        this.plugin = plugin;
        this.pm = plugin.getServer().getPluginManager();

        initializeListeners();
        startListening();
    }

    // ====================================== REGISTRY

    public void initializeListeners() {
        listeners = new ArrayList<>(List.of(

        ));
    }

    public void clearListeners() {
        listeners.clear();
    }

    // --------------------------------------- START
    public void startListening() {
        listeners.forEach(this::registerListener);
    }

    // --------------------------------------- STOP
    public void stopListening() {
        listeners.forEach(this::unregisterListener);
    }

    // ====================================== RELOAD

    public void reload() {
        clearListeners();
        stopListening();
        initializeListeners();
        startListening();
    }

    // ====================================== UTILITY

    public void registerListener(Listener listener) {
        pm.registerEvents(listener, plugin);
    }

    public void unregisterListener(Listener listener) {
        HandlerList.unregisterAll(listener);
    }
}
