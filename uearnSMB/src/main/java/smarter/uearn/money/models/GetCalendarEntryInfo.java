package smarter.uearn.money.models;

import java.io.Serializable;

/**
 * Created by kavya on 11/6/2015.
 */
public class GetCalendarEntryInfo implements Serializable {

    public boolean update_all_fields = true;
    public String user_id = " ";
    public String from = " ";
    public String email_id = "";
    public String to = " ";
    public String event_start_date = " ";
    public String event_end_date = " ";
    public String cmailId = " ";
    public String callrec_url = " ";
    public String location = " ";
    public String notes = " ";
    public String repeate = " ";
    public String subject = " ";
    public int limit;
    public String timeZone = " ";
    public String caller_name = "";
    public String caller_number = "";
    public String companyName = " ";
    public String designation = " ";
    public String status = "";
    //For delete or update of calendar
    public String appointment_id;
    public String website = "";
    public String address = "";
    public String last_contactedON = "";
    public String responsestatus = "";
    public LatLong latLong;
    public String longitude;
    public String latitude;
    public String external_calendar_reference = "";
    public String customer_id = "";
    public String alarm_before = "";
    public String order_potential = "";
    public String lead_source = "";
    public String reference_cmail = "";
    public String product_type = "";
    public String calendar_type = "";
    public int tat;
    public String assign_to = "";
    public String cc_email = "";
    public String flp_count = "";
    public String appointmentStartId = "";
    public String appointmentEndId = "";
    public String notesImageUrl = "";
    public String notesAudioUrl = "";
    public String machine_status = "";
    public String created_at = "";
    public String subStatus1 = "";
    public String subStatus2 = "";
    public String updatedAt = "";

    public String extranotes = "";
    public String wrapup = "";
    public String transactionId = "";
    public String mode = "";
    public String customkvs = "";

    public int rnrCount = 0;

    public String getNotesImageUrl() {
        return notesImageUrl;
    }

    public void setNotesImageUrl(String notesImageUrl) {
        this.notesImageUrl = notesImageUrl;
    }

    public String getNotesAudioUrl() {
        return notesAudioUrl;
    }

    public void setNotesAudioUrl(String notesAudioUrl) {
        this.notesAudioUrl = notesAudioUrl;
    }

    public GetCalendarEntryInfo() {

    }
    public GetCalendarEntryInfo(String user_id, String event_start_date, String event_end_date, String calendar_type) {
        this.user_id = user_id;
        this.event_start_date = event_start_date;
        this.event_end_date = event_end_date;
        this.calendar_type = calendar_type;
    }

    public GetCalendarEntryInfo(String user_id, String event_start_date, String event_end_date, String calendar_type, String aStart, String aEnd) {
        this.user_id = user_id;
        this.event_start_date = event_start_date;
        this.event_end_date = event_end_date;
        this.calendar_type = calendar_type;
        this.appointmentStartId = aStart;
        this.appointmentEndId = aEnd;
    }

