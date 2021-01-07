package smarter.uearn.money.models;

import java.io.Serializable;

public class NetworkDetails implements Serializable {
    private String network;
    private Integer network_speed;

    public String getNetwork() {
        return network;
    }

    public void setNetwork(String network) {
        this.network = network;
    }

    public Integer getNetwork_speed() {
        return network_speed;
    }

    public void setNetwork_speed(Integer network_speed) {
        this.network_speed = network_speed;
    }
}
