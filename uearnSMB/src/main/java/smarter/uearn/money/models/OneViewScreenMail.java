package smarter.uearn.money.models;

import java.util.ArrayList;

/**
 * Created by Admin on 30-07-2016.
 */
public class OneViewScreenMail {
    private String id;
    private String name;
    private String designation;
    private String number;
    private String email;
    private String address;
    private String website;
    private String company;
    private String activity_type;
    private String activity_id;
    private String start_time;
    private String end_time;
    private String created_time;
    private String subject;
    private String message;
    private String password;
    private String description;
    private String from;
    private String to;
    private String latitude;
    private String longitude;
    private boolean db_allowbooking;
    private boolean show_callrecording;
    private ArrayList<Attachments> attachmentListArrayList;
    private ArrayList<AttachmentList> attachmentsSerilizableList;
    private String url;
    private String cmail_url;
    private String status;
    private String order_potential;
    private String location;
    private String product_type;
    private String responsestatus;
    private String source_of_lead;
    private String customer_to_contact;
    private String extenal_calendar_reference;
    private String bcc;
    private String htmlMessage;
    private String uuid;
    private int parent;
    private int children;

    private String transcription_url = "";
    private String transcription_lang = "";

    public String getTranscription_url() {
        return transcription_url;
    }

    public String getTranscription_lang() {
        return transcription_lang;
    }

    public void setTranscription_url(String transcription_url) {
        this.transcription_url = transcription_url;
    }

    public void setTranscription_lang(String transcription_lang) {
        this.transcription_lang = transcription_lang;
    }


//TODO: remove when new Digital brochure is implemented
    private String eventType;

    public OneViewScreenMail() {
    }
    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setCmail_url(String cmail_url) {
        this.cmail_url = cmail_url;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getOrder_potential() {
        return order_potential;
    }

    public void setOrder_potential(String order_potential) {
        this.order_potential = order_potential;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getProduct_type() {
        return product_type;
    }

    public void setProduct_type(String product_type) {
        this.product_type = product_type;
    }

    public void setResponsestatus(String responsestatus) {
        this.responsestatus = responsestatus;
    }

    public void setSource_of_lead(String source_of_lead) {
        this.source_of_lead = source_of_lead;
    }

    public void setCustomer_to_contact(String customer_to_contact) {
        this.customer_to_contact = customer_to_contact;
    }

   /* public String getExtenal_calendar_reference() {
        return extenal_calendar_reference;
    }

    public void setExtenal_calendar_reference(String extenal_calendar_reference) {
        this.extenal_calendar_reference = extenal_calendar_reference;
    }*/
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getActivity_type() {
        return activity_type;
    }

    public void setActivity_type(String activity_type) {
        this.activity_type = activity_type;
    }

    public String getStart_time() {
        return start_time;
    }

    public void setStart_time(String start_time) {
        this.start_time = start_time;
    }

    public String getEnd_time() {
        return end_time;
    }

    public void setEnd_time(String end_time) {
        this.end_time = end_time;
    }

    public String getActivity_id() {
        return activity_id;
    }

    public void setActivity_id(String activity_id) {
        this.activity_id = activity_id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public ArrayList<Attachments> getAttachmentListArrayList() {
        return attachmentListArrayList;
    }

    public void setAttachmentListArrayList(ArrayList<Attachments> attachmentListArrayList) {
        this.attachmentListArrayList = attachmentListArrayList;
    }

    public String getLatitude() {
        return latitude;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public void setuuid(String uuid) {
        this.uuid = uuid;
    }
    public String getUuid() {
        return uuid;
    }
    public boolean isDb_allowbooking() {
        return db_allowbooking;
    }

    public void setDb_allowbooking(boolean db_allowbooking) {
        this.db_allowbooking = db_allowbooking;
    }

    public boolean isShow_callrecording() {
        return show_callrecording;
    }

    public void setShow_callrecording(boolean show_callrecording) {
        this.show_callrecording = show_callrecording;
    }

    public ArrayList<AttachmentList> getAttachmentsSerilizableList() {
        return attachmentsSerilizableList;
    }

    public void setAttachmentsSerilizableList(ArrayList<AttachmentList> attachmentsSerilizableList) {
        this.attachmentsSerilizableList = attachmentsSerilizableList;
    }

    public void setBcc(String bcc) {
        this.bcc = bcc;
    }

    public void setHtmlMessage(String htmlMessage) {
        this.htmlMessage = htmlMessage;
    }

    public String getCreated_time() {
        return created_time;
    }

    public void setCreated_time(String created_time) {
        this.created_time = created_time;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public void setParent(int parent) {
        this.parent = parent;
    }

    public int getParent() {
        return this.parent;
    }

    public void setChildren(int children) {
        this.children = children;
    }

    public int getChildren() {
        return this.children;
    }

    public String getSource_of_lead() {
        return source_of_lead;
    }

    public String getCustomerToContact() {
        return customer_to_contact;
    }
}
