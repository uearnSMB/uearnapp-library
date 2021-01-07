package smarter.uearn.money.models;


public class CustomerLite {
    public int id;
    public String customerName;
    public String customerNumber;
    public String status;
    public String substatus1;
    public String substatus2;
    public String notes;
    public String type;
    public String customkvs;
    public String leadSource;
    public String referredBy;

    public CustomerLite(int id, String customerName, String customerNumber, String status, String substatus1, String substatus2, String leadSource, String referredBy, String notes, String type, String customkvs) {
        this.id = id;
        this.customerName = customerName;
        this.customerNumber = customerNumber;
        this.status = status;
        this.substatus1 = substatus1;
        this.substatus2 = substatus2;
        this.leadSource = leadSource;
        this.referredBy = referredBy;
        this.notes = notes;
        this.type = type;
        this.customkvs = customkvs;
    }

    public CustomerLite(String customerName, String customerNumber, String status, String substatus1, String substatus2, String notes, String type, String customkvs) {
        this.customerName = customerName;
        this.customerNumber = customerNumber;
        this.status = status;
        this.substatus1 = substatus1;
        this.substatus2 = substatus2;
        this.notes = notes;
        this.type = type;
        this.customkvs = customkvs;
    }
}
