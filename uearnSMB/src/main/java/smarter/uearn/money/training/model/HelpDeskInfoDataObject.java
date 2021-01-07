package smarter.uearn.money.training.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class HelpDeskInfoDataObject {
    @SerializedName("data")
    @Expose
    private List<HelpDeskInfoData> data = null;

    public List<HelpDeskInfoData> getData() {
        return data;
    }

    public void setData(List<HelpDeskInfoData> data) {
        this.data = data;
    }
}
