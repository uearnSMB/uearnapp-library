package smarter.uearn.money.models;

import smarter.uearn.money.utils.AppConstants;
import smarter.uearn.money.utils.ApplicationSettings;

/**
 * Created by Admin on 25-06-2015.
 */
public class AppSettings {

    private boolean logout=true;
    private boolean first_time_launch=true;
    private boolean disable_call_recording_layout=false;
    private int day;
    private boolean firstTimeGmailAuth = true;
    private String contactString;

    public boolean getLogout(){
        //return logout;
        return ApplicationSettings.getPref(AppConstants.APP_LOGOUT, false);
    }
    public void setLogout(boolean value){
        if(value) {
            logout = true;
            ApplicationSettings.putPref(AppConstants.CHECK_LOGIN_STATUS,false);
            ApplicationSettings.putPref(AppConstants.APP_LOGOUT,true);
        }else{
            logout=false;
            ApplicationSettings.putPref(AppConstants.CHECK_LOGIN_STATUS,true);
            ApplicationSettings.putPref(AppConstants.APP_LOGOUT,false);
        }
    }
    public boolean getFirstTimeLaunch(){
        return first_time_launch;
    }
    public void setFirstTimeLaunch(boolean value){
        if(value) {
            first_time_launch = true;
            ApplicationSettings.putPref(AppConstants.ONE_TIME_SPLASH, false);
        }else{
            first_time_launch=false;
            ApplicationSettings.putPref(AppConstants.ONE_TIME_SPLASH, true);
        }
    }

    public boolean isFirstTimeGmailAuth(){
        return firstTimeGmailAuth;
    }

    public void setFirstTimeGmailAuth(boolean value){
        firstTimeGmailAuth=value;
        ApplicationSettings.putPref(AppConstants.FIRST_TIME_GMAIL, value);
    }

    public void setContactString(String contacts){
        contactString = contacts;
        ApplicationSettings.putPref(AppConstants.PREF_CONTACT_STRING,contacts);
    }

    public boolean getDisableCallRecordingLayout(){
        return disable_call_recording_layout;
    }

    public int getDay(){
        return day;
    }

    public void setDay(int day){
        this.day = day;
        ApplicationSettings.putPref(AppConstants.PREF_DATE_CHECK,day);
    }

    public void loadfromAppConstants(){
        logout = ApplicationSettings.getPref(AppConstants.APP_LOGOUT,true);
        first_time_launch = !(ApplicationSettings.getPref(AppConstants.ONE_TIME_SPLASH,false));
        disable_call_recording_layout = ApplicationSettings.getPref(AppConstants.DISABLE_CALL_REC_BUTTONS,false);
        day = ApplicationSettings.getPref(AppConstants.PREF_DATE_CHECK,0);
        contactString = ApplicationSettings.getPref(AppConstants.PREF_CONTACT_STRING,"");
        firstTimeGmailAuth = ApplicationSettings.getPref(AppConstants.FIRST_TIME_GMAIL,true);
    }
}
