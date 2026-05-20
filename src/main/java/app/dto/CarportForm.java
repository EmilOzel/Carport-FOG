package app.dto;

public class CarportForm {
    private final int width;
    private final int length;
    private final int height;
    private final String roofType;
    private final boolean hasShed;
    private final int shedWidth;
    private final int shedLength;

    public CarportForm(int width, int length, int height, String roofType, boolean hasShed, int shedWidth, int shedLength) {
        this.width = width;
        this.length = length;
        this.height = height;
        this.roofType = roofType;
        this.hasShed = hasShed;
        this.shedWidth = shedWidth;
        this.shedLength = shedLength;
    }

    public int getWidth() {
        return width;
    }

    public int getLength() {
        return length;
    }

    public int getHeight() {
        return height;
    }

    public String getRoofType() {
        return roofType;
    }

    public boolean isHasShed() {
        return hasShed;
    }

    public int getShedWidth() {
        return shedWidth;
    }

    public int getShedLength() {
        return shedLength;
    }
}
