package Model;//This package contains the classes to represent the tables in the database

/*this is a Model class to represent the AuthToken table in the database for the family server*/
public class AuthToken {
    private String authToken;
    private String username;

    //default constructor
    public AuthToken() {}
    //parameterized constructor
    public AuthToken(String authToken, String username) {
        this.authToken = authToken;
        this.username = username;
    }

    /**
     * Get the authorization token
     */
    public String getAuthToken() {
        return authToken;
    }

    /**
     * Set the authorization token
     */
    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    /**
     * Get the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Set the username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null) return false;
        if (o instanceof AuthToken) {//instanceof is a marco
            AuthToken oAuthToken = (AuthToken)o;//we can't use it as an event object until we cast it even though it is of the AuthToken type
            return oAuthToken.getAuthToken().equals(this.getAuthToken()) &&
                    oAuthToken.getUsername().equals(this.getUsername());

        }
        else {
            return false;
        }
    }
}