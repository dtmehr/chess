package dataaccess;

import model.AuthData;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import static dataaccess.DatabaseManager.getConnection;

public class SqlAuthDAO implements AuthDAO {
//    added because stuff wasn't working right
//    very similar to other stuff made in dif phases
    @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
//        try block
        try (Connection connection = getConnection()) {
//            check
            String sql = "SELECT username FROM auth WHERE token = ?";
            try (PreparedStatement statement = connection.prepareStatement(sql)) {
                statement.setString(1, authToken);

                try (ResultSet results = statement.executeQuery()) {

                    if (results.next()) {
                        AuthData authData = new AuthData();
                        authData.setUsername(results.getString("username"));
                        return authData;
                    }
                    return null;
                }
            }
//            throw error similar to always
        } catch (SQLException e) {
            throw new DataAccessException("SqlAuthDAO error: " + e.getMessage());
        }
    }
//clear methods just in case, not sure if needed tbh
    @Override
    public void clear() throws DataAccessException {
    }
}
