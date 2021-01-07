package smarter.uearn.money.utils.ServerAPIConnectors;

/**
 * Created by Srinath on 08-08-2017.
 */

public class KnowlarityModel {

    private String k_name = "";
    private String agent_name = "";
    private String customer_number = "";
    private String country_code = "";
    private String email = "";
    private String first_name = "";
    private String last_name = "";
    private String phone = "";
    private String start_time = "";
    private String end_time = "";
    private String knowlarity_number = "";
    private String caller_id = "";
    private String user_id = "";


    public KnowlarityModel(String k_name, String agent_name, String customer_number) {
        this.k_name = k_name;
        this.agent_name = agent_name;
        this.customer_number = customer_number;
    }

    public KnowlarityModel(String country_code, String email, String first_name, String last_name, String phone) {
        this.country_code = country_code;
        this.email = email;
        this.first_name = first_name;
        this.last_name = last_name;
        this.phone = phone;
    }

    public KnowlarityModel(String start_time, String end_time, String knowlarity_number, String customer_number) {
        this.start_time = start_time;
        this.end_time = end_time;
        this.knowlarity_number = knowlarity_number;
        this.customer_number = customer_number;
    }

    public String getKnowlarity_number() {
        return knowlarity_number;
    }

    public String getEnd_time() {
        return end_time;
    }

    public String getStart_time() {
        return start_time;
    }

    public String getK_name() {
        return k_name;
    }
    public String getAgent_name() {
        return agent_name;
    }
    public String getCustomer_number() {
        return customer_number;
    }
    public String getCountry_code() {
        return country_code;
    }
    public String getEmail() {
        return email;
    }
    public String getFirst_name() {
        return first_name;
    }
    public String getLast_name() {
        return last_name;
    }
    public String getPhone() {
        return phone;
    }

    public void setClient_id(String caller_id) {
        this.caller_id = caller_id;
    }
    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }
    public String getUser_id() {
        return user_id;
    }
    public String getCaller_id() {
        return caller_id;
    }

}
