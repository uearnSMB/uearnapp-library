package smarter.uearn.money.utils;

/**
 * Created by kavya on 5/5/2016.
 * Notification related class
 */
public class NotificationData {

    public static boolean notificationData;
    public static boolean isAppointment, isWorkOrder;
    public static String work_order_id;
    public static String appointment_id;
    public static String statusString;
    public static String notes_string;
    public static String order_value;
    public static long appointment_db_id = 0;
    //Added By Srinath
    public static String knolarity_number = "";
    public static String knolarity_name = "";
    public static String knolarity_start_time = "";
    public static String knolarity_response_time = "";
    public static String knolarity_response= "";
    public static String substatus1 = "";
    public static String substatus2 = "";
    public static String uuid = "";
    public static boolean makeACall  = false;
    public static boolean callFromDialer  = false;
    public static boolean privateCall  = false;
    public static boolean isSocketResponse;
    public static String dialledCustomerNumber = "";
    public static String dialledCustomerName = "";
    public static String customerFeedback = "";
    public static String updatedCustomKVS = "";
    public static String remarks = "";
    public static String leadSource = "";
    public static String emailId = "";
    public static String referredBy = "";
    public static String customKVS = "";
    public static String id = "";
    public static String transactionId = "";
    public static String source = "";
    public static boolean legAConnect  = false;
    public static String backToNetworkCustomerNo = "";
    public static String backToNetworkTransactionId = "";
    public static String backToNetworkResponseMsg = "";
    public static boolean backToNetwork  = false;
    public static String outboundDialledCustomerNumber = "";
    public static String outboundDialledCustomerName = "";
    public static String outboundDialledTransactionId  = "";

    public static String inboundCustomerNumber = "";
    public static String inboundCustomerName = "";
    public static String inboundTransactionId = "";
    public static String grammarTestAnswers = "";
    public static String timeTakenForGrammarTest = "";

    public static void clear() {
        notificationData = false;
        isWorkOrder = false;
        isAppointment = false;
        work_order_id = null;
        appointment_id = null;
//        statusString = null;
        order_value = null;
        notes_string = null;
    }
}
