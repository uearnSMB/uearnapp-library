package smarter.uearn.money.utils.webservice;

import smarter.uearn.money.utils.ServerAPIConnectors.Urls;

/**
 * Created by Dilip on 1/18/2017.
 * Webservice constants class
 */

public class Constants {

    // Domain URL
   /* static final String DOMAIN_URL = "http://www.smarter-biz.com/";*/
    public static String DOMAIN_URL = Urls.SERVER_ADDRESS+"/";
    //static final String DOMAIN_URL = "https://smbdev.azurewebsites.net/";
    //static final String DOMAIN_URL = "http://35.187.227.191/" ;

    // Form Methods
    static final String POST = "POST";
    static final public String GET = "GET";

    // Microsoft AUTH variables
    /*static final String AUTH_URL = "https://login.windows.net/rbcsmarterbiz.onmicrosoft.com/oauth2/token";*/
    static final String AUTH_URL = Urls.PERMANENT_SERVER_ADDRESS +"/secured/oauth2/token";
    static final String AUTH_GRANT_TYPE = "password";
    static final String AUTH_RESOURCE = Urls.PERMANENT_SERVER_ADDRESS +"/smarterbizapp";
    static final String AUTH_CLIENT_ID = "e4acd36b-44b4-48cf-8e1a-1ab9f45b469f";
    static final String AUTH_USERNAME = "app@rbcsmarterbiz.onmicrosoft.com";
    static final String AUTH_PWD = "Smarterbiz1";
    static final String AUTH_SCOPE = "openid";

    // Webservice URL's
    static final String LOGIN_API = "check_login.json?";
    static final String FORGOT_API = "api/v2/reset_password.json?";
    static final String CHANGE_PASSWORD_API = "api/v2/changepwd.json?";
    static final String USER_REGISTRATION = "api/v2/cmail_users.json?ver=2";
    static final String GET_CALENDAR_EVENTS_URL = "api/v2/common/event/list?user_id=";
    static final public String SETTINGS_STAGES = "api/v2/settings/stages?user_id=";
    static final String SETTINGS_TEAM = "api/v2/team?user_id=";
    static final String SETTINGS_STAGES_SAVE = "api/v2/settings/stages";
    static final String SMARTER_ERROR_LOGS = "api/v2/logmail";
    static final String SMARTER_REMOTE_DIAL_START = "api/v2/bcp/remotedialstart";
    static final String SMARTER_REMOTE_DIAL_STOP = "api/v2/bcp/remotedialstop";
}
