package smarter.uearn.money.models;


public class ChangePasswordInfo {
    public String user_id;
    public String oldPassword;
    public String newPassword;
    public String confirmPassword;

    public ChangePasswordInfo(String user_id, String oldPassword, String newPassword, String confirmPassword) {
        this.user_id = user_id;
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
        this.confirmPassword = confirmPassword;
    }
}
