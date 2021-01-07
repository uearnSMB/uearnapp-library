package smarter.uearn.money.models;

/**
 * Created on 01/03/17.
 *
 * @author Dilip Kumar
 * @version 1.0
 */

public class PrivateContacts {

    private String name;
    private String number;
    private boolean blocked;

    public PrivateContacts(String number, String name) {
        this.number = number;
        this.name = name;
    }

    public PrivateContacts(String number, String name,boolean jBlocked) {
        this.number = number;
        this.name = name;
        this.blocked =jBlocked;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean getBlocked() {
        return blocked;
    }

    public void setBlocked(boolean numberBlocked) {
        this.blocked = numberBlocked;
    }
}
