package ServiceTests;

import DAO.DataAccessException;
import DAO.Database;
import Service.Clear;
import Service.Login;
import Service.Register;
import Service.Request.LoginRequest;
import Service.Request.RegisterRequest;
import Service.Result.ClearResult;
import Service.Result.LoginResult;
import Service.Result.RegisterResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ClearTest {
    private Database db;
    private Register register;
    private RegisterRequest registerRequest;
    private RegisterRequest registerRequest1;
    private RegisterResult registerResult;
    private RegisterResult registerResult1;
    private Login login;
    private LoginRequest loginRequest;
    private LoginRequest loginRequest1;
    private LoginResult loginResult;
    private LoginResult loginResult1;
    private Clear clear;
    private ClearResult result;

    @BeforeEach
    public void setUp() throws DataAccessException {
        registerRequest = new RegisterRequest("Ba7Man", "CaballeroOscuro", "Batman1@wayne.com",
                "Rembrand", "Pardo","M");
        registerRequest1 = new RegisterRequest("NightWing2", "AlmostBatman1", "Wing1@wayne.com",
                "Max", "Scheible","M");
        loginRequest = new LoginRequest("Ba7Man", "CaballeroOscuro");
        loginRequest1 = new LoginRequest("NightWing2", "AlmostBatman1");
        db = new Database();
        db.getConnection();
        db.clearTables();
        db.closeConnection(true);
        register = new Register();
        login = new Login();
        clear = new Clear();
    }

    @Test
    public void simpleClearingDB() throws DataAccessException {
        //we populate our database
        registerResult = register.newUserAccount(registerRequest);
        registerResult1 = register.newUserAccount(registerRequest1);
        loginResult = login.loginUser(loginRequest);
        loginResult1 = login.loginUser(loginRequest1);
        //we check that indeed our database is not empty
        Assertions.assertEquals(true, registerResult.getSuccess());
        Assertions.assertEquals(true, registerResult1.getSuccess());
        Assertions.assertEquals(true, loginResult.getSuccess());
        Assertions.assertEquals(true, loginResult1.getSuccess());
        //we clear out database
        result = clear.deleteAll();
        Assertions.assertEquals(true, result.getSuccess());
        System.out.println(result.getMessage());
    }

    @Test
    public void clearingDBTest() throws DataAccessException {
        //we clear out database
        registerResult = register.newUserAccount(registerRequest);
        registerResult1 = register.newUserAccount(registerRequest1);
        loginResult = login.loginUser(loginRequest);
        loginResult1 = login.loginUser(loginRequest1);
        //we check that indeed our database is not empty
        Assertions.assertEquals(true, registerResult.getSuccess());
        Assertions.assertEquals(true, registerResult1.getSuccess());
        Assertions.assertEquals(true, loginResult.getSuccess());
        Assertions.assertEquals(true, loginResult1.getSuccess());
        //since we already have those usernames we should not be able to register them again
        registerResult = register.newUserAccount(registerRequest);
        registerResult1 = register.newUserAccount(registerRequest1);
        //we check that indeed our database is not empty
        Assertions.assertEquals("Error: Username is already taken.", registerResult.getMessage());
        Assertions.assertEquals("Error: Username is already taken.", registerResult1.getMessage());
        //we clear DB
        result = clear.deleteAll();
        Assertions.assertEquals(true, result.getSuccess());
        System.out.println(result.getMessage());
        //after clearing we should be able to repopulate it with the same values without any problem
        registerResult = register.newUserAccount(registerRequest);
        registerResult1 = register.newUserAccount(registerRequest1);
    }
}
