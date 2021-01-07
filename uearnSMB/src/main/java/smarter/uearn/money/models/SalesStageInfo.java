package smarter.uearn.money.models;

import smarter.uearn.money.utils.AppConstants;
import smarter.uearn.money.utils.ApplicationSettings;

import java.util.ArrayList;

/**
 * Created by Kavya on 09-08-2016.
 */
public class SalesStageInfo {
    private ArrayList<String> appointmentSalesStage;
    private ArrayList<String> workorderSalesStage;
    private String ameyoNumbers;
    public SalesStageInfo() {
    }

    public ArrayList<String> getAppointmentSalesStage() {
        return appointmentSalesStage;
    }

    public void setAppointmentSalesStage(ArrayList<String> appointmentSalesStage) {
        this.appointmentSalesStage = appointmentSalesStage;
        ApplicationSettings.putAppointmentStatusArrayListPref(appointmentSalesStage);
    }

    public ArrayList<String> getWorkorderSalesStage() {
        return workorderSalesStage;
    }

    public void setWorkorderSalesStage(ArrayList<String> workorderSalesStage) {
        this.workorderSalesStage = workorderSalesStage;
        ApplicationSettings.putWorkOrderStatusArrayListPref(workorderSalesStage);
    }
    public void setAmeyoNumbers(String ameyoNumbers) {
        this.ameyoNumbers = ameyoNumbers;
        //Log.d("ameyo_Numbers",ameyoNumbers );
        ApplicationSettings.putPref(AppConstants.AMEYO_NUMBERS, ameyoNumbers);
    }

    public String getAmeyoNumbers() {
        return ameyoNumbers;
    }

    public void dosave() {
        setAppointmentSalesStage(appointmentSalesStage);
        setWorkorderSalesStage(workorderSalesStage);
        setAmeyoNumbers(ameyoNumbers);
    }

    public void loadSalesStage() {
        appointmentSalesStage = ApplicationSettings.getAppointmentStatusArrayList();
        workorderSalesStage = ApplicationSettings.getWorkOrderStatusArrayList();
    }
}
