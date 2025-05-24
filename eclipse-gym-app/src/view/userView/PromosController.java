package view.userView;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import model.User;

public class PromosController {
    @FXML
    private Label pageTitle;

    private User currentUser;

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    @FXML
    public void initialize() {
        System.out.println("Trang: Promos | User: " + (currentUser != null ? currentUser.getUsername() : "null"));
        pageTitle.setText("Đây là trang Promos user");
    }
}