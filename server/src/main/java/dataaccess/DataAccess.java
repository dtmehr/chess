package dataaccess;

import model.AuthData;
import model.UserData;
import model.GameData;

//will include CRUD stuff here
public interface DataAccess {

    void clear() throws DataAccessException;
}
