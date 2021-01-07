package smarter.uearn.money.models;

/**
 * Created on 09/06/17.
 *
 * @author dilipkumar4813
 * @version 1.0
 */

public class PersonalJunkContact {

    private String type = "";
    private String phoneNumber = "";
    private String status = "";
    private String name = "";

    public PersonalJunkContact() {

    }

    public PersonalJunkContact(int itemType, String number) {
        if (itemType == 0) {
            this.type = "mypersonal";
        } else {
            this.type = "myjunk";
        }

        this.phoneNumber = number;
    }

    public PersonalJunkContact(String nameNum, String number) {
        this.name = nameNum;
        this.phoneNumber = number;
        this.type = "mypersonal";
    }

    public PersonalJunkContact(String number, int stat) {
        this.phoneNumber = number;

        if (stat == 0) {
            this.status = "unblocked";
        } else {
            this.status = "blocked";
        }

        this.type = "junk";
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getType() {
        return type;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getStatus() {
        return status;
    }
}
