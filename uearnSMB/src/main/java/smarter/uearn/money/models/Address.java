package smarter.uearn.money.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Address implements Serializable {

    public Address() {

    }

    @SerializedName("address")
    @Expose
    private String address = "";

    @SerializedName("longitude")
    @Expose
    private String longitude = "";

    @SerializedName("latitude")
    @Expose
    private String latitude = "";

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    @SerializedName("country")
    @Expose
    private String country = "";

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getPincode() {
        return pincode;
    }

    public void setPincode(String pincode) {
        this.pincode = pincode;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public LatLong getLatLong() {
        return latLong;
    }

    public void setLatLong(LatLong latLong) {
        this.latLong = latLong;
    }

    @SerializedName("pincode")
    @Expose
    private String pincode = "";


    @SerializedName("state")
    @Expose
    private String state = "";

    @SerializedName("city")
    @Expose
    private String city = "";


    @SerializedName("latlong")
    @Expose
    private LatLong latLong = null;

}
