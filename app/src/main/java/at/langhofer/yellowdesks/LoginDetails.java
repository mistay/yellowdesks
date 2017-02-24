package at.langhofer.yellowdesks;

/**
 * Created by arminlanghofer on 12.02.17.
 */

public class LoginDetails {
    public String username;
    public String password;
    public String firstname;
    public String lastname;

    public void debug() {
        System.out.println("login: " + username + " password: " + password + " firstname: " + firstname + " lastname: " + lastname );
    }

}
