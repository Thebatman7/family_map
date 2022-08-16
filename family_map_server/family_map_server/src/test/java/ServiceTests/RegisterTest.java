package ServiceTests;

import DAO.DataAccessException;
import DAO.Database;
import Service.Register;
import Service.Request.RegisterRequest;
import Service.Result.RegisterResult;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class RegisterTest {
    private Database db;
    private Register register;
    private RegisterRequest request;
    private RegisterRequest request1;
    private RegisterResult result;
    private RegisterResult result1;


    @BeforeEach
    public void setUp() throws DataAccessException {
        request = new RegisterRequest("Ba7Man", "CaballeroOscuro", "Batman1@wayne.com",
                "Rembrand", "Pardo","M");
        request1 = new RegisterRequest("Ba7Man", "CaballeroOscuro", "Batman1@wayne.com",
                "Rembrand", "Pardo","M");
        register = new Register();
        db = new Database();
        db.getConnection();
        db.clearTables();
        db.closeConnection(true);
    }

    @Test
    public void insertingUserTestPass() {
        result = register.newUserAccount(request);
        Assertions.assertEquals(true, result.getSuccess());
        Assertions.assertNotNull(result.getAuthToken());
    }

    @Test
    public void insertingUserTestFail() {
        result = register.newUserAccount(request);
        Assertions.assertEquals(true, result.getSuccess());
        result1 = register.newUserAccount(request1);
        Assertions.assertEquals(false, result1.getSuccess());
        System.out.println("This is the message: " + result1.getMessage());
    }

}
