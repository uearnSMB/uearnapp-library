package smarter.uearn.money.models;

/**
 * Created by dilipkumar4813 on 21/03/17.
 */

public class TeamStructure {

    String groupid, uid, email, role;

    public TeamStructure(String group, String id, String email, String role) {
        this.groupid = group;
        this.uid = id;
        this.email = email;
        this.role = role;
    }

    public String getGroupid() {
        return groupid;
    }

    public String getUid() {
        return uid;
    }

    public String getEmail() {
        return email;
    }

    public String getRole() {
        return role;
    }
}
