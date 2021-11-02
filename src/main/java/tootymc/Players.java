package tootymc;

import java.io.File;
import java.util.HashMap;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.DriverManager;
import java.util.logging.Logger;
import java.sql.PreparedStatement;
import java.util.concurrent.CompletableFuture;

public class Players {
    private Logger logger;
    private File dataFolder;
    private Connection conn = null;
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
        } catch (SQLException e) {
            e.printStackTrace();
        }
        this.initTable();
        this.getAndCache();
    }

    private void initTable() {
        CompletableFuture.supplyAsync(() -> {
            try (Statement statement = conn.createStatement()) {
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

    private void getAndCache() {
        CompletableFuture.supplyAsync(() -> {
            try (Statement statement = conn.createStatement()) {
                ResultSet rs = statement.executeQuery("SELECT * FROM players");
                while (rs.next()) {
                    String uuid = rs.getString("uuid");
                    String id = rs.getString("id");
                    this.discordToUuid.put(id, uuid);
                    this.uuidToDiscord.put(uuid, id);
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return "Database players cached in discordToUuid and uuidToDiscord";
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
        this.discordToUuid.put(id, uuid);
        this.uuidToDiscord.put(uuid, id);
        CompletableFuture.supplyAsync(() -> {
            try (
                PreparedStatement statement = conn.prepareStatement(
                    "INSERT INTO players (uuid, id) VALUES (?,?)")) {
                statement.setString(1, uuid);
                statement.setString(2, id);
                statement.executeUpdate();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return String.format(
                "Player with id %s and uuid %s added to database!", id, uuid);
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
