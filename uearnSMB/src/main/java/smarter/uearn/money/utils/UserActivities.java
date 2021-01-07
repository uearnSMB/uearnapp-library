package smarter.uearn.money.utils;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Srinath on 10/28/2016.
 */
public class UserActivities {

    public String user_id;
    public String startTime;
    public String endTime;
    public String userMail;

    public static ArrayList<HashMap<String, String>> agentList;
    public static ArrayList<HashMap<String, String>> missedagentList;
    public UserActivities() {

    }
    public UserActivities(String user_id, String startTime, String endTime) {
        this.user_id = user_id;
        this.startTime = startTime;
        this.endTime = endTime;
    }



    public UserActivities(String user_id, String userMail, String startTime, String endTime) {
        this.user_id = user_id;
        this.startTime = startTime;
        this.endTime = endTime;
        this.userMail = userMail;
    }










    public void setActivityName(ArrayList activityName, int i){
        ApplicationSettings.putValue(activityName, "activitiesName"+i);
    }

    public ArrayList<String> getActivityName(int i){
       return ApplicationSettings.getValue("activitiesName"+i);
    }

    public void setActivityxaxis(ArrayList activityName, int i){
        ApplicationSettings.putValue(activityName, "activityXaxis");
    }

    public ArrayList<String> getActivityxaxis(){
        return ApplicationSettings.getValue("activityXaxis");
    }


    public void setActivityValue(ArrayList activityName, int i){
        ApplicationSettings.putValue(activityName, "activitiesValue"+i);
    }

    public ArrayList<String> getActivityValue(int i){
        return ApplicationSettings.getValue("activitiesValue"+i);
    }

    public void setSalesValue(ArrayList<String> salesReportlist) {
        ApplicationSettings.putSalesValue(salesReportlist);
    }

    public void setSalesStage(ArrayList< String> salesStage) {
        ApplicationSettings.putSalesStage(salesStage);
    }

    public ArrayList< String> getSalesValue() {
        ArrayList<String> valueList = ApplicationSettings.getSalesValue();
        return valueList;
    }

    public ArrayList< String> getSalesStage() {
        ArrayList< String> stageList = ApplicationSettings.getSalesStage();
        return stageList;
    }

    /*Status Graph ::KSN*/
    public void setStatusName(ArrayList<String> statusName){
         ApplicationSettings.putValue(statusName, "StatusName");
    }

    public ArrayList<String> getStatusName(){
        return  ApplicationSettings.getValue("StatusName");
    }

    public void setTeamlist(ArrayList<String> teamList, int i){
        ApplicationSettings.putValue(teamList, "TeamName"+i);
    }

    public ArrayList<String> getTeamlist(int i){
        return  ApplicationSettings.getValue("TeamName"+i);
    }

    public void setStatusValue(ArrayList<String> statusValue, int i){
        ApplicationSettings.putValue(statusValue, "Statusvalue"+i);
    }

    public ArrayList<String> getstatusValue(int i){
        return  ApplicationSettings.getValue("Statusvalue"+i);
    }

    /*Appointment Graph*/
    public void setAppontmentName(ArrayList<String> appointmentName){
        ApplicationSettings.putValue(appointmentName, "appontmentName");
    }

    public ArrayList<String> getAppointmentName(){
        return  ApplicationSettings.getValue("appontmentName");
    }

    public void setApntmentTeamList(ArrayList<String> teamList, int i){
        ApplicationSettings.putValue(teamList, "ApntmentTeam"+i);
    }

    public ArrayList<String> getApntmentTeamList(int i){
        return  ApplicationSettings.getValue("ApntmentTeam"+i);
    }

    public void setAppointmentValue(ArrayList<String> setAppointmentValue, int i){
        ApplicationSettings.putValue(setAppointmentValue, "AppointmentValue"+i);
    }

