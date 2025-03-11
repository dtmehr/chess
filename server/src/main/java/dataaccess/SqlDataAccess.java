package dataaccess;

import passoff.exception.ResponseParseException;
import spark.Response;

import java.sql.SQLException;

public class SqlDataAccess implements DataAccess{
    public SqlDataAccess() throws ResponseException {

    }

    private void configureDatabase() throws ResponseException{
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()){
            for (var statement : createStatements) {
                try (var preparedStatement = con.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException exception){
            throw new ResponseException (500, String.format("Unable to configure database %s", exception));
        }

    }
}
