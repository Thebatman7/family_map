package Service;

import DAO.DataAccessException;
import DAO.Database;
import Service.Result.ClearResult;
import Service.Result.SingleEventResult;

public class Clear {

    /**
     * Deletes all data from the Database, including user accounts, authorization tokens, generated person and event data
     */
    public ClearResult deleteAll() throws DataAccessException {
        boolean commit = false;
        Database db = new Database();
        ClearResult result = new ClearResult();
        try {
            db.getConnection();
            db.clearTables();
            result.setSuccess(true);
            result.setMessage("Clear succeeded.");
            commit = true;
        }
        catch(DataAccessException dbException) {
            result.setSuccess(false);
            result.setMessage("Error. Deleting all data from Database was unsuccessful.");
            dbException.printStackTrace();
        }
        //we close connection
        db.closeConnection(commit);
        return result;
    }
}
