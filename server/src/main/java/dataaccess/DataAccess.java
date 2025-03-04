package dataaccess;

//will include CRUD stuff here
public interface DataAccess {

    //user
    void clear() throws DataAccessException;
    void createUser();
    String login();
    void logout();

    //game
    void createGame();
    void joinGame();
    void listGames();

}

