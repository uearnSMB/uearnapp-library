package smarter.uearn.money.models;

import smarter.uearn.money.utils.AppConstants;
import smarter.uearn.money.utils.ApplicationSettings;

import java.util.ArrayList;

/**
 * Created by kavya on 10/14/2015.
 */
public class GoogleAccountsList {

    private ArrayList<GoogleAccount> googleAccountList = new ArrayList<GoogleAccount>();

    public GoogleAccountsList() {
        googleAccountList = AppConstants.GOOGLE_ACCOUNTSLIST;
    }

    public ArrayList<GoogleAccount> getGoogleAccountList() {
        return googleAccountList;
    }

    public void setGoogleAccountList(ArrayList<GoogleAccount> list) {
        googleAccountList = list;
        ApplicationSettings.putGoogleAccountsListPref(list);
    }

    public void loadGmailAccounts()
    {
        googleAccountList = ApplicationSettings.getGoogleAccountsPref();
    }

    public void doSave() {
        //Log.i("Tag","get Enable in dosave is "+googleAccountList.get(0).getIsEnable());
        setGoogleAccountList(googleAccountList);
    }
}
