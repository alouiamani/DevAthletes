package services;

import entities.Payment;
import utils.MyDatabase;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.UUID;

public class PaymentService {
    private Connection connection;

    public PaymentService() {
        connection = MyDatabase.getInstance().getConnection();
    }

    public Payment processPayment(int commandeId, double amount, String paymentMethod) throws SQLException {
        Payment payment = new Payment();
        payment.setCommandeId(commandeId);
        payment.setAmount(amount);
        payment.setPaymentMethod(paymentMethod);
        payment.setTransactionId(generateTransactionId());
        payment.setStatus("PENDING");

        String sql = "INSERT INTO payments (commande_id, amount, payment_method, status, payment_date, transaction_id) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pst = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pst.setInt(1, commandeId);
            pst.setDouble(2, payment.getAmount());
            pst.setString(3, payment.getPaymentMethod());
            pst.setString(4, payment.getStatus());
            pst.setTimestamp(5, Timestamp.valueOf(payment.getPaymentDate()));
            pst.setString(6, payment.getTransactionId());

            pst.executeUpdate();

            try (ResultSet generatedKeys = pst.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    payment.setId(generatedKeys.getInt(1));
                }
            }
        }

        // Simulate payment processing
        simulatePaymentProcessing(payment);
        updatePaymentStatus(payment);

        return payment;
    }

    private void simulatePaymentProcessing(Payment payment) {
        // Simulate payment processing delay
        try {
            Thread.sleep(1000);
            payment.setStatus("COMPLETED");
        } catch (InterruptedException e) {
            payment.setStatus("FAILED");
        }
    }

    private void updatePaymentStatus(Payment payment) throws SQLException {
        String sql = "UPDATE payments SET status = ? WHERE id = ?";
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setString(1, payment.getStatus());
            pst.setInt(2, payment.getId());
            pst.executeUpdate();
        }
    }

    private String generateTransactionId() {
        return "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
} 