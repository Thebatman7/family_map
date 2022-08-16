package Service.Result;

public class FillResult {
    private String message;
    private boolean success;

    //constructor
    public FillResult() {}
    //parameterized constructor
    public FillResult(String message, boolean success) {
        this.message = message;
        this.success = success;
    }

    public String getMessage() { return message; }

    public void setMessage(String message) { this.message = message; }

    public boolean getSuccess() { return success; }

    public void setSuccess(boolean success) { this.success = success; }
}