    public GetCalendarEntryInfo(String user_id, String from, String to, String event_start_date, String event_end_date,
                                String cmailId, String callrec_url, String location, String notes, String repeate,
                                String subject, String timeZone, String caller_name, String companyName) {
        this.user_id = user_id;
        this.from = from;
        this.to = to;
        this.event_start_date = event_start_date;
        this.event_end_date = event_end_date;
        this.cmailId = cmailId;
        this.callrec_url = callrec_url;
        this.location = location;
        this.notes = notes;
        this.repeate = repeate;
        this.subject = subject;
        this.timeZone = timeZone;
        this.caller_name = caller_name;
        this.email_id = from; //Email field and from filed are same.
        this.caller_number = to; //caller name and to field will be same.
        this.companyName = companyName;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public void setEvent_start_date(String event_start_date) {
        this.event_start_date = event_start_date;
    }

    public void setEvent_end_date(String event_end_date) {
        this.event_end_date = event_end_date;
    }

    public void setCmailId(String cmailId) {
        this.cmailId = cmailId;
    }

    //CODE CLEANUP :::KSN
    /*public void setCallrec_url(String callrec_url) {
        this.callrec_url = callrec_url;
    }*/

    public void setLocation(String location) {
        this.location = location;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    //CODE CLEANUP :::KSN
    /*public void setRepeate(String repeate) {
        this.repeate = repeate;
    }*/

    public void setSubject(String subject) {
        this.subject = subject;
    }

    //CODE CLEANUP :::KSN
  /*  public void setLimit(int limit) {
        this.limit = limit;
    }
*/
    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public void setCaller_name(String caller_name) {
        this.caller_name = caller_name;
    }

    public String getUser_id() {
        return user_id;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public String getEvent_start_date() {
        return event_start_date;
    }

    //CODE CLEANUP :::KSN
   /* public String getEvent_end_date() {
        return event_end_date;
    }

    public String getCmailId() {
        return cmailId;
    }

    public String getCallrec_url() {
        return callrec_url;
    }*/

    public String getLocation() {
        return location;
    }

    public String getNotes() {
        return notes;
    }

    //CODE CLEANUP :::KSN
   /* public String getRepeate() {
        return repeate;
    }*/

    public String getSubject() {
        return subject;
    }

    //CODE CLEANUP :::KSN
    /*public int getLimit() {
        return limit;
    }

    public String getTimeZone() {
        return timeZone;
    }*/

    public String getCaller_name() {
        return caller_name;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getCaller_number() {
        return caller_number;
    }

    public void setCaller_number(String caller_number) {
        this.caller_number = caller_number;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getDesignation() {
        return designation;
    }

    public String getAppointment_id() {
        return appointment_id;
    }

    public void setAppointment_id(String appointment_id) {
        this.appointment_id = appointment_id;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    //CODE CLEANUP :::KSN
    /*public String getEmail_id() {
        return email_id;
    }*/

    public void setEmail_id(String email_id) {
        this.email_id = email_id;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    //CODE CLEANUP :::KSN
/*    public String getLast_contactedON() {
        return last_contactedON;
    }

    public void setLast_contactedON(String last_contactedON) {
        this.last_contactedON = last_contactedON;
    }

    public String getResponsestatus() {
        return responsestatus;
    }*/

    public String getResponsestatus() {
        return responsestatus;
    }

    public void setResponsestatus(String responsestatus) {
        this.responsestatus = responsestatus;
    }

    public LatLong getLatLong() {
        return latLong;
    }

    public void setLatLong(LatLong latLong) {
        this.latLong = latLong;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    /*public String getCustomer_id() {
        return customer_id;
    }*/

    public void setCustomer_id(String customer_id) {
        this.customer_id = customer_id;
    }

    public String getExternal_calendar_reference() {
        return external_calendar_reference;
    }

    public void setExternal_calendar_reference(String external_calendar_reference) {
        this.external_calendar_reference = external_calendar_reference;
    }

    //CODE CLEANUP :::KSN
    /*public String getLead_source() {
        return lead_source;
    }*/

    public void setLead_source(String lead_source) {
        this.lead_source = lead_source;
    }

    //CODE CLEANUP :::KSN
    /*public String getReference_cmail() {
        return reference_cmail;
    }*/

    public void setReference_cmail(String reference_cmail) {
        this.reference_cmail = reference_cmail;
    }

    public void setCcMail(String cc_email) {
        this.cc_email = cc_email;
    }

    public String getCcMail() {
        return cc_email;
    }

    public void setFlpCount(String flpCount) {
        this.flp_count = flpCount;
    }

    public String getFlpCount() {
        return flp_count;
    }

    public void setMachine_status(String machine_status) {
        this.machine_status = machine_status;
    }

    public String getMachineStatus() {
        return machine_status;
    }

    public void setRnrCount(int rnrCount) {
        this.rnrCount = rnrCount;
    }
    public int getRnrCount() {
        return rnrCount;
    }

    public void setSubStatus1(String subStatus1) {
        this.subStatus1 = subStatus1;
    }
    public String getSubStatus1() {
        return subStatus1;
    }

    public void setSubStatus2(String subStatus2) {
        this.subStatus2 = subStatus2;
    }

    public String getSubStatus2() {
        return subStatus2;
    }

    public void setupdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getupdatedAt() {
        return updatedAt;
    }

    public void setextraNotes(String extraNotes) {
        this.extranotes = extraNotes;
    }

    public String getextraNotes() {
        return extranotes;
    }

    public void setCustomKVS(String customkvs) {
        this.customkvs = customkvs;
    }

    public String getCustomKVS() {
        return customkvs;
    }
}
