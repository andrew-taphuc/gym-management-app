package controller;

import model.Invoice;
import utils.DBConnection;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class InvoiceController {
    private Connection connection;

    public InvoiceController() {
        this.connection = DBConnection.getConnection();
    }

    /**
     * Tìm service type từ payment ID
     */
    private String getServiceTypeByPaymentId(int paymentId) {
        // Tìm trong bảng Memberships trước
        String membershipQuery = "SELECT mp.PlanName " +
                "FROM Memberships m " +
                "JOIN MembershipPlans mp ON m.PlanID = mp.PlanID " +
                "WHERE m.PaymentID = ?";

        try (PreparedStatement stmt = connection.prepareStatement(membershipQuery)) {
            stmt.setInt(1, paymentId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("PlanName");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Nếu không tìm thấy trong Memberships, tìm trong TrainingRegistrations
        String trainingQuery = "SELECT tp.PlanName " +
                "FROM TrainingRegistrations tr " +
                "JOIN TrainingPlans tp ON tr.PlanID = tp.PlanID " +
                "WHERE tr.PaymentID = ?";

        try (PreparedStatement stmt = connection.prepareStatement(trainingQuery)) {
            stmt.setInt(1, paymentId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getString("PlanName");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return "Dịch vụ không xác định"; // Default service type
    }

    /**
     * Tìm member ID từ payment ID
     */
    private int getMemberIdByPaymentId(int paymentId) {
        // Tìm trong bảng Memberships trước
        String membershipQuery = "SELECT MemberID FROM Memberships WHERE PaymentID = ?";

        try (PreparedStatement stmt = connection.prepareStatement(membershipQuery)) {
            stmt.setInt(1, paymentId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("MemberID");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Nếu không tìm thấy trong Memberships, tìm trong TrainingRegistrations
        String trainingQuery = "SELECT MemberID FROM TrainingRegistrations WHERE PaymentID = ?";

        try (PreparedStatement stmt = connection.prepareStatement(trainingQuery)) {
            stmt.setInt(1, paymentId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("MemberID");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1; // Không tìm thấy
    }

    /**
     * Tự động tạo mã hóa đơn tiếp theo
     */
    private String generateNextInvoiceCode() {
        String query = "SELECT InvoiceCode FROM Invoices WHERE InvoiceCode LIKE 'HD%' ORDER BY InvoiceCode DESC LIMIT 1";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String lastCode = rs.getString("InvoiceCode");
                // Lấy phần số từ mã cuối cùng (HD0001 -> 0001)
                String numberPart = lastCode.substring(2);
                int nextNumber = Integer.parseInt(numberPart) + 1;
                // Format lại thành HD + 4 chữ số
                return String.format("HD%04d", nextNumber);
            } else {
                // Nếu chưa có hóa đơn nào, bắt đầu từ HD0001
                return "HD0001";
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Nếu có lỗi, trả về mã mặc định với timestamp để tránh trùng lặp
            return "HD" + System.currentTimeMillis() % 10000;
        }
    }

    /**
     * Tạo hóa đơn mới với mã tự động
     */
    public int createInvoice(int paymentId, int createdBy) {
        // Tạo mã hóa đơn tự động
        String invoiceCode = generateNextInvoiceCode();

        // Lấy thông tin từ payment
        String paymentQuery = "SELECT Amount, PromotionID FROM Payments WHERE PaymentID = ?";
        double totalAmount = 0;
        double discountAmount = 0;

        try (PreparedStatement stmt = connection.prepareStatement(paymentQuery)) {
            stmt.setInt(1, paymentId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                totalAmount = rs.getDouble("Amount");

                // Nếu có promotion, tính discount
                Integer promotionId = rs.getObject("PromotionID") != null ? rs.getInt("PromotionID") : null;
                if (promotionId != null) {
                    discountAmount = calculateDiscountAmount(promotionId, totalAmount);
                }
            } else {
                return -1; // Payment không tồn tại
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return -1;
        }

        // Lấy service type và member ID
        String serviceType = getServiceTypeByPaymentId(paymentId);
        int memberId = getMemberIdByPaymentId(paymentId);

        if (memberId == -1) {
            return -1; // Không tìm thấy member
        }

        double finalAmount = totalAmount - discountAmount;

        // Tạo hóa đơn
        String insertQuery = "INSERT INTO Invoices (InvoiceCode, PaymentID, MemberID, ServiceType, " +
                "TotalAmount, DiscountAmount, FinalAmount, CreatedBy) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?) RETURNING InvoiceID";

        try (PreparedStatement stmt = connection.prepareStatement(insertQuery)) {
            stmt.setString(1, invoiceCode);
            stmt.setInt(2, paymentId);
            stmt.setInt(3, memberId);
            stmt.setString(4, serviceType);
            stmt.setDouble(5, totalAmount);
            stmt.setDouble(6, discountAmount);
            stmt.setDouble(7, finalAmount);
            stmt.setInt(8, createdBy);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt("InvoiceID");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

    /**
     * Tính toán số tiền giảm giá từ promotion
     */
    private double calculateDiscountAmount(int promotionId, double totalAmount) {
        String query = "SELECT DiscountType, DiscountValue FROM Promotions WHERE PromotionID = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, promotionId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                String discountType = rs.getString("DiscountType");
                double discountValue = rs.getDouble("DiscountValue");

                if ("Phần trăm".equals(discountType)) {
                    return totalAmount * (discountValue / 100.0);
                } else if ("Tiền mặt".equals(discountType)) {
                    return Math.min(discountValue, totalAmount); // Không vượt quá tổng tiền
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return 0.0;
    }

    /**
     * Lấy hóa đơn theo Payment ID
     */
    public Invoice getInvoiceByPaymentId(int paymentId) {
        String query = "SELECT * FROM Invoices WHERE PaymentID = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, paymentId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Invoice invoice = new Invoice();
                invoice.setInvoiceId(rs.getInt("InvoiceID"));
                invoice.setInvoiceCode(rs.getString("InvoiceCode"));
                invoice.setPaymentId(rs.getInt("PaymentID"));
                invoice.setMemberId(rs.getInt("MemberID"));
                invoice.setIssueDate(rs.getTimestamp("IssueDate").toLocalDateTime());
                invoice.setServiceType(rs.getString("ServiceType"));
                invoice.setTotalAmount(rs.getDouble("TotalAmount"));
                invoice.setDiscountAmount(rs.getDouble("DiscountAmount"));
                invoice.setFinalAmount(rs.getDouble("FinalAmount"));
                invoice.setCreatedBy(rs.getInt("CreatedBy"));
                invoice.setCreatedAt(rs.getTimestamp("CreatedAt").toLocalDateTime());

                return invoice;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Lấy hóa đơn theo ID
     */
    public Invoice getInvoiceById(int invoiceId) {
        String query = "SELECT * FROM Invoices WHERE InvoiceID = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, invoiceId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Invoice invoice = new Invoice();
                invoice.setInvoiceId(rs.getInt("InvoiceID"));
                invoice.setInvoiceCode(rs.getString("InvoiceCode"));
                invoice.setPaymentId(rs.getInt("PaymentID"));
                invoice.setMemberId(rs.getInt("MemberID"));
                invoice.setIssueDate(rs.getTimestamp("IssueDate").toLocalDateTime());
                invoice.setServiceType(rs.getString("ServiceType"));
                invoice.setTotalAmount(rs.getDouble("TotalAmount"));
                invoice.setDiscountAmount(rs.getDouble("DiscountAmount"));
                invoice.setFinalAmount(rs.getDouble("FinalAmount"));
                invoice.setCreatedBy(rs.getObject("CreatedBy") != null ? rs.getInt("CreatedBy") : null);
                invoice.setCreatedAt(rs.getTimestamp("CreatedAt").toLocalDateTime());
                return invoice;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Lấy danh sách hóa đơn của một member
     */
    public List<Invoice> getInvoicesByMemberId(int memberId) {
        List<Invoice> invoices = new ArrayList<>();
        String query = "SELECT * FROM Invoices WHERE MemberID = ? ORDER BY IssueDate DESC";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, memberId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Invoice invoice = new Invoice();
                invoice.setInvoiceId(rs.getInt("InvoiceID"));
                invoice.setInvoiceCode(rs.getString("InvoiceCode"));
                invoice.setPaymentId(rs.getInt("PaymentID"));
                invoice.setMemberId(rs.getInt("MemberID"));
                invoice.setIssueDate(rs.getTimestamp("IssueDate").toLocalDateTime());
                invoice.setServiceType(rs.getString("ServiceType"));
                invoice.setTotalAmount(rs.getDouble("TotalAmount"));
                invoice.setDiscountAmount(rs.getDouble("DiscountAmount"));
                invoice.setFinalAmount(rs.getDouble("FinalAmount"));
                invoice.setCreatedBy(rs.getObject("CreatedBy") != null ? rs.getInt("CreatedBy") : null);
                invoice.setCreatedAt(rs.getTimestamp("CreatedAt").toLocalDateTime());
                invoices.add(invoice);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return invoices;
    }

    /**
     * Lấy tất cả hóa đơn
     */
    public List<Invoice> getAllInvoices() {
        List<Invoice> invoices = new ArrayList<>();
        String query = "SELECT * FROM Invoices ORDER BY IssueDate DESC";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Invoice invoice = new Invoice();
                invoice.setInvoiceId(rs.getInt("InvoiceID"));
                invoice.setInvoiceCode(rs.getString("InvoiceCode"));
                invoice.setPaymentId(rs.getInt("PaymentID"));
                invoice.setMemberId(rs.getInt("MemberID"));
                invoice.setIssueDate(rs.getTimestamp("IssueDate").toLocalDateTime());
                invoice.setServiceType(rs.getString("ServiceType"));
                invoice.setTotalAmount(rs.getDouble("TotalAmount"));
                invoice.setDiscountAmount(rs.getDouble("DiscountAmount"));
                invoice.setFinalAmount(rs.getDouble("FinalAmount"));
                invoice.setCreatedBy(rs.getObject("CreatedBy") != null ? rs.getInt("CreatedBy") : null);
                invoice.setCreatedAt(rs.getTimestamp("CreatedAt").toLocalDateTime());
                invoices.add(invoice);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return invoices;
    }

    /**
     * Tính tổng doanh thu từ hóa đơn
     */
    public double getTotalRevenue() {
        String query = "SELECT SUM(FinalAmount) FROM Invoices";

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

    /**
     * Xóa hóa đơn (soft delete - chỉ admin mới có quyền)
     */
    public boolean deleteInvoice(int invoiceId) {
        String query = "DELETE FROM Invoices WHERE InvoiceID = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, invoiceId);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
