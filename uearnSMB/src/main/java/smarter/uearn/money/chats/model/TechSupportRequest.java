package smarter.uearn.money.chats.model;

import java.io.Serializable;

public class TechSupportRequest implements Serializable {
    private Integer user_id;
    private Integer tech_id;
    private String session_id;

    public Integer getUser_id() {
        return user_id;
    }

    public void setUser_id(Integer user_id) {
        this.user_id = user_id;
    }

    public Integer getTech_id() {
        return tech_id;
    }

    public void setTech_id(Integer tech_id) {
        this.tech_id = tech_id;
    }

    public String getSession_id() {
        return session_id;
    }

    public void setSession_id(String session_id) {
        this.session_id = session_id;
    }
}
