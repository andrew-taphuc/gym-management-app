package view.trainerView;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import model.User;

public class WorkoutsController {
    @FXML
    private Label pageTitle;

    private User currentUser;

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    @FXML
    public void initialize() {
        System.out.println("Trang: Workouts | User: " + (currentUser != null ? currentUser.getUsername() : "null"));
        pageTitle.setText("Đây là trang Workouts trainer");
    }
}