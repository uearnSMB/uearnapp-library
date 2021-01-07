package smarter.uearn.money.models;

import java.io.Serializable;
import java.util.LinkedHashMap;


public class PassbookLifetimeInfo implements Serializable {

    public String date_of_join_txt = "";
    public String date_of_join = "";
    public String total_working_days_txt = "";
    public String total_working_days = "";
    public String total_active_time_txt = "";
    public String total_active_time = "";
    public String total_earned = "";
    public String payment_done = "";
    public String closing_balance = "";
    public String call_earning_txt = "";
    public String call_earning = "";
    public String bonus_txt = "";
    public String bonus = "";
    public String currency = "";
    public LinkedHashMap projectsHash = new LinkedHashMap();
    public LinkedHashMap bonusHash = new LinkedHashMap();

    public PassbookLifetimeInfo() {

    }

}