    public ArrayList<String> getAppointmentValue(int i){
        return  ApplicationSettings.getValue("AppointmentValue"+i);
    }

    /*Missed Call Ananlysis*/
    public void setcallName(ArrayList<String> callName){
        ApplicationSettings.putValue(callName, "callName");
    }

    public ArrayList<String> getcallName(){
        return  ApplicationSettings.getValue("callName");
    }

    public void setcallTeamList(ArrayList<String> teamList, int i){
        ApplicationSettings.putValue(teamList, "CallTeam"+i);
    }

    public ArrayList<String> getcallTeamList(int i){
        return  ApplicationSettings.getValue("CallTeam"+i);
    }

    public void setcallValue(ArrayList<String> setAppointmentValue, int i){
        ApplicationSettings.putValue(setAppointmentValue, "CallValue"+i);
    }

    public ArrayList<String> getcallValue(int i){
        return  ApplicationSettings.getValue("CallValue"+i);
    }

    /*funnel excel data ::KSN*/
    public void setfunnelheader(ArrayList<String> callName){
        ApplicationSettings.putValue(callName, "FunnelName");
    }

    public ArrayList<String> getfunnelheader(){
        return  ApplicationSettings.getValue("FunnelName");
    }

    public void setfunnelrowValues(ArrayList<String> rowvalues, int i){
        ApplicationSettings.putValue(rowvalues, "rowValues"+i);
    }

    public ArrayList<String> getfunnelrowValues(int i){
        return  ApplicationSettings.getValue("rowValues"+i);
    }
    public void setfunnelrowSize(int i){
          ApplicationSettings.putPref("rowSize", i);
    }
    public int getfunnelrowSize() {
        return ApplicationSettings.getPref("rowSize",0);
    }

    /*funnelcummilative excel data ::KSN*/
    public void setFunnelCumulativeHeader(ArrayList<String> callName){
        ApplicationSettings.putValue(callName, "FunnelCumulative");
    }

    public ArrayList<String> getFunnelCumulativeHeader(){
        return  ApplicationSettings.getValue("FunnelCumulative");
    }

    public void setcumulativerowValues(ArrayList<String> rowvalues, int i){
        ApplicationSettings.putValue(rowvalues, "FunnelCumulativeRowValues"+i);
    }

    public ArrayList<String> getCumulativeRowValues(int i){
        return  ApplicationSettings.getValue("FunnelCumulativeRowValues"+i);
    }
    public void setFunnelCumulativerowSize(int i){
        ApplicationSettings.putPref("FunnelCumulativeRowSize", i);
    }
    public int getFunnelCumulativeRowSize() {
        return ApplicationSettings.getPref("FunnelCumulativeRowSize",0);
    }

    /*activities Excel*/
    public void setactivitiesheader(ArrayList<String> callName){
        ApplicationSettings.putValue(callName, "activityHeader");
    }

    public ArrayList<String> getactivitiesheader(){
        return  ApplicationSettings.getValue("activityHeader");
    }

    public void setactivityrowValues(ArrayList<String> rowvalues, int i){
        ApplicationSettings.putValue(rowvalues, "activityrowValues"+i);
    }

    public ArrayList<String> getactivityrowValues(int i){
        return  ApplicationSettings.getValue("activityrowValues"+i);
    }
    public void setactivityowSize(int i){
        ApplicationSettings.putPref("activityrowSize", i);
    }
    public int getactivityrowSize() {
        return ApplicationSettings.getPref("activityrowSize",0);
    }

    /*Status Excel :: KSN*/
    public void setStatusExcelheader(ArrayList<String> callName){
        ApplicationSettings.putValue(callName, "statusExcelHeader");
    }

    public ArrayList<String> getStatusExcelsheader(){
        return  ApplicationSettings.getValue("statusExcelHeader");
    }

    public void setStatusExcelRowValues(ArrayList<String> rowvalues, int i){
        ApplicationSettings.putValue(rowvalues, "statusExcelrowValues"+i);
    }

