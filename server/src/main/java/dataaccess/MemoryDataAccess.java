package dataaccess;

import java.util.Map;
import java.util.HashMap;

import model.AuthData;
import model.GameData;
import model.UserData;

public class MemoryDataAccess implements DataAccess{

    private Map<String, UserData> users = new HashMap<>();
    private Map<String, AuthData> authTokens = new HashMap<>();
    private Map<Integer, GameData> games = new HashMap<>();

    //methods
    @Override
    public void clear() throws DataAccessException {
        try {
            users.clear();
            authTokens.clear();
            games.clear();
        } catch (Exception e) {
            throw new DataAccessException("Unable to clear data");
        }
    }

    @Override
    public void createUser() {

    }

//    @Override
//    public void login() throws DataAccessException{
//        try {
//            //to do
//        } catch (Exception e) {
//            throw new DataAccessException("Unable to clear data", e);
//        }
//
//    }
//
//    @Override
//    public void logout() throws DataAccessException{
//        try {
//            //to do
//        } catch (Exception e) {
//            throw new DataAccessException("Unable to clear data", e);
//        }
//
//    }
//
//    @Override
//    public void listGames() throws DataAccessException{
//        try {
//            //to do
//        } catch (Exception e) {
//            throw new DataAccessException("Unable to clear data", e);
//        }
//
//    }
//
//    @Override
//    public void createGame() throws DataAccessException{
//        try {
//            //to do
//        } catch (Exception e) {
//            throw new DataAccessException("Unable to clear data", e);
//        }
//
//    }
//
//    @Override
//    public void joinGame() throws DataAccessException{
//        try {
//            //to do
//        } catch (Exception e) {
//            throw new DataAccessException("Unable to clear data", e);
//        }
//
//    }


}
