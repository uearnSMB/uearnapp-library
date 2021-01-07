package smarter.uearn.money.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class KycDocuments implements Serializable {

    public KycDocuments() {

    }

    @SerializedName("for_user")
    @Expose
    private Integer forUser;

    @SerializedName("doc_name")
    @Expose
    private String docName="";
    @SerializedName("doc_value")
    @Expose
    private String docValue="";
    @SerializedName("doc_url")
    @Expose
    private String docUrl="";
    @SerializedName("file_name")
    @Expose
    private String fileName="";

    @SerializedName("userId")
    @Expose
    private String userId="";

    @SerializedName("isUploaded")
    @Expose
    private Boolean isUploaded=false;


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }


    public Boolean getUploaded() {
        return isUploaded;
    }

    public void setUploaded(Boolean uploaded) {
        isUploaded = uploaded;
    }

    public Integer getForUser() {
        return forUser;
    }

    public void setForUser(Integer forUser) {
        this.forUser = forUser;
    }

    public String getDocName() {
        return docName;
    }

    public void setDocName(String docName) {
        this.docName = docName;
    }

    public String getDocValue() {
        return docValue;
    }

    public void setDocValue(String docValue) {
        this.docValue = docValue;
    }

    public String getDocUrl() {
        return docUrl;
    }

    public void setDocUrl(String docUrl) {
        this.docUrl = docUrl;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

}
