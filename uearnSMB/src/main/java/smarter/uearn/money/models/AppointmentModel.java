package smarter.uearn.money.models;

/**
 * Created by dilipkumar4813 on 27/03/17.
 */

public class AppointmentModel {

    private String mAppointmentId, mSubject;
    private long mStartTime;
    private long mEndTime;
    private long mLongItemId;
    private int mUploadStatus;
    private String mStatus;
    private String mName, mPhone;
    private String mExternalCalendar;
    private String notes;
    private String orderPotential;

    public AppointmentModel(long itemId, String name, String phone, long mStartTime, long mEndTime, String mAppointmentId, String mSubject, String status, int mUploadStatus, String externalCalendar, String notes, String orderPotential) {
        this.mLongItemId = itemId;
        this.mName = name;
        this.mPhone = phone;
        this.mStartTime = mStartTime;
        this.mEndTime = mEndTime;
        this.mAppointmentId = mAppointmentId;
        this.mSubject = mSubject;
        this.mStatus = status;
        this.mUploadStatus = mUploadStatus;
        this.mExternalCalendar = externalCalendar;
        this.notes = notes;
        this.orderPotential = orderPotential;
    }

    public long getmLongItemId() {
        return mLongItemId;
    }

    public String getmName() {
        return mName;
    }

    public String getmPhone() {
        return mPhone;
    }

    public long getmStartTime() {
        return mStartTime;
    }

    public long getmEndTime() {
        return mEndTime;
    }

    public String getmAppointmentId() {
        return mAppointmentId;
    }

    public String getmSubject() {
        return mSubject;
    }

    public String getmStatus() {
        return mStatus;
    }

    public int getmUploadStatus() {
        return mUploadStatus;
    }

    public String getmExternalCalendar() {
        return mExternalCalendar;
    }

    public String getmNote() {
        return notes;
    }

    public String getmOrderPotential() {
        return orderPotential;
    }




}
