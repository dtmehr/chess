package dataaccess;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import passoff.exception.ResponseParseException;
import spark.Response;

import java.sql.SQLException;

public class SqlDataAccess implements DataAccess{
    private final Gson gson = new GsonBuilder().create();

    public SqlDataAccess() throws DataAccessException {
        configureDatabase();
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS user(
            username VARCHAR(255) NOT NULL,
            password VARCHAR(255) NOT NULL,
            PRIMARY KEY (username)
            
            CREATE TABLE IF NOT EXISTS `auth` (
              token    VARCHAR(255) NOT NULL,
              username VARCHAR(255) NOT NULL,
              PRIMARY KEY (token),
              FOREIGN KEY (username) REFERENCES `user` (username)
                ON DELETE CASCADE
                ON UPDATE CASCADE
            
            CREATE TABLE IF NOT EXISTS `game` (
              game_id   INT NOT NULL AUTO_INCREMENT,
              game_json TEXT NOT NULL,
              PRIMARY KEY (game_id)
            """

    };

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
}
