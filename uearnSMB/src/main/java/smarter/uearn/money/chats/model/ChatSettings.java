package smarter.uearn.money.chats.model;

import java.io.Serializable;

public class ChatSettings implements Serializable {
    private String tech_tab;
    private String pm_tab;
    private String sme_tab;
    private String group_tab;

    public String getTech_tab() {
        return tech_tab;
    }

    public void setTech_tab(String tech_tab) {
        this.tech_tab = tech_tab;
    }

    public String getPm_tab() {
        return pm_tab;
    }

    public void setPm_tab(String pm_tab) {
        this.pm_tab = pm_tab;
    }

    public String getSme_tab() {
        return sme_tab;
    }

    public void setSme_tab(String sme_tab) {
        this.sme_tab = sme_tab;
    }

    public String getGroup_tab() {
        return group_tab;
    }

    public void setGroup_tab(String group_tab) {
        this.group_tab = group_tab;
    }
}
