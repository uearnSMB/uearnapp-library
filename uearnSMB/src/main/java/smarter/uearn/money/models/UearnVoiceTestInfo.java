package smarter.uearn.money.models;

import org.json.JSONArray;

import java.io.Serializable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import smarter.uearn.money.utils.ApplicationSettings;


public class UearnVoiceTestInfo implements Serializable {

    public String name = "";
    public String languageType = "";
    public JSONArray languageText;
    public String text = "";
    public String para1 = "";
    public String para2 = "";
    public String para3 = "";
    public boolean isPara1 = false;
    public boolean isPara2 = false;
    public boolean isPara3 = false;
    public String para1Url = "";
    public String para2Url = "";
    public String para3Url = "";

    public UearnVoiceTestInfo() {
    }

    public UearnVoiceTestInfo(String name, String languageType, JSONArray languageText) {
        this.name = name;
        this.languageType = languageType;
        this.languageText = languageText;
    }

    public String getName() {
        return name;
    }

    public String getLanguageType() {
        return languageType;
    }


}

