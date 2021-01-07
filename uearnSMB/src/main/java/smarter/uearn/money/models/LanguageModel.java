package smarter.uearn.money.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LanguageModel {

    @SerializedName("language")
    @Expose
    private String language;

    @SerializedName("isSelected")
    @Expose
    private boolean isSelected=false;

    public LanguageModel() {

    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

}
