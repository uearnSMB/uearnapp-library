package smarter.uearn.money.models;

/**
 * Created by kavya on 4/28/2016.
 */
public class Attachments {

    public String attachment_name = "";
    public String attachment_url = "";

    public Attachments() {}

    public Attachments(String attachment_name, String attachment_url) {
        this.attachment_name = attachment_name;
        this.attachment_url = attachment_url;
    }

    public String getAttachment_name() {
        return attachment_name;
    }

    public void setAttachment_name(String attachment_name) {
        this.attachment_name = attachment_name;
    }

    public String getAttachment_url() {
        return attachment_url;
    }

    public void setAttachment_url(String attachment_url) {
        this.attachment_url = attachment_url;
    }
}
