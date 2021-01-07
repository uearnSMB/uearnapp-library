package smarter.uearn.money.utils;


import android.content.Context;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

import com.google.gson.Gson;

import smarter.uearn.money.models.Address;
import smarter.uearn.money.models.GoogleAccount;
import smarter.uearn.money.models.KycDocuments;
import smarter.uearn.money.views.SmartAudioPlayer;
import smarter.uearn.money.views.SmartAudioRecorder;

import java.util.ArrayList;
import java.util.HashMap;

public class ApplicationSettings extends PreferenceActivity {
    private static SharedPreferences getPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(SmarterSMBApplication.getInstance().getApplicationContext());
    }

    public static SharedPreferences.Editor getEditor() {
        SharedPreferences sp = getPreferences();
        if (sp != null)
            return sp.edit();

        return null;
    }

    public static int getPref(String key, int defValue) {
        SharedPreferences sp = getPreferences();
        if (sp == null) return defValue;
        return sp.getInt(key, defValue);
    }

    public static long getLongPref(String key, long defValue) {
        SharedPreferences sp = getPreferences();
        if (sp == null) return defValue;
        return sp.getLong(key, defValue);
    }

    public static void putLongPref(String key, long val) {
        SharedPreferences.Editor spe = getEditor();
        if (spe == null) return;
        spe.putLong(key, val);
        spe.commit();
    }

    public static float getFloatPref(String key, float defValue) {
        SharedPreferences sp = getPreferences();
        if (sp == null) return defValue;
        return sp.getFloat(key, defValue);
    }

    public static void putFloatPref(String key, float val) {
        SharedPreferences.Editor spe = getEditor();
        if (spe == null) return;
        spe.putFloat(key, val);
        spe.commit();
    }


    public static void putPref(String key, int val) {
        SharedPreferences.Editor spe = getEditor();
        if (spe == null) return;
        spe.putInt(key, val);
        spe.commit();
    }

    public static boolean getPref(String key, boolean defValue) {
        SharedPreferences sp = getPreferences();
        if (sp == null) return defValue;
        return sp.getBoolean(key, defValue);
    }

    public static void putPref(String key, boolean val) {
        SharedPreferences.Editor spe = getEditor();
        if (spe == null) return;
        spe.putBoolean(key, val);
        spe.commit();
    }

    public static long getPref(String key, long defValue) {
        SharedPreferences sp = getPreferences();
        if (sp == null) return defValue;
        return sp.getLong(key, defValue);
    }

    public static void putPref(String key, long val) {
        SharedPreferences.Editor spe = getEditor();
        if (spe == null) return;
        spe.putLong(key, val);
        spe.commit();
    }

    public static String getPref(String key, String defValue) {
        SharedPreferences sp = getPreferences();
        if (sp == null) return defValue;
        return sp.getString(key, defValue);
    }

    public static void putPref(String key, String val) {
        SharedPreferences.Editor spe = getEditor();
        if (spe == null) return;
        spe.putString(key, val);
        spe.commit();
    }

    public static void putAppointmentStatusArrayListPref(ArrayList<String> stringArrayList) {
        String arrayListName = "appointmentStatusStringList";
        SharedPreferences.Editor editor = getEditor();
        editor.putInt(arrayListName + "_size", stringArrayList.size());
        for (int i = 0; i < stringArrayList.size(); i++) {
            editor.putString(arrayListName + "_" + i, stringArrayList.get(i));
        }
        editor.commit();
    }

    public static ArrayList<String> getAppointmentStatusArrayList() {
        String arrayName = "appointmentStatusStringList";
        SharedPreferences prefs = getPreferences();
        int size = prefs.getInt(arrayName + "_size", 0);
        ArrayList<String> stringArrayList = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            String statusString = prefs.getString(arrayName + "_" + i, null);
            stringArrayList.add(statusString);
        }
        return stringArrayList;
    }

    public static void putWorkOrderStatusArrayListPref(ArrayList<String> stringArrayList) {
        String arrayListName = "workOrderStatusStringList";
        SharedPreferences.Editor editor = getEditor();
        editor.putInt(arrayListName + "_size", stringArrayList.size());
        for (int i = 0; i < stringArrayList.size(); i++) {
            editor.putString(arrayListName + "_" + i, stringArrayList.get(i));
        }
        editor.commit();
    }

    public static ArrayList<String> getWorkOrderStatusArrayList() {
        String arrayName = "workOrderStatusStringList";
        SharedPreferences prefs = getPreferences();
        int size = prefs.getInt(arrayName + "_size", 0);
        ArrayList<String> stringArrayList = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            String statusString = prefs.getString(arrayName + "_" + i, null);
            stringArrayList.add(statusString);
        }
        return stringArrayList;
    }

    public static void putGoogleAccountsListPref(ArrayList<GoogleAccount> accountArrayList) {
        String arrayListName = "googleAccountsList";
        SharedPreferences.Editor editor = getEditor();
        editor.putInt(arrayListName + "_size", accountArrayList.size());
        for (int i = 0; i < accountArrayList.size(); i++) {
            editor.putString(arrayListName + "_" + i + "_email", accountArrayList.get(i).getEmail());
            editor.putString(arrayListName + "_" + i + "_userId", accountArrayList.get(i).getUser_id());
            editor.putString(arrayListName + "_" + i + "_id", accountArrayList.get(i).getId());
            editor.putString(arrayListName + "_" + i + "_refreshToken", accountArrayList.get(i).getRefreshToken());
            editor.putString(arrayListName + "_" + i + "_accessToken", accountArrayList.get(i).getAccessToken());
            editor.putBoolean(arrayListName + "_" + i + "_isenable", accountArrayList.get(i).getIsEnable());
            editor.putBoolean(arrayListName + "_" + i + "_googleCalendarEnable", accountArrayList.get(i).getIsGoogleCalendarEnable());
            editor.putString(arrayListName + "_" + i + "_type", accountArrayList.get(i).getTypeOfAccount());
            editor.putBoolean(arrayListName + "_" + i + "_isprimary", accountArrayList.get(i).getisPrimary());
            //  editor.putString(arrayListName+"_"+i+"_typeOfAccount", accountArrayList.get(i).getTypeOfAccount());
        }
        editor.commit();
    }

    public static ArrayList<GoogleAccount> getGoogleAccountsPref() {

        String arrayName = "googleAccountsList";
        SharedPreferences prefs = getPreferences();
        int size = prefs.getInt(arrayName + "_size", 0);
        ArrayList<GoogleAccount> googleAccountArrayList = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            GoogleAccount googleAccount = new GoogleAccount();
            googleAccount.setEmail(prefs.getString(arrayName + "_" + i + "_email", null));
            googleAccount.setUser_id(prefs.getString(arrayName + "_" + i + "_userId", null));
            googleAccount.setId(prefs.getString(arrayName + "_" + i + "_id", null));
            googleAccount.setRefreshToken(prefs.getString(arrayName + "_" + i + "_refreshToken", null));
            googleAccount.setAccessToken(prefs.getString(arrayName + "_" + i + "_accessToken", null));
            googleAccount.setIsEnable(prefs.getBoolean(arrayName + "_" + i + "_isenable", true));
            googleAccount.setGoogleCalendarEnable(prefs.getBoolean(arrayName + "_" + i + "_googleCalendarEnable", true));
            googleAccount.setTypeOfAccount(prefs.getString(arrayName + "_" + i + "_type", null));
            googleAccount.setIsPrimary(prefs.getBoolean(arrayName + "_" + i + "_isprimary", false));
            googleAccountArrayList.add(googleAccount);
        }

        return googleAccountArrayList;
    }

    public static void putToken(String token) {
        String key = "token";
        SharedPreferences.Editor editor = getEditor();
        editor.putString(key, token);
        editor.commit();
    }

    public static String getToken() {
        String key = "token";
        SharedPreferences preferences = getPreferences();
        if (preferences == null) return null;
        String token = preferences.getString(key, null);
        return token;
    }

    /*Sales reports :::KSN*/
    public static void putSalesStage(ArrayList salesList) {
        SharedPreferences.Editor editor = getEditor();
        ArrayList<String> arrayList = salesList;
        for (int i = 0; i < arrayList.size(); i++) {

            editor.putString("stage" + i, arrayList.get(i));
        }
        editor.putInt("SalesStageLength", arrayList.size());
        editor.apply();
    }

    public static ArrayList<String> getSalesStage() {
        ArrayList<String> arrayList = new ArrayList<String>();
        SharedPreferences preferences = getPreferences();
        if (preferences == null) return null;
        int length = preferences.getInt("SalesStageLength", 0);
        //Log.d("SalesStageLength",""+length);
        for (int i = 0; i < length; i++) {
            arrayList.add(preferences.getString("stage" + i, null));
        }
        return arrayList;
    }

    public static void putSalesValue(ArrayList salesValueList) {
        SharedPreferences.Editor editor = getEditor();
        ArrayList<String> arrayList = salesValueList;
        for (int i = 0; i < arrayList.size(); i++) {
            editor.putString("value" + i, arrayList.get(i));
        }
        editor.putInt("SalesValueLength", arrayList.size());
        editor.apply();
    }

    public static ArrayList<String> getSalesValue() {
        ArrayList<String> arrayList = new ArrayList<String>();
        SharedPreferences preferences = getPreferences();
        if (preferences == null) return null;
        int length = preferences.getInt("SalesValueLength", 0);
        //Log.d("SalesValueLength",""+length);
        for (int i = 0; i < length; i++) {
            arrayList.add(preferences.getString("value" + i, null));
        }
        return arrayList;
    }

    public static void putValue(ArrayList ValueList, String nameOfValue) {
        SharedPreferences.Editor editor = getEditor();
        ArrayList<String> arrayList = ValueList;
        for (int i = 0; i < arrayList.size(); i++) {
            editor.putString(nameOfValue + i, arrayList.get(i));
            //Log.d(nameOfValue+":",""+arrayList.get(i));
        }
        editor.putInt(nameOfValue + "Length", arrayList.size());
        editor.apply();
    }

    public static ArrayList<String> getValue(String nameOfValue) {
        ArrayList<String> arrayList = new ArrayList<String>();
        SharedPreferences preferences = getPreferences();
        if (preferences == null) return null;
        int length = preferences.getInt(nameOfValue + "Length", 0);
        //Log.d(nameOfValue+"Length",""+length);
        for (int i = 0; i < length; i++) {
            arrayList.add(preferences.getString(nameOfValue + i, null));
        }
        return arrayList;
    }

    public static void removePref(String val) {
        SharedPreferences.Editor spe = getEditor();
        if (spe == null) return;
        spe.remove(val);
        spe.commit();
    }

    public static boolean containsPref(String key) {
        SharedPreferences sp = getPreferences();
        return sp.contains(key);
    }

    public static void clear() {
        SharedPreferences.Editor spe = getEditor();
        if (spe == null) return;
        spe.clear();
        spe.commit();
    }

    public static MediaPlayer mediaPlayer;

    /*public static MediaPlayer getAudioPlayer() {
        return mediaPlayer;
    }*/


    //=========Start of Get and set of user KyC Documents=============
    public static void setKycDocument(Context context, String tagName, KycDocuments kycDocuments) {
        if (kycDocuments == null) {
            putPref(tagName, "");
        } else {
            Gson gson = new Gson();
            String json = gson.toJson(kycDocuments);
            putPref(tagName, json);
        }
    }

    public static KycDocuments getKycDocuments(Context context, String tagName) {
        KycDocuments userKYC = null;
        String userStr = getPref(tagName, "");
        if (userStr != null) {
            Gson gson = new Gson();
            userKYC = gson.fromJson(userStr, KycDocuments.class);
        }
        return userKYC;
    }
    //=========End of Get and set of user KyC Documents=============


    //=========Get and set of userAddress=============

    public static void setUserAddress(String tagName, Address userAddress) {
        if (userAddress == null) {
            putPref(tagName, "");
        } else {
            Gson gson = new Gson();
            String json = gson.toJson(userAddress);
            putPref(tagName, json);
        }
    }


    public static Address getUserAddress(String tagName) {
        if (Utils.isStringEmpty(tagName)) {
            return null;
        } else {
            Address userAddress = null;
            String userStr = getPref(tagName, null);
            if (userStr != null) {
                Gson gson = new Gson();
                userAddress = gson.fromJson(userStr, Address.class);
            }
            return userAddress;
        }
    }
    //=======================================

}
