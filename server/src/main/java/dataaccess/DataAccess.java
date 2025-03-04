package dataaccess;

//will include CRUD stuff here
public interface DataAccess {

    //user
    void clear() throws DataAccessException;
    void createUser(String username, String password, String email) throws DataAccessException;
    String login(String username, String password) throws DataAccessException;
    void logout(String authToken);

    //game
    int createGame(String makerToken);
    void joinGame(int gameID, String authToken, String teamColor);
    void listGames();

}

