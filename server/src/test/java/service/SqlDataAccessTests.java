package service;

import dataaccess.DataAccessException;
import dataaccess.SqlDataAccess;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class SqlDataAccessTests {
    private SqlDataAccess dao;

    @BeforeEach
    void setup() throws DataAccessException {
        dao = new SqlDataAccess();
        dao.clear();
    }

    @Test
    void clearTest_Positive() throws DataAccessException {

    }

    @Test
    void clearTest_Negative() throws DataAccessException {

    }

    @Test
    void createUserTest_Positive() throws DataAccessException {
        dao.createUser("jimmer", "32", "jimmer@email.com");
        String test = dao.login("jimmer", "32");
        assertNotNull(test);
    }

    @Test
    void createUserTest_Negative() throws DataAccessException{
        dao.createUser("jimmer", "32", "jimmer@email.com");
        assertThrows(DataAccessException.class, () -> {
            dao.createUser("jimmer", "332", "jimmer32@gmail.com");
        });


    }
}
