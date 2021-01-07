package smarter.uearn.money.models;

import org.json.JSONArray;

import java.io.Serializable;


public class CheckBoxStatusInfo implements Serializable {
    private JSONArray checkBoxKey;
    public JSONArray checkBoxValue;
    private int maxChecked = 0;

    public CheckBoxStatusInfo(int maxChecked, JSONArray checkBoxKey, JSONArray checkBoxValue) {
        this.maxChecked = maxChecked;
        this.checkBoxKey = checkBoxKey;
        this.checkBoxValue = checkBoxValue;
    }

}