    public ArrayList<String> getstatusrowValues(int i){
        return  ApplicationSettings.getValue("statusExcelrowValues"+i);
    }
    public void setExcelStatusRowSize(int i){
        ApplicationSettings.putPref("statusExcelRowSize", i);
    }
    public int getstatusrowSize() {
        return ApplicationSettings.getPref("statusExcelRowSize",0);
    }

    /*Excel call Data*/
    public void setexcelCallAnalysis(ArrayList<String> arrayList, int i) {
        ApplicationSettings.putValue(arrayList, "ExcelCallValue"+i);
    }

    public ArrayList<String> getexcelCallAnalysis(int i) {
        return ApplicationSettings.getValue("ExcelCallValue"+i);
    }

    public void setexcelCallHeader(ArrayList<String> arrayList) {
        ApplicationSettings.putValue(arrayList, "ExcelCallHeader");
    }

    public ArrayList<String> getexcelCallHeader() {
        return ApplicationSettings.getValue("ExcelCallHeader");
    }

    public void setcallRowSize(int i){
        ApplicationSettings.putPref("CallrowSize", i);
    }
    public int getCallrowSize() {
        return ApplicationSettings.getPref("CallrowSize",0);
    }

    /*Excel Appointment Data*/
    public  void setAppointmentExcelHeader(ArrayList<String> appointmentExcelData){
        ApplicationSettings.putValue(appointmentExcelData,"AppointmentExcelData");

    }

    public ArrayList<String> getAppointmentExcelHeader(){
        return ApplicationSettings.getValue("AppointmentExcelData");
    }

    public void setAppointmentRowValue(ArrayList<String> rowValue, int i){
        ApplicationSettings.putValue(rowValue, "AppointmentExcelRowData"+i);
    }

    public ArrayList<String> getAppointmentRowValue(int i){
        return ApplicationSettings.getValue("AppointmentExcelRowData"+i);
    }

    public void setAppointmentRowSize(int rowSize) {
        ApplicationSettings.putPref("AppointmentExcelRowSize", rowSize);
    }

    public int getAppointmentRowSize() {
        return ApplicationSettings.getPref("AppointmentExcelRowSize", 0);
    }

    public void setUpcoming(String upcoming) {
        ApplicationSettings.putPref("upcoming",upcoming);
    }

    public void setComleted(String comleted) {
        ApplicationSettings.putPref("completed", comleted);
    }

    public void setPending(String pending) {
        ApplicationSettings.putPref("pending", pending);
    }

    public void setTat(String tat) {
        if((tat != null) && (!tat.isEmpty())) {
            Integer tatValue = Integer.parseInt(tat);
            ApplicationSettings.putPref("tat", tatValue);
        }
    }

    public void setTotalTalkTime(int totalTalkTime) {
        /*if((totalTalkTime != null) && (!totalTalkTime.isEmpty())) {
            Integer talkTime = Integer.parseInt(totalTalkTime);*/
            ApplicationSettings.putPref("totalTalkTime", totalTalkTime);
        /*}*/
    }

    public void setTotalCalls(int totalCalls) {

        /*if((totalCalls != null) && (!totalCalls.isEmpty())) {
            Integer talkTime = Integer.parseInt(totalCalls);*/
            ApplicationSettings.putPref("totalCalls", totalCalls);
        /*}*/
    }

    public static void setAgentList(ArrayList<HashMap<String, String>> agentList) {
        UserActivities.agentList = agentList;
    }

    public static ArrayList<HashMap<String,String>> getAgentList() {
       return UserActivities.agentList;
    }

    public static void setMissedAgentList(ArrayList<HashMap<String, String>> agentList) {
        UserActivities.missedagentList = agentList;
    }

    public static ArrayList<HashMap<String,String>> getMissedAgentList() {
        return UserActivities.missedagentList;
    }

}
