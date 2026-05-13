package app.services;

import app.entities.Carport;

public class PriceCalculator {

    public double calculateBasePrice(Carport carport) {
        double carportArea = (carport.getWidth() / 100.0) * (carport.getLength() / 100.0);
        double price = carportArea * 750;

        if (carport.isHasShed()) {
            double shedArea = (carport.getShedWidth() / 100.0) * (carport.getShedLength() / 100.0);
            price += shedArea * 1200;
        }

        return price;
    }

    public double applyDiscount(double basePrice, double discountPercent) {
        double safeDiscount = clampDiscount(discountPercent);
        return basePrice - (basePrice * safeDiscount / 100);
    }

    public double calculateDiscountAmount(double basePrice, double discountPercent) {
        double safeDiscount = clampDiscount(discountPercent);
        return basePrice * safeDiscount / 100;
    }

    private double clampDiscount(double discountPercent) {
        if (discountPercent < 0) {
            return 0;
        }

        if (discountPercent > 100) {
            return 100;
        }

        return discountPercent;
    }
}
