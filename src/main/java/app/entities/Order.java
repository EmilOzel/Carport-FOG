package app.entities;

import java.time.LocalDateTime;

public class Order {
    private int id;
    private int userId;
    private int carportId;
    private String status;
    private LocalDateTime createdAt;
    private double totalPrice;
    private double basePrice;
    private double discountPercent;
    private double discountAmount;

    public Order(int id, int userId, int carportId, String status, LocalDateTime createdAt, double totalPrice) {
        this.id = id;
        this.userId = userId;
        this.carportId = carportId;
        this.status = status;
        this.createdAt = createdAt;
        this.totalPrice = totalPrice;
    }
    public Order(double basePrice, double discountPercent, double discountAmount) {
        this.basePrice = basePrice;
        this.discountPercent = discountPercent;
        this.discountAmount = discountAmount;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getCarportId() {
        return carportId;
    }

    public void setCarportId(int carportId) {
        this.carportId = carportId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }
    public double getBasePrice() {
        return basePrice;
    }
    public void setBasePrice(double basePrice) {
        this.basePrice = basePrice;
    }
    public double getDiscountPercent() {
        return discountPercent;
    }
    public void setDiscountPercent(double discountPercent) {
        this.discountPercent = discountPercent;
    }
    public double getDiscountAmount() {
        return discountAmount;
    }
    public void setDiscountAmount(double discountAmount) {
        this.discountAmount = discountAmount;
    }

}
