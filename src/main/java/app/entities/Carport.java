package app.entities;

public class Carport {
    private int width;
    private int height;
    private String roofType;
    private boolean hasShed;
    private int shedWidth;
    private int shedHeight;

    public Carport(int width, int height, String roofType, boolean hasShed, int shedWidth, int shedHeight) {
        this.width = width;
        this.height = height;
        this.roofType = roofType;
        this.hasShed = hasShed;
        this.shedWidth = shedWidth;
        this.shedHeight = shedHeight;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public String getRoofType() {
        return roofType;
    }

    public void setRoofType(String roofType) {
        this.roofType = roofType;
    }

    public boolean isHasShed() {
        return hasShed;
    }

    public void setHasShed(boolean hasShed) {
        this.hasShed = hasShed;
    }

    public int getShedWidth() {
        return shedWidth;
    }

    public void setShedWidth(int shedWidth) {
        this.shedWidth = shedWidth;
    }

    public int getShedHeight() {
        return shedHeight;
    }

    public void setShedHeight(int shedHeight) {
        this.shedHeight = shedHeight;
    }
}
