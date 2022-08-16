package Service.Request;

public class FillRequest {
    private String username;
    private int generations;

    //default constructor
    public FillRequest() {
        generations = 4;
    }
    //constructor with only username
    public FillRequest(String username) {
        this.username = username;
        this.generations = 4;
    }
    //parameterized constructor
    public FillRequest(String username, int generations) {
        this.username = username;
        this.generations = generations;
    }

    public String getUsername() { return username; }

    public void setUsername(String username) { this.username = username; }

    public int getGenerations() { return generations; }

    public void setGenerations(int generations) { this.generations = generations; }
}
