package at.langhofer.yellowdesks;

/**
 * Created by arminlanghofer on 12.02.17.
 */

public class LoginDetails {
    static String username;
    static String password;
    static String firstname;
    static String lastname;

    public static void debug() {
        System.out.println("login: " + username + " password: " + password + " firstname: " + firstname + " lastname: " + lastname );
    }

}
