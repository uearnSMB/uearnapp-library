package smarter.uearn.money.training.model;

import java.io.Serializable;

public class HelpTopics implements Serializable {
    private String helpdesk_id;
    private String helpdesk_type;
    private String helpdeskmessage;

    public String getHelpdesk_id() {
        return helpdesk_id;
    }

    public void setHelpdesk_id(String helpdesk_id) {
        this.helpdesk_id = helpdesk_id;
    }

    public String getHelpdesk_type() {
        return helpdesk_type;
    }

    public void setHelpdesk_type(String helpdesk_type) {
        this.helpdesk_type = helpdesk_type;
    }

    public String getHelpdeskmessage() {
        return helpdeskmessage;
    }

    public void setHelpdeskmessage(String helpdeskmessage) {
        this.helpdeskmessage = helpdeskmessage;
    }
}
