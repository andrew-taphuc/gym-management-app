package model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Feedback {
    private final IntegerProperty id;
    private final StringProperty memberName;
    private final StringProperty feedbackType;
    private final StringProperty comment;
    private final StringProperty status;
    private final StringProperty feedbackDate;
    private final StringProperty responseComment;
    private final StringProperty responseDate;

     // Constructor cho Admin (có đủ thông tin)
    public Feedback(int id, String memberName, String type, String comment,
                    String status, String feedbackDate,
                    String responseComment, String responseDate) {
        this.id = new SimpleIntegerProperty(id);
        this.memberName = new SimpleStringProperty(memberName);
        this.feedbackType = new SimpleStringProperty(type);
        this.comment = new SimpleStringProperty(comment);
        this.status = new SimpleStringProperty(status);
        this.feedbackDate = new SimpleStringProperty(feedbackDate);
        this.responseComment = new SimpleStringProperty(responseComment);
        this.responseDate = new SimpleStringProperty(responseDate);
    }

    //Constructer cho member
    public Feedback(String type, String comment, String status, String date, String responseComment) {
        this.id = new SimpleIntegerProperty(0);
        this.memberName = new SimpleStringProperty("");
        this.feedbackType = new SimpleStringProperty(type);
        this.comment = new SimpleStringProperty(comment);
        this.status = new SimpleStringProperty(status);
        this.feedbackDate = new SimpleStringProperty(date);
        this.responseComment = new SimpleStringProperty(responseComment);
        this.responseDate = new SimpleStringProperty("");
    }

    // Properties (dùng cho binding nếu cần)
    public IntegerProperty idProperty() { return id; }
    public StringProperty memberNameProperty() { return memberName; }
    public StringProperty feedbackTypeProperty() { return feedbackType; }
    public StringProperty commentProperty() { return comment; }
    public StringProperty statusProperty() { return status; }
    public StringProperty feedbackDateProperty() { return feedbackDate; }
    public StringProperty responseCommentProperty() { return responseComment; }
    public StringProperty responseDateProperty() { return responseDate; }

    // Getters
    public int getId() { return id.get(); }
    public String getMemberName() { return memberName.get(); }
    public String getFeedbackType() { return feedbackType.get(); }
    public String getComment() { return comment.get(); }
    public String getStatus() { return status.get(); }
    public String getFeedbackDate() { return feedbackDate.get(); }
    public String getResponseComment() { return responseComment.get(); }
    public String getResponseDate() { return responseDate.get(); }
}
