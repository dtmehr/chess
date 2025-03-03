package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;

public class UserService {
//    private final UserService userService;
    private final DataAccess dataAccess;

    public UserService(DataAccess dataAccess) {
        this.dataAccess = dataAccess;
    }

    public void clear() throws DataAccessException {
        dataAccess.clear();
    }
}
