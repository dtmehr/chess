package dataaccess;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SqlDataAccess implements DataAccess{
    private Map<String, AuthData> authTokens = new HashMap<>();
    private final Gson gson = new GsonBuilder().create();

    public SqlDataAccess() throws DataAccessException {
        configureDatabase();
    }

    //used for register on UserService
    //changed functionality from MemoryDataAccess to actually work for sql stuff
    public void forceAuth(String username, String token) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            String sql = """
            INSERT INTO auth (token, username)
            VALUES (?, ?)
            ON DUPLICATE KEY UPDATE username=VALUES(username)
        """;
            try (var ps = conn.prepareStatement(sql)) {
                ps.setString(1, token);
                ps.setString(2, username);
                ps.executeUpdate();
            }
        } catch (SQLException e) {
            throw new DataAccessException("forceAuth() error: " + e.getMessage());
        }
    }

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


    void storeUserPassword(String username, String clearTextPassword) {
        String hashedPassword = BCrypt.hashpw(clearTextPassword, BCrypt.gensalt());

        // write the hashed password in database along with the user's other information
        writeHashedPasswordToDatabase(username, hashedPassword);
    }

    private void writeHashedPasswordToDatabase(String username, String hashedPassword) {
    }

    boolean verifyUser(String username, String providedClearTextPassword) throws DataAccessException {
        // read the previously hashed password from the database
        var hashedPassword = readHashedPasswordFromDatabase(username);

        return BCrypt.checkpw(providedClearTextPassword, hashedPassword);
    }
//not sure about thsi one yet
    private String readHashedPasswordFromDatabase(String username) throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()) {
            String sql = "SELECT password FROM user WHERE username = ?";
            try (var ps = conn.prepareStatement(sql)) {
                ps.setString(1, username);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return rs.getString("password");
                    }
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Error reading hashed password: " + e.getMessage());
        }
        return null;
    }

    private void configureDatabase() throws DataAccessException{
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()){
            for (var statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException exception){
            throw new DataAccessException ("Unable to configure database: " + exception.getMessage());
        }
    }

    @Override
    public void clear() throws DataAccessException {
        try (var conn = DatabaseManager.getConnection()){
            try (var ps = conn.prepareStatement("DELETE FROM user")) {
                ps.executeUpdate();
            }
            try (var ps = conn.prepareStatement("DELETE FROM auth")) {
                ps.executeUpdate();
            }
            try (var ps = conn.prepareStatement("DELETE FROM game")) {
                ps.executeUpdate();
            }

        } catch (SQLException exception){
            throw new DataAccessException("clear() fail" + exception.getMessage());
        }
    }

    @Override
    public void createUser(String username, String password, String email) throws DataAccessException {
        // check if username is taken
        try (var connection = DatabaseManager.getConnection()) {
            String checkSql = "SELECT username FROM user WHERE username = ?";
            try (var psCheck = connection.prepareStatement(checkSql)) {
                psCheck.setString(1, username);
                try (var rs = psCheck.executeQuery()) {
                    if (rs.next()) {
                        throw new DataAccessException("Username is already taken");
                    }
                }
            }
        // hash password
            String hashedPassword = BCrypt.hashpw(password, BCrypt.gensalt());

            // insert the new user
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
        // used this in og file
        if (username == null || password == null || username.isEmpty() || password.isEmpty()) {
            throw new DataAccessException("Wrong username or password");
        }
        try (var conn = DatabaseManager.getConnection()) {
            String sql = "SELECT password, email FROM user WHERE username = ?";
            try (var ps = conn.prepareStatement(sql)) {
                ps.setString(1, username);

                try (var rs = ps.executeQuery()) {
                    if (!rs.next()) {
                        throw new DataAccessException("Wrong username or password");
                    }
                    String hashedPassword = rs.getString("password");

                    if (!org.mindrot.jbcrypt.BCrypt.checkpw(password, hashedPassword)) {
                        throw new DataAccessException("Wrong username or password");
                    }
                }
            }

            String token = service.AuthTokenGen.genAuthToken();
            String insertAuthSQL = "INSERT INTO auth (token, username) VALUES (?, ?)";

            try (var psInsert = conn.prepareStatement(insertAuthSQL)) {
                psInsert.setString(1, token);
                psInsert.setString(2, username);
                psInsert.executeUpdate();
            }
            return token;
        } catch (SQLException e) {
            throw new DataAccessException("Login failed: " + e.getMessage());
        }
    }


    @Override
    public boolean logout(String authToken) throws DataAccessException {
        try (var connection = DatabaseManager.getConnection()) {
            String sql = "DELETE FROM auth WHERE token = ?";
            try (var ps = connection.prepareStatement(sql)) {
                ps.setString(1, authToken);
                int rowsAffected = ps.executeUpdate();
                if (rowsAffected == 0) {
                    throw new DataAccessException("Token not found. Logout failed.");
                }
                return true;
            }
        } catch (SQLException exception) {
            throw new DataAccessException("logout() error: " + exception.getMessage());
        }
    }

    @Override
    public int createGame(String makerToken, String gameID) throws DataAccessException {
        return 0;
    }

    @Override
    public void joinGame(int gameID, String authToken, String teamColor) throws DataAccessException {

    }

    @Override
    public AuthData getAuthData(String token) {
        return null;
    }

    @Override
    public Collection<GameData> listGames() throws DataAccessException {
        return List.of();
    }
        //create authtoken
        //insert authtoken
        //catch any problems
    }


