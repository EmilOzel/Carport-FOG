package app.entities;

public class Carport {
    private int id;
    private int width;
    private int length;
    private int height;
    private boolean hasShed;
    private int shedWidth;
    private int shedLength;

    public Carport(int width, int length, int height, boolean hasShed, int shedWidth, int shedLength) {
        this.width = width;
        this.length = length;
        this.height = height;
        this.hasShed = hasShed;
        this.shedWidth = shedWidth;
        this.shedLength = shedLength;
    }

    public int getShedLength() {
        return shedLength;
    }

    public void setShedLength(int shedLength) {
        this.shedLength = shedLength;
    }

    public int getShedWidth() {
        return shedWidth;
    }

    public void setShedWidth(int shedWidth) {
        this.shedWidth = shedWidth;
    }

    public boolean isHasShed() {
        return hasShed;
    }

    public void setHasShed(boolean hasShed) {
        this.hasShed = hasShed;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
