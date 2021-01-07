package smarter.uearn.money.models;


public class CustomerDetails {
    public String customerName;
    public String leadSource;
    public String updatedAt;

    public CustomerDetails(String customerName, String leadSource, String updatedAt) {
        this.customerName = customerName;
        this.leadSource = leadSource;
        this.updatedAt = updatedAt;
    }
}
