package dataaccess;

import model.AuthData;

public interface AuthDAO {
    //add more if needed
    //better org
    AuthData getAuth(String authToken) throws DataAccessException;
    void clear() throws DataAccessException;

}
