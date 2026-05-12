package app.entities;

import java.time.LocalDateTime;

public class Order {
    private int id;
    private int userId;
    private int carportId;
    private String status;
    private LocalDateTime createdAt;
    private double totalPrice;

    public Order(int id, int userId, int carportId, String status, LocalDateTime createdAt, double totalPrice) {
        this.id = id;
        this.userId = userId;
        this.carportId = carportId;
        this.status = status;
        this.createdAt = createdAt;
        this.totalPrice = totalPrice;
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
}
