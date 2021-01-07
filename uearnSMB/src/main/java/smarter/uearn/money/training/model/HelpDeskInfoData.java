package smarter.uearn.money.training.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class HelpDeskInfoData {

    @SerializedName("helpdesk_id")
    @Expose
    private String helpdeskId;
    @SerializedName("question")
    @Expose
    private String question;
    @SerializedName("question_type")
    @Expose
    private String questionType;
    @SerializedName("question_id")
    @Expose
    private String questionId;

    public String getHelpdeskId() {
        return helpdeskId;
    }

    public void setHelpdeskId(String helpdeskId) {
        this.helpdeskId = helpdeskId;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getQuestionType() {
        return questionType;
    }

    public void setQuestionType(String questionType) {
        this.questionType = questionType;
    }

    public String getQuestionId() {
        return questionId;
    }

    public void setQuestionId(String questionId) {
        this.questionId = questionId;
    }
    private String strAnswer=null;
    private String questionID = null;


    public String getStrAnswer() {
        return strAnswer;
    }

    public void setStrAnswer(String strAnswer) {
        this.strAnswer = strAnswer;
    }

    public String getQuestionID() {
        return questionID;
    }

    public void setQuestionID(String questionID) {
        this.questionID = questionID;
    }
}
