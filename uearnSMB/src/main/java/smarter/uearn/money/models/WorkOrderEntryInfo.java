package smarter.uearn.money.models;

import java.util.ArrayList;

/**
 * Created by kavya on 11/6/2015.
 */
public class WorkOrderEntryInfo {

    public String id = "";
    public String work_order_id = "";
    public String user_id = "";
    public String subject = "";
    public String event_start_date = "";
    public String event_end_date = "";
    public String work_order_status = "";
    public String notes = "";
    public String reminder_start_date = "";
    public int work_completion = 0;
    public String customer_name = "";
    public String customer_number = "";
    public String customer_email = "";
    public String designation = "";
    public String company_name = "";
    public String company_address = "";
    public String website = "";
    public String assigned_to = "";
    public String share_with = "";
    public float total_price = 0;
    public float tax = 0;
    public String advance_date = "";
    public float received_amount = 0;
    public String balance_due_date = "";
    public float amount_receivable = 0;
    public LatLong latLong;
    public String timeZone = "";
    public String cc="";

    //TODO: Add attachments
    public ArrayList<Attachments> attachmentsArrayList = new ArrayList<Attachments>();

    public Attachments attachments;

    public WorkOrderEntryInfo() {

    }
}
