package service;

import dataaccess.DataAccessException;
import dataaccess.SqlDataAccess;
import org.junit.jupiter.api.*;

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
}
