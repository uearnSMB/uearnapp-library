package smarter.uearn.money.models;

/**
 * Created by dilipkumar4813 on 21/02/17.
 */

public class ScanLog {

    String number  = "";
    String dateTime  = "";
    String name = "-";
    String callType  = "";
    String callDuration  = "";

    public ScanLog(String number,String cachedName, String dateTime, String callType, String callDuration) {
        this.number = number;
        this.name = cachedName;
        this.dateTime = dateTime;
        this.callType = callType;
        this.callDuration = callDuration;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }

    public String getCallType() {
        return callType;
    }

    public void setCallType(String callType) {
        this.callType = callType;
    }

    public String getCallDuration() {
        return callDuration;
    }

    public void setCallDuration(String callDuration) {
        this.callDuration = callDuration;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
