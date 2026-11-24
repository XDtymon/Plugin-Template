package dev.pewienkot.pluginTemplate.handler;

import dev.pewienkot.pluginTemplate.PluginTemplate;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Simple Listener Handler
 * Used to store all listeners in one place
 * <p>
 * {@link reload} Reloads all listeners
 */
public class ListenerHandler {

    // ====================================== FIELDS

    private final PluginTemplate plugin;
    private final PluginManager pm;

    /** All plugin's {@link Listener} */
    private List<Listener> listeners;

    public ListenerHandler(PluginTemplate plugin) {
        this.plugin = plugin;
        this.pm = plugin.getServer().getPluginManager();

        initializeListeners();
        startListening();
    }

    // ====================================== REGISTRY

    /** Initializes new Listeners **/
    public void initializeListeners() {

        listeners = new ArrayList<>(List.of(

        ));
    }

    /** Clears all listeners **/
    public void clearListeners() {
        listeners.clear();
    }

    // --------------------------------------- START
    /** Starts all listeners **/
    public void startListening() {
        listeners.forEach(this::registerListener);
    }

    // --------------------------------------- STOP
    /** Stops all listeners **/
    public void stopListening() {
        listeners.forEach(this::unregisterListener);
    }

    // ====================================== RELOAD

    /** Reloads all listeners **/
    public void reload() {
        clearListeners();
        stopListening();
        initializeListeners();
        startListening();
    }

    // ====================================== UTILITY

    /** Registers a listener **/
    public void registerListener(Listener listener) {
        pm.registerEvents(listener, plugin);
    }

    /** Un-registers a listener **/
    public void unregisterListener(Listener listener) {
        HandlerList.unregisterAll(listener);
    }
}
