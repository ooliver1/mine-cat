package tootymc;

import java.io.File;
import java.util.HashMap;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.DriverManager;
import java.util.logging.Logger;
import java.util.concurrent.CompletableFuture;

public class Players {
    private Logger logger;
    private File dataFolder;
    private Connection conn = null;
    private Statement statement = null;
    private static final String databaseProtocol = "jdbc:sqlite";
    private HashMap<String, String> uuidToDiscord = new HashMap<String, String>();
    private HashMap<String, String> discordToUuid = new HashMap<String, String>();

    public Players(Tooty plugin) {
        this.logger = plugin.getLogger();
        this.dataFolder = plugin.getDataFolder();
        if (!dataFolder.exists()) {
            dataFolder.mkdir();
        }
        String databaseUri = databaseProtocol + ":" + dataFolder.getPath() + "/database.db";
        logger.info("Connecting to database with uri: " + databaseUri);
        try {
            this.conn = DriverManager.getConnection(databaseUri);
            this.statement = conn.createStatement();
            statement.setQueryTimeout(30);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        CompletableFuture.supplyAsync(() -> {
            try {
                statement.executeUpdate(
                        "CREATE TABLE IF NOT EXISTS players (id VARCHAR, uuid VARCHAR)");
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return "Database table created if not exists!";
        }).thenAccept(result -> {
            logger.info(result);
            return;
        });
    }

    public String getDiscordId(String uuid) {
        return uuidToDiscord.get(uuid);
    }

    public String getUuid(String id) {
        return discordToUuid.get(id);
    }

    public void addPlayer(String uuid, String id) {
        CompletableFuture.supplyAsync(() -> {
            try {
                statement.executeUpdate(
                        String.format("INSERT INTO players (id, uuid) VALUES (%s, %s)", id, uuid));
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return String.format("Player with id %s and uuid %s added to database!");
        }).thenAccept(result -> {
            logger.info(result);
            return;
        });
    }

    public void close() throws SQLException {
        if (conn != null)
            conn.close();
    }
}
