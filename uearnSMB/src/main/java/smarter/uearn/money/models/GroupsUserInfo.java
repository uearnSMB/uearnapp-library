package smarter.uearn.money.models;

/**
 * Created by kavya on 11/24/2015.
 */
public class GroupsUserInfo {

    public String userId;
    public String dependent_type;
    public String phone;
    public String request;
    public String status;
    public String email;
    public String dependent_userId;
    public String name = "";
    public String role;
    public String pincode = "";
    public String state = "";
    public String city = "";
    public String country = "";
    public String region = "";

    public GroupsUserInfo() {
    }

    public GroupsUserInfo(String email, String name, String phone) {
        this.email = email;
        this.name = name;
        this.phone = phone;
    }

    public GroupsUserInfo(String email, String dependent_type, String phone, String request, String status) {
        this.email = email;
        this.dependent_type = dependent_type;
        this.phone = phone;
        this.request = request;
        this.status = status;
    }

    public GroupsUserInfo(String email, String dependent_type, String phone, String request, String status, String role) {
        this.email = email;
        this.dependent_type = dependent_type;
        this.phone = phone;
        this.request = request;
        this.status = status;
        this.role = role;
    }

    public GroupsUserInfo(String name, String email, String dependent_type, String phone,
                          String request, String status, String pincode,
                          String state, String city, String region, String country) {
        this.name = name;
        this.email = email;
        this.dependent_type = dependent_type;
        this.phone = phone;
        this.request = request;
        this.status = status;
        this.pincode = pincode;
        this.state = state;
        this.city = city;
        this.region = region;
        this.country = country;
    }

   /* public String getUserId() {
        return userId;
    }*/

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
       return name;
    }

    public void setDependent_userId(String dependent_userId) {
        this.dependent_userId = dependent_userId;
    }
}
