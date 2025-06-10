package controller;

import model.Payment;
import model.enums.enum_PaymentStatus;
import utils.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PaymentController {
    private Connection connection;

    public PaymentController() {
        this.connection = DBConnection.getConnection();
    }

    public Payment getPaymentByID(int paymentID) {
        String query = "SELECT * FROM Payments WHERE PaymentID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, paymentID);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Payment payment = new Payment();
                payment.setPaymentId(rs.getInt("PaymentID"));
                payment.setAmount(rs.getDouble("Amount"));
                payment.setPaymentDate(rs.getTimestamp("PaymentDate").toLocalDateTime());
                payment.setPaymentMethod(rs.getString("PaymentMethod"));
                payment.setStatus(enum_PaymentStatus.fromValue(rs.getString("Status")));
                payment.setPromotionId(rs.getObject("PromotionId") != null ? rs.getInt("PromotionId") : null);
                payment.setNotes(rs.getString("Notes"));
                return payment;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int createPayment(Payment payment) {
        String query = "INSERT INTO Payments (Amount, PaymentDate, PaymentMethod, Status, PromotionId, Notes) " +
                "VALUES (?, ?, ?, ?::payment_status_enum, ?, ?) RETURNING PaymentID";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setDouble(1, payment.getAmount());
            stmt.setTimestamp(2, java.sql.Timestamp.valueOf(payment.getPaymentDate()));
            stmt.setString(3, payment.getPaymentMethod());
            stmt.setString(4, payment.getStatus().getValue());
            // Dùng setObject để nhận cả null cho PromotionId
            if (payment.getPromotionId() != null) {
                stmt.setInt(5, payment.getPromotionId());
            } else {
                stmt.setNull(5, java.sql.Types.INTEGER);
            }
            stmt.setString(6, payment.getNotes());

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                int paymentId = rs.getInt("PaymentID");
                payment.setPaymentId(paymentId);
                return paymentId;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public boolean updatePaymentStatus(int paymentID, enum_PaymentStatus newStatus) {
        String query = "UPDATE Payments SET Status = ?::payment_status_enum WHERE PaymentID = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, newStatus.getValue());
            stmt.setInt(2, paymentID);

            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public double getTotalPayments() {
        String query = "SELECT SUM(Amount) FROM Payments WHERE Status = 'Thành công'";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getDouble(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }
}
