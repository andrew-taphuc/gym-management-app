package view.ownerView;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import model.User;

public class PlansRenewalsController {
    @FXML
    private Label pageTitle;

    private User currentUser;

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    @FXML
    public void initialize() {
        System.out.println(
                "Trang: Plans & Renewals | User: " + (currentUser != null ? currentUser.getUsername() : "null"));
        pageTitle.setText("Đây là trang Plans & Renewals để làm trang điều chỉnh gói tập");
    }
}