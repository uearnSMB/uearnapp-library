package smarter.uearn.money.models;

/**
 * Created by Bala on 6/30/2015.
 */
public class LoginInfo {
    public String userName;
    public String password;
    public String loginMethod;

    public LoginInfo(String userName, String password, String loginMethod) {
        this.userName = userName;
        this.password = password;
        this.loginMethod = loginMethod;
    }
}
