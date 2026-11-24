package dev.pewienkot.pluginTemplate.data;

import dev.pewienkot.pluginTemplate.PluginTemplate;
import dev.pewienkot.pluginTemplate.messages.Messages;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.configuration.ConfigurationSection;

import javax.naming.Name;
import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Locale;

public class DBConnection {

    private final PluginTemplate plugin;
    private final Messages msg;

    public enum DBType {
        MY_SQL,
        SQLITE
    }

    /**
     * True if, Intentional connection stop
     */
    private boolean closedManually = false;

    private Connection connection;
    private DBType type = DBType.SQLITE;

    public DBConnection(PluginTemplate plugin) {
        this.plugin = plugin;
        this.msg = plugin.messages();

        type = parseDBType(plugin.getConfig().getString("database.type"));
    }

    // ====================================
    // ======================================= CONNECTING

    /**
     * Opens the connection using values from config.
     * MY_SQL failure switches to SQLITE and informs the console.
     *
     * @return true if any connection was established
     */
    public synchronized boolean connect() {
        closeQuietly();
        closedManually = false;

        ConfigurationSection section = plugin.getConfig().getConfigurationSection("database");
        if (section == null) msg.DATABASE_ERROR_CONFIG.message().console();

        return tryOpen(section) == null;
    }

    /**
     * @return StackTraceElement[]
     */
    private StackTraceElement[] tryOpen(ConfigurationSection section) {
        Exception exception;

        try {
            connection = switch (type) {
                case SQLITE -> openSQLite(section);
                case MY_SQL -> openMySql(section);
            };

            msg.DATABASE_CONNECTED.message(
                    Placeholder.unparsed("type", type.toString())
            ).console();
            return null;
        } catch (ClassNotFoundException e) {
            msg.DATABASE_ERROR_JDBC.message(
                    Placeholder.unparsed("type", type.toString())
            ).console();
            exception = e;
        } catch (SQLException e) {
            msg.DATABASE_ERROR_CONNECT.message(
                    Placeholder.unparsed("e", Arrays.toString(e.getStackTrace()))
            ).console();
            exception = e;
        }

        connection = null;
        return exception.getStackTrace();
    }

    private Connection openSQLite(ConfigurationSection section) throws ClassNotFoundException, SQLException {
        Class.forName("org.sqlite.JDBC");

        String fileName = section == null ? "database.db" : section.getString("sqlite.file", "database.db");
        File file = new File(plugin.getDataFolder(), fileName);

        File parent = file.getParentFile();
        if (parent != null && !parent.exists() && !parent.mkdirs()) {
            throw new SQLException("Could not create directory " + parent.getAbsolutePath());
        }

        return DriverManager.getConnection("jdbc:sqlite:" + file.getAbsolutePath());
    }

    private Connection openMySql(ConfigurationSection section) throws ClassNotFoundException, SQLException {
        Class.forName("com.mysql.cj.jdbc.Driver");

        String host = section == null ? "localhost" : section.getString("mysql.host", "localhost");
        int port = section == null ? 3306 : section.getInt("mysql.port", 3306);
        String database = section == null ? "armory" : section.getString("mysql.database", "armory");
        String user = section == null ? "root" : section.getString("mysql.username", "root");
        String password = section == null ? "" : section.getString("mysql.password", "");
        boolean ssl = section != null && section.getBoolean("mysql.use-ssl", false);

        String url = "jdbc:mysql://" + host + ":" + port + "/" + database
                + "?useSSL=" + ssl
                + "&characterEncoding=utf8"
                + "&autoReconnect=true";

        return DriverManager.getConnection(url, user, password);
    }

    private DBType parseDBType(String raw) {
        if (raw == null) return DBType.SQLITE;
        try {
            return DBType.valueOf(raw.trim().toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
            msg.DATABASE_ERROR_TYPE.message(
                    Placeholder.unparsed("type", raw)
            ).console();
            return DBType.SQLITE;
        }
    }

    // ======================================= STATE

    public synchronized boolean isConnected() {
        try {
            return connection != null && !connection.isClosed() && connection.isValid(2);
        } catch (SQLException e) {
            return false;
        }
    }

    /**
     * Returns the live connection. Tries to reconnect if the link was lost,
     */
    public synchronized Connection getConnection() {
        if (!isConnected()) {
            if (closedManually || !connect()) {
                plugin.fatalErrorStop(Component.text("Database isn't connected", NamedTextColor.RED));
                return null;
            }
        }
        return connection;
    }

    public DBType getDBType() {
        return type;
    }

    // ======================================= CLOSING

    public synchronized void close() {
        closedManually = true;
        if (connection != null) {
            closeQuietly();
            msg.DATABASE_DISCONNECTED.message().console();
        }
    }

    private void closeQuietly() {
        if (connection == null) return;
        try {
            connection.close();
        } catch (SQLException ignored) {
            // nothing more we can do here
        }
        connection = null;
    }

}
