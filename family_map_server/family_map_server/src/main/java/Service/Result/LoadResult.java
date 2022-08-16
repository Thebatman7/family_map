package Service.Result;

public class LoadResult {
    private String message;
    private boolean success;

    //constructor
    public LoadResult() {}
    //parameterized constructor
    public LoadResult(String message, boolean success) {
        this.message = message;
        this.success = success;
    }

    public String getMessage() { return message; }

    public void setMessage(String message) { this.message = message; }

    public boolean getSuccess() { return success; }

    public void setSuccess(boolean success) { this.success = success;
    }
}
