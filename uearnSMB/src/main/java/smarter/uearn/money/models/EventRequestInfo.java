package smarter.uearn.money.models;

/**
 * Created by Admin on 09-07-2015.
 */
public class EventRequestInfo {

    private String event_type_str = null;
    private String audio_content_type = null;
    private String content_type = null;
    private String call_type = null;
    private String phone = null;
    private String contact_name = null;

    public String getEvent_type_str() {
        return event_type_str;
    }

    //CODE CLEANUP :::KSN
  /*  public void setEvent_type_str(String event_type_str) {
        this.event_type_str = event_type_str;
    }*/

    public String getAudio_content_type() {
        return audio_content_type;
    }

    //CODE CLEANUP :::KSN
   /* public void setAudio_content_type(String audio_content_type) {
        this.audio_content_type = audio_content_type;
    }*/

    public String getContent_type() {
        return content_type;
    }

    //CODE CLEANUP :::KSN
    /*public void setContent_type(String content_type) {
        this.content_type = content_type;
    }
*/
    public String getCall_type() {
        return call_type;
    }

    //CODE CLEANUP :::KSN
   /* public void setCall_type(String call_type) {
        this.call_type = call_type;
    }
*/
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getContact_name() {
        return contact_name;
    }

    //CODE CLEANUP :::KSN
   /* public void setContact_name(String contact_name) {
        this.contact_name = contact_name;
    }*/
}
