package smarter.uearn.money.models;

/**
 * Created by Srinath on 16-Jan-17.
 */
public class AppStatus {
    public String activity_type;
    public String gps_status;
    public String call_recording_status;
    public String battery_status;
    public String user_id;
    public String time_stamp;
    public String versionNumber;
    public double latitude;
    public double longitude;


    public AppStatus(String activity_type, String gps_status, String call_recording_status, String battery_status, String user_id, String time_stamp, String versionNumber) {
        this.activity_type = activity_type;
        this.gps_status = gps_status;
        this.call_recording_status = call_recording_status;
        this.battery_status = battery_status;
        this.user_id = user_id;
        this.time_stamp = time_stamp;
        this.versionNumber = versionNumber;
    }

    public AppStatus(String activity_type, String gps_status, String call_recording_status, String battery_status, String user_id, String time_stamp, String versionNumber, double latitude, double longitude) {
        this.activity_type = activity_type;
        this.gps_status = gps_status;
        this.call_recording_status = call_recording_status;
        this.battery_status = battery_status;
        this.user_id = user_id;
        this.time_stamp = time_stamp;
        this.versionNumber = versionNumber;
        this.latitude = latitude;
        this.longitude = longitude;
    }

}
