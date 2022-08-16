package ServiceTests;

import DAO.DataAccessException;
import DAO.Database;
import Service.Login;
import Service.Register;
import Service.Request.LoginRequest;
import Service.Request.RegisterRequest;
import Service.Result.LoginResult;
import Service.Result.RegisterResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class LoginTest {
    private Database db;
    private Login login;
    private LoginRequest request;
    private LoginRequest request1;
    private LoginResult result;
    private LoginResult result1;
    private Register register;
    private RegisterRequest registerRequest;
    private RegisterResult registerResult;

    @BeforeEach
    public void setUp() throws DataAccessException {
        request = new LoginRequest("Ba7Man", "CaballeroOscuro");
        request1 = new LoginRequest("NightWing", "AlmostBatman1");
        registerRequest = new RegisterRequest("Ba7Man", "CaballeroOscuro", "Batman1@wayne.com",
                "Rembrand", "Pardo","M");
        register = new Register();
        login = new Login();
        db = new Database();
        db.getConnection();
        db.clearTables();
        db.closeConnection(true);
    }

    @Test
    public void loggingUserPass() {
        registerResult = register.newUserAccount(registerRequest);
        Assertions.assertEquals(true, registerResult.getSuccess());
        result = login.loginUser(request);
        Assertions.assertEquals(true, result.getSuccess());
        Assertions.assertEquals("Ba7Man", result.getUsername());
        Assertions.assertNotEquals(null, result.getPersonID());
        Assertions.assertNull(result.getMessage());
    }

    @Test
    public void loggingUserFail() {
        result1 = login.loginUser(request1);
        Assertions.assertEquals(false, result1.getSuccess());
        System.out.println(result1.getMessage());
        registerResult = register.newUserAccount(registerRequest);
        Assertions.assertEquals(true, registerResult.getSuccess());
        result = login.loginUser(request);
        Assertions.assertEquals(true, result.getSuccess());
        result1 = login.loginUser(request1);
        Assertions.assertEquals(false, result1.getSuccess());
        Assertions.assertNotNull(result1.getMessage());
        System.out.println(result1.getMessage());
    }
}
