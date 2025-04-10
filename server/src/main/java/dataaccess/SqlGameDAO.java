package dataaccess;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.AuthData;
import model.GameData;
import org.mindrot.jbcrypt.BCrypt;
import java.sql.SQLException;
import java.util.*;

public class SqlGameDAO implements GameDAO {
    private final Gson gson = new GsonBuilder().create();

    public SqlGameDAO() throws DataAccessException {
        configureDatabase();
    }

    //used for register on UserService
    //changed functionality from MemoryDataAccess to actually work for sql stuff
    public void forceAuth(String username, String token) throws DataAccessException {
        try (var connection = DatabaseManager.getConnection()) {
            String sql = """
            INSERT INTO auth (token, username)
            VALUES (?, ?)
            ON DUPLICATE KEY UPDATE username=VALUES(username)
        """;
            try (var statement = connection.prepareStatement(sql)) {
                statement.setString(1, token);
                statement.setString(2, username);
                statement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException("forceAuth() error: " + e.getMessage());
        }
    }

    //table setup
    private final String[] createStatements = {

            """
    CREATE TABLE IF NOT EXISTS user(
      username VARCHAR(255) NOT NULL,
      password VARCHAR(255) NOT NULL,
      email VARCHAR(255),
      PRIMARY KEY (username)
    )
    """,


            """
    CREATE TABLE IF NOT EXISTS auth(
      token VARCHAR(255) NOT NULL,
      username VARCHAR(255) NOT NULL,
      PRIMARY KEY (token),
      FOREIGN KEY (username) REFERENCES user (username)
        ON DELETE CASCADE
        ON UPDATE CASCADE
    )
    """,

            """
    CREATE TABLE IF NOT EXISTS game(
      game_id INT NOT NULL AUTO_INCREMENT,
      game_json TEXT NOT NULL,
      PRIMARY KEY (game_id)
    )
    """
    };


    private void configureDatabase() throws DataAccessException{
        DatabaseManager.createDatabase();
        try (var connection = DatabaseManager.getConnection()){
            for (var statement : createStatements) {
                try (var preparedStatement = connection.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException exception){
            throw new DataAccessException ("Unable to configure database: " + exception.getMessage());
        }
    }

//general notes
    //mirror already made code to pass tests
    //start with try (var conn = DatabaseManager.getConnection())
    //lots of try catch in each, depending on the function
    //

 //major changes
    @Override
    public void clear() throws DataAccessException {
        try (var connection = DatabaseManager.getConnection()) {
            try (var statement = connection.createStatement()) {
                statement.execute("SET FOREIGN_KEY_CHECKS = 0");
            }
            String tables = "SELECT table_name FROM information_schema.tables WHERE table_schema = DATABASE()";
            try (var statementTables = connection.createStatement();
                 var resultTables = statementTables.executeQuery(tables)) {
                while (resultTables.next()) {
                    String tableName = resultTables.getString(1);
                    String truncateSql = "TRUNCATE TABLE " + tableName;
                    try (var stmtTruncate = connection.createStatement()) {
                        stmtTruncate.execute(truncateSql);
                    }
                }
            }
            try (var statement = connection.createStatement()) {
                statement.execute("SET FOREIGN_KEY_CHECKS = 1");
            }
        } catch (SQLException exception) {
            throw new DataAccessException("clear() fail: " + exception.getMessage());
        }
    }


    @Override
    public void createUser(String username, String password, String email) throws DataAccessException {
        // check if username is taken
        try (var connection = DatabaseManager.getConnection()) {
            String checkSql = "SELECT username FROM user WHERE username = ?";
            try (var check = connection.prepareStatement(checkSql)) {
                check.setString(1, username);
                try (var results = check.executeQuery()) {
                    if (results.next()) {
                        // Changed the error message to exactly "username taken"
                        throw new DataAccessException("username taken");
                    }
                }
            }
            // else hash password
            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

            // and insert the new user with given values
            String insertSql = "INSERT INTO user (username, password, email) VALUES (?,?,?)";
            try (var psInsert = connection.prepareStatement(insertSql)) {
                psInsert.setString(1, username);
                psInsert.setString(2, hashedPassword);
                psInsert.setString(3, email);
                psInsert.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException("createUser() failed: " + e.getMessage());
        }
    }


    @Override
    public String login(String username, String password) throws DataAccessException {
        // used this in other file. checks for random edge cases
        if (username == null || password == null || username.isEmpty() || password.isEmpty()) {
            throw new DataAccessException("Wrong username or password");
        }
        //connect and check if username exists
        try (var connection = DatabaseManager.getConnection()) {
            String sql = "SELECT password, email FROM user WHERE username = ?";
            try (var statement = connection.prepareStatement(sql)) {
                statement.setString(1, username);

                try (var results = statement.executeQuery()) {
                    if (!results.next()) {
                        throw new DataAccessException("Wrong username or password");
                    }
                    //hash password
                    String hashedPassword = results.getString("password");

                    //make sure password matches up
                    if (!org.mindrot.jbcrypt.BCrypt.checkpw(password, hashedPassword)) {
                        throw new DataAccessException("Wrong username or password");
                    }
                }
            }

            String token = service.AuthTokenGen.genAuthToken();
            String insertAuthSQL = "INSERT INTO auth (token, username) VALUES (?, ?)";
            try (var insert = connection.prepareStatement(insertAuthSQL)) {
                //token and username
                insert.setString(1, token);
                insert.setString(2, username);
                insert.executeUpdate();
            }
            return token;
        } catch (SQLException exception) {
            throw new DataAccessException("login fail" + exception.getMessage());
        }
    }


    @Override
    public boolean logout(String authToken) throws DataAccessException {
        //delete token from auth
        try (var connection = DatabaseManager.getConnection()) {
            String sql = "DELETE FROM auth WHERE token = ?";
            try (var statement = connection.prepareStatement(sql)) {
                statement.setString(1, authToken);
                int rows = statement.executeUpdate();
                //rows should most likely be 1
                if (rows == 0) {
                    throw new DataAccessException("Token not found");
                }
                return true;
            }
            //anu other errors
        } catch (SQLException exception) {
            throw new DataAccessException("error with logout()" + exception.getMessage());
        }
    }
    //major rework changes
    @Override
    public int createGame(String makerToken, String gameName) throws DataAccessException {
        //check not null
        AuthData authData = getAuthData(makerToken);
        if (authData == null) {
            throw new DataAccessException("currently null");
        }
        // temp id
        GameData newGame = new GameData(0);
        newGame.setGameName(gameName);
//insert empty
        try (var connection = DatabaseManager.getConnection()) {
            String insertSql = "INSERT INTO game (game_json) VALUES (?)";
            try (var statement = connection.prepareStatement(insertSql)) {
                //basically insert
                statement.setString(1, gson.toJson(newGame));
                statement.executeUpdate();
            }
            //last inserted
            String selectSql = "SELECT LAST_INSERT_ID()";
            int generatedId;
            try (var selectStmt = connection.prepareStatement(selectSql);
                 var resultSet = selectStmt.executeQuery()) {
                if (!resultSet.next()) {
                    throw new DataAccessException("fail get gameid");
                }
                generatedId = resultSet.getInt(1);
            }
            newGame.setGameID(generatedId);
            String updatedJson = gson.toJson(newGame);
            String updateSql = "UPDATE game SET game_json = ? WHERE game_id = ?";
            try (var statement = connection.prepareStatement(updateSql)) {
                statement.setString(1, updatedJson);
                statement.setInt(2, generatedId);
                statement.executeUpdate();
            }
            return generatedId;
        } catch (SQLException exception) {
            throw new DataAccessException("createGame() error" + exception.getMessage());
        }
    }

    @Override
    public void joinGame(int gameID, String authToken, String teamColor) throws DataAccessException {
        // check auth
        AuthData authData = getAuthData(authToken);
        if (authData == null) {
            throw new DataAccessException("unauthorized");
        }
        // check team color
        if (!("WHITE".equals(teamColor) || "BLACK".equals(teamColor))) {
            throw new DataAccessException("bad request");
        }
        // grab the game
        try (var conn = DatabaseManager.getConnection()) {
            String selectSql = "SELECT game_json FROM game WHERE game_id = ?";
            try (var select = conn.prepareStatement(selectSql)) {
                select.setInt(1, gameID);
                try (var results = select.executeQuery()) {
                    if (!results.next()) {
                        throw new DataAccessException("bad request");
                    }
                    String gameJson = results.getString("game_json");
                    GameData gameData = gson.fromJson(gameJson, GameData.class);

                    // team taken?
                    if ("WHITE".equals(teamColor) && gameData.getWhiteUsername() != null && !gameData.getWhiteUsername().isEmpty()) {
                        throw new DataAccessException("already taken");
                    }
                    if ("BLACK".equals(teamColor) && gameData.getBlackUsername() != null && !gameData.getBlackUsername().isEmpty()) {
                        throw new DataAccessException("already taken");
                    }

                    // assign color
                    if ("WHITE".equals(teamColor)) {
                        gameData.setWhiteUsername(authData.getUsername());
                    } else {
                        gameData.setBlackUsername(authData.getUsername());
                    }
                    String updateSql = "UPDATE game SET game_json = ? WHERE game_id = ?";
                    try (var psUpdate = conn.prepareStatement(updateSql)) {
                        psUpdate.setString(1, gson.toJson(gameData));
                        psUpdate.setInt(2, gameID);
                        psUpdate.executeUpdate();
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("joinGame() error" + e.getMessage());
        }
    }


    //same patter as before btu for sql
    @Override
    public AuthData getAuthData(String token) throws DataAccessException {
        try (var connection = DatabaseManager.getConnection()) {
            String sql = "SELECT username FROM auth WHERE token = ?";
            try (var statement = connection.prepareStatement(sql)) {
                statement.setString(1, token);
                try (var results = statement.executeQuery()) {
                    if (results.next()) {
                        String username = results.getString("username");
                        return new AuthData(username, token);
                    }
                }
            }
        } catch (SQLException | DataAccessException exception) {
            throw new DataAccessException("error with getAuthData" + exception.getMessage());
        }
        return null;
    }

//list games but for sql
    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        List<GameData> allGames = new ArrayList<>();
        String sql = "SELECT game_json FROM game";
        try (var connection = DatabaseManager.getConnection();
             var statement = connection.prepareStatement(sql);
             var result = statement.executeQuery()) {

            while (result.next()) {
                String gameJson = result.getString("game_json");
                GameData gameData = gson.fromJson(gameJson, GameData.class);
                allGames.add(gameData);
            }

        } catch (SQLException e) {
            throw new DataAccessException("listGames() fail" + e.getMessage());
        }
        return allGames;
    }

}


