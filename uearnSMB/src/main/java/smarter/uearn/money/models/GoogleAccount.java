package smarter.uearn.money.models;

/**
 * Created by kavya on 10/13/2015.
 */
public class GoogleAccount {

    private String user_id;
    private String id = "";
    private String email = "";
    private boolean isenable;
    private String refreshToken = "";
    private String accessToken = "";
    private boolean googleCalendarEnable = false;
    public String devicekey;
    public String typeOfAccount;
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

    public void setIsEnable(boolean enable) {
        this.isenable = enable;
    }

    public String getRefreshToken() { return refreshToken; }

    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }

    public String getAccessToken() { return accessToken;}

    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }

    public boolean getIsGoogleCalendarEnable()  { return googleCalendarEnable; }

    public void setGoogleCalendarEnable(boolean googleCalendarEnable) { this.googleCalendarEnable = googleCalendarEnable; }

    public void setDevicekey(String devicekey) { this.devicekey = devicekey; }

    public String getDevicekey() {
        return devicekey;
    }

    public void setIsPrimary(boolean primary) { this.isPrimary = primary; }

    public boolean getisPrimary() { return  isPrimary; }

    public String getTypeOfAccount() {
        return typeOfAccount;
    }

    public void setTypeOfAccount(String typeOfAccount) {
        this.typeOfAccount = typeOfAccount;
    }
}
