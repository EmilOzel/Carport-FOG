package app.entities;

public class MaterialLine {
    private final String name;
    private final String dimensions;
    private final int quantity;
    private final String unit;
    private final String description;
    private final double unitPrice;

    public MaterialLine(String name, String dimensions, int quantity, String unit, String description, double unitPrice) {
        this.name = name;
        this.dimensions = dimensions;
        this.quantity = quantity;
        this.unit = unit;
        this.description = description;
        this.unitPrice = unitPrice;
    }

    public String getName() {
        return name;
    }

    public String getDimensions() {
        return dimensions;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getUnit() {
        return unit;
    }

    public String getDescription() {
        return description;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public double getTotalPrice() {
        return quantity * unitPrice;
    }
}
