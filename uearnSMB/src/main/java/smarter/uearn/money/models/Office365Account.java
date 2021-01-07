package smarter.uearn.money.models;

/**
 * Created by kavya on 10/13/2015.
 */
public class Office365Account {

    private String user_id;
    private String id = "";
    private String email = "";
    private boolean isenable;
    /*private String accessToken = "";
    private String refreshToken = "";*/
    private boolean googleCalendarEnable = false;
    public boolean isPrimary;


    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean getIsEnable() {
        return isenable;
    }

    //CODE CLEANUP :::KSN
    /*public void setIsEnable(boolean enable) {
        this.isenable = enable;
    }

    public String getRefreshToken() { return refreshToken; }

    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }

    public String getAccessToken() { return accessToken;}

    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }*/ //:::KSN

    public boolean getIsGoogleCalendarEnable()  { return googleCalendarEnable; }

    //CODE CLEANUP :::KSN
   /* public void setGoogleCalendarEnable(boolean googleCalendarEnable) { this.googleCalendarEnable = googleCalendarEnable; }
*/ //:::KSN
    public void setIsPrimary(boolean primary) { this.isPrimary = primary; }

    //CODE CLEANUP :::KSN
    /*public boolean getisPrimary() { return  isPrimary; }*/

}
