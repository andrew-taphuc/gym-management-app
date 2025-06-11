package controller;

import model.Payment;
import model.enums.enum_PaymentStatus;
import utils.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class PaymentController {
    private Connection connection;

    public PaymentController() {
        this.connection = DBConnection.getConnection();
    }

    /**
     * Class để chứa thông tin payment hiển thị trên bảng
     */
    public static class PaymentInfo {
        private final int paymentId;
        private final String amount;
        private final String paymentDate;
        private final String paymentMethod;
        private final String status;
        private final String serviceType;
        private final String memberName;
        private final String invoiceCode;

        public PaymentInfo(int paymentId, String amount, String paymentDate,
                String paymentMethod, String status, String serviceType, String memberName, String invoiceCode) {
            this.paymentId = paymentId;
            this.amount = amount;
            this.paymentDate = paymentDate;
            this.paymentMethod = paymentMethod;
            this.status = status;
            this.serviceType = serviceType;
            this.memberName = memberName;
            this.invoiceCode = invoiceCode;
        }

        // Getters
        public int getPaymentId() {
            return paymentId;
        }

        public String getAmount() {
            return amount;
        }

        public String getPaymentDate() {
            return paymentDate;
        }

        public String getPaymentMethod() {
            return paymentMethod;
        }

        public String getStatus() {
            return status;
        }

        public String getServiceType() {
            return serviceType;
        }

        public String getMemberName() {
            return memberName;
        }

        public String getInvoiceCode() {
            return invoiceCode;
        }
    }

    /**
     * Lấy danh sách tất cả payments với thông tin chi tiết
     */
    public List<PaymentInfo> getAllPaymentsWithDetails() {
        List<PaymentInfo> paymentInfos = new ArrayList<>();

        String query = """
                SELECT DISTINCT
                    p.PaymentID,
                    p.Amount,
                    p.PaymentDate,
                    p.PaymentMethod,
                    p.Status,
                    COALESCE(mp.PlanName, tp.PlanName, 'Không xác định') as ServiceType,
                    u.FullName as MemberName,
                    i.InvoiceCode
                FROM Payments p
                LEFT JOIN Memberships m ON p.PaymentID = m.PaymentID
                LEFT JOIN MembershipPlans mp ON m.PlanID = mp.PlanID
                LEFT JOIN Members mem1 ON m.MemberID = mem1.MemberID
                LEFT JOIN TrainingRegistrations tr ON p.PaymentID = tr.PaymentID
                LEFT JOIN TrainingPlans tp ON tr.PlanID = tp.PlanID
                LEFT JOIN Members mem2 ON tr.MemberID = mem2.MemberID
                LEFT JOIN Users u ON COALESCE(mem1.UserID, mem2.UserID) = u.UserID
                LEFT JOIN Invoices i ON p.PaymentID = i.PaymentID
                ORDER BY p.PaymentDate DESC
                """;

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                PaymentInfo paymentInfo = new PaymentInfo(
                        rs.getInt("PaymentID"),
                        String.format("%,.0f VNĐ", rs.getDouble("Amount")),
                        rs.getTimestamp("PaymentDate").toLocalDateTime()
                                .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                        rs.getString("PaymentMethod"),
                        rs.getString("Status"),
                        rs.getString("ServiceType"),
                        rs.getString("MemberName") != null ? rs.getString("MemberName") : "Không xác định",
                        rs.getString("InvoiceCode") != null ? rs.getString("InvoiceCode") : "-");
                paymentInfos.add(paymentInfo);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return paymentInfos;
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
