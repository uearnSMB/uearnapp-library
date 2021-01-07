package smarter.uearn.money.models;

import java.io.IOException;
import java.io.Serializable;

/**
 * Created by alesudha on 7/7/2016.
 */
public class AttachmentList implements Serializable {

    public String attachment_name = "";
    public String attachment_url = "";

    public AttachmentList() {}

    public AttachmentList(String attachment_name, String attachment_url) {
        this.attachment_name = attachment_name;
        this.attachment_url = attachment_url;
    }

    public String getAttachment_name() {
        return attachment_name;
    }

    public void setAttachment_name(String attachment_name) {
        this.attachment_name = attachment_name;
    }
    
    public void setAttachment_url(String attachment_url) {
        this.attachment_url = attachment_url;
    }

}
