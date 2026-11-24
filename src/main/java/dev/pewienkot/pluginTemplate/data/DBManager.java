package dev.pewienkot.pluginTemplate.data;

import dev.pewienkot.pluginTemplate.PluginTemplate;
import dev.pewienkot.pluginTemplate.messages.Messages;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;

public class DBManager {

    public enum DBState {
        CONNECTED,
        DISCONNECTED,
        DISABLED
    }

    private final PluginTemplate plugin;
    private final Messages msg;

    private final DBConnection dbConnection;

    private volatile boolean locked = false;

    public DBManager(PluginTemplate plugin) {
        this.plugin = plugin;
        this.msg = plugin.messages();

        this.dbConnection = new DBConnection(plugin);

        Bukkit.getScheduler().runTaskLater(plugin, dbConnection::connect, 1L);
    }

    // ======================================= LIFECYCLE

    /**
     * Connects and prepares the tables. Call it in onEnable, AFTER the menu configuration is loaded
     * (ArmoryManager reads the slots from it).
     *
     * @return false if no database could be opened (the plugin can disable itself then)
     */
    public boolean start() {
        if (connectAndInit()) return true;

        locked = true;
        msg.DATABASE_LOCKED.message().console();
        return false;
    }

    public void shutdown() {
        saveAll();
        dbConnection.close();
    }

    private boolean connectAndInit() {
        if (!dbConnection.connect()) return false;
        return true;
    }

    // ======================================= API

    /**
     * Writes all changed player data to the database. Does nothing if there is no connection.
     * Works also while locked, so data is never lost.
     */
    public void saveAll() {
        if (!dbConnection.isConnected()) {
            return;
        }

        /*
        int saved = armoryManager.saveAll();
        if (saved < 0) {
            msg.DATABASE_SAVE_ALL.message(
                    Placeholder.unparsed("amount", "None")
            ).console();
            return;
        }

        msg.DATABASE_SAVE_ALL.message(
                Placeholder.unparsed("amount", String.valueOf(saved))
        ).console();
         */
    }

    /**
     * Locks or unlocks the mechanics.
     * <ul>
     *   <li>unlocked -> locked: saves everything, then players cannot use the armory,</li>
     *   <li>locked -> unlocked: makes sure the database works again (reconnects if needed)
     *       and only then lets players back in. If the database is still down, stays locked.</li>
     * </ul>
     *
     * @return the state after toggling
     */
    public DBState toggleDB() {
        if (!locked) {
            saveAll();
            locked = true;
            msg.DATABASE_TOGGLE.message(
                    Placeholder.unparsed("state", String.valueOf(locked))
            ).console();
            return getDBState();
        }

        boolean ready;
        if (dbConnection.isConnected()) {
            ready = syncQuietly();
        } else {
            ready = connectAndInit();
        }

        if (ready) {
            locked = false;
            msg.DATABASE_TOGGLE.message(
                    Placeholder.unparsed("state", String.valueOf(locked))
            ).console();
        } else {
            msg.DATABASE_STILL_DISCONNECTED.message().console();
        }

        return getDBState();
    }

    /**
     * Saves everything, closes the database and opens it again. Also resets the SQLITE fallback,
     * so the configured type (e.g. MY_SQL) is tried again. Call plugin.reloadConfig() before it
     * if the config was edited.
     * <p>
     * Players are locked out while it runs. Data of online players is written to the (possibly new)
     * data source after reconnecting. Offline players' data stays in the previous data source.
     * If the database cannot be opened, mechanics stay locked (fix it, then use {@link #toggleDB()}).
     *
     * @return the state after reloading
     */
    public DBState reloadDB() {
        boolean wasLocked = locked;
        locked = true; // block players while the connection is swapped

        saveAll();
        dbConnection.close();

        if (connectAndInit()) {
            // Cache is the source of truth for online players, so write it to the new connection
            //armoryManager.markAllDirty();
            //armoryManager.saveAll();

            locked = wasLocked;
            msg.DATABASE_RELOAD.message().console();
        } else {
            msg.DATABASE_RELOAD_ERROR.message().console();
        }

        return getDBState();
    }

    public DBState getDBState() {
        if (locked) return DBState.DISABLED;
        return dbConnection.isConnected() ? DBState.CONNECTED : DBState.DISCONNECTED;
    }

    /**
     * True when players may use the mechanics (connected and not locked).
     */
    public boolean isUsable() {
        return getDBState() == DBState.CONNECTED;
    }

    private boolean syncQuietly() {
        /*
        try {
            armoryManager.syncSlots();
            return true;
        } catch (SQLException e) {
            msg.DATABASE_SYNC_ERROR.message(
                    Placeholder.unparsed("e", Arrays.toString(e.getStackTrace()))
            ).console();
            return false;
        }
         */

        return false;
    }

    // ======================================= GETTERS

    public DBConnection dbConnection() {
        return dbConnection;
    }
}
