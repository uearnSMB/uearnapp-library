package smarter.uearn.money.training.model;

import java.io.Serializable;
import java.util.List;

public class HelpTopicsData implements Serializable {
    private List<HelpTopics> data;

    public List<HelpTopics> getData() {
        return data;
    }

    public void setData(List<HelpTopics> data) {
        this.data = data;
    }
}
