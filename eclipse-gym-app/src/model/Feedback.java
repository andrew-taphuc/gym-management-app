package model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Feedback {
    private final StringProperty feedbackType;
    private final StringProperty comment;
    private final StringProperty status;
    private final StringProperty feedbackDate;

    public Feedback(String type, String comment, String status, String date) {
        this.feedbackType = new SimpleStringProperty(type);
        this.comment = new SimpleStringProperty(comment);
        this.status = new SimpleStringProperty(status);
        this.feedbackDate = new SimpleStringProperty(date);
    }

    public StringProperty feedbackTypeProperty() { return feedbackType; }
    public StringProperty commentProperty() { return comment; }
    public StringProperty statusProperty() { return status; }
    public StringProperty feedbackDateProperty() { return feedbackDate; }
}
