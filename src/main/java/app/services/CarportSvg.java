package app.services;

import app.entities.Carport;

import java.util.Locale;

public class CarportSvg {
    private static final int SIDE_X = 120;
    private static final int SIDE_Y = 90;
    private static final int TOP_X = 120;
    private static final int TOP_Y = 380;

    private static final int MAX_LENGTH = 1200;
    private static final int MAX_WIDTH = 780;
    private static final int BEAM_OFFSET_CM = 30;
    private static final int RAFTER_SPACING_CM = 55;

    private static final String LINE = "stroke:black;stroke-width:2;fill:none";
    private static final String ROOF_LINE = "stroke:black;stroke-width:4;fill:none";
    private static final String BEAM_LINE = "stroke:black;stroke-width:3.5;fill:none";
    private static final String THIN_LINE = "stroke:black;stroke-width:1;fill:none";
    private static final String DASHED_LINE = "stroke:black;stroke-width:2;stroke-dasharray:8 6;fill:none";
    private static final String POST = "stroke:black;stroke-width:2;fill:white";

    private final Svg svgDrawing;
    private final Carport carport;
    private final double scale;

    public CarportSvg(Carport carport) {
        this.carport = carport;
        this.scale = findScale();
        this.svgDrawing = new Svg(0, 0, "0 0 1000 900", "100%", "auto");
    }

    public String toString() {
        addSideView();
        addTopView();
        return svgDrawing.toString();
    }

    private void addSideView() {
        int x = SIDE_X;
        int y = SIDE_Y;
        int length = toPixels(carport.getLength());
        int height = toPixels(carport.getHeight());
        int groundY = y + height;
        int roofY = y;
        int shedLength = carport.isHasShed() ? toPixels(carport.getShedLength()) : 0;
        int shedX = findShedX(x, length, shedLength);
        int shedEndX = shedX + shedLength;

        svgDrawing.addLine(x, groundY, x + length, groundY, LINE);
        svgDrawing.addLine(x, roofY, x + length, roofY, ROOF_LINE);
        svgDrawing.addLine(x, roofY + 20, x + length, roofY + 20, THIN_LINE);

        addSidePosts(x, length, shedX, shedEndX, roofY, groundY);

        if (carport.isHasShed()) {
            svgDrawing.addRectangle(shedX, roofY + 35, groundY - roofY - 35, shedLength, LINE);
            addBoards(shedX, roofY + 35, groundY, shedLength);
        }

        addHorizontalMeasure(x, groundY + 45, x + length, formatMeasure(carport.getLength()));
        addVerticalMeasure(x - 50, roofY, groundY, formatMeasure(carport.getHeight()));
    }

    private void addTopView() {
        int x = TOP_X;
        int y = TOP_Y;
        int length = toPixels(carport.getLength());
        int width = toPixels(carport.getWidth());
        int[] beamYs = beamYPositions(y, width);
        int shedLength = carport.isHasShed() ? toPixels(carport.getShedLength()) : 0;
        int shedX = findShedX(x, length, shedLength);
        int shedEndX = shedX + shedLength;
        int shedY = beamYs[0];
        int shedEndY = beamYs[1];
        int shedWidth = shedEndY - shedY;

        svgDrawing.addRectangle(x, y, width, length, LINE);
        addRafters(x, y, length, width);
        addBeams(x, x + length, beamYs);
        addPosts(createCarportPostPositions(x, length), beamYs);
        addCross(x, y, shedX - x, width);

        if (carport.isHasShed()) {
            svgDrawing.addRectangle(shedX, shedY, shedWidth, shedLength, LINE);
            addBoards(shedX, shedY, shedY + shedWidth, shedLength);
            addBeams(shedX, shedEndX, new int[] {shedY, shedEndY});
            addPosts(new int[] {shedX, shedEndX}, new int[] {shedY, shedEndY});
            addHorizontalMeasure(shedX, shedEndY + 28, shedEndX, formatMeasure(carport.getShedLength()));
            addVerticalMeasure(shedEndX + 32, shedY, shedEndY, formatMeasure(carport.getShedWidth()));
        }

        addHorizontalMeasure(x, y + width + 50, x + length, formatMeasure(carport.getLength()));
        addVerticalMeasure(x - 50, y, y + width, formatMeasure(carport.getWidth()));
    }

    private int[] beamYPositions(int y, int width) {
        int beamOffset = toPixels(BEAM_OFFSET_CM);
        return new int[] {y + beamOffset, y + width - beamOffset};
    }

    private void addBeams(int x1, int x2, int[] beamYs) {
        for (int beamY : beamYs) {
            svgDrawing.addLine(x1, beamY, x2, beamY, BEAM_LINE);
        }
    }

    private void addRafters(int x, int y, int length, int width) {
        int spacing = toPixels(RAFTER_SPACING_CM);

        for (int currentX = x + spacing; currentX < x + length; currentX += spacing) {
            svgDrawing.addLine(currentX, y, currentX, y + width, THIN_LINE);
            addSmallMeasure(currentX - spacing, y - 25, currentX);
        }
    }

    private void addPosts(int[] postXs, int[] postYs) {
        for (int postX : postXs) {
            for (int postY : postYs) {
                addPost(postX, postY);
            }
        }
    }

    private void addSidePosts(int x, int length, int shedX, int shedEndX, int topY, int groundY) {
        int[] postPositions = createCarportPostPositions(x, length);

        for (int postX : postPositions) {
            addSidePost(postX, topY, groundY);
        }

        if (carport.isHasShed()) {
            addSidePost(shedX, topY, groundY);
            addSidePost(shedEndX, topY, groundY);
        }
    }

    private int[] createCarportPostPositions(int x, int length) {
        int firstPost = x + toPixels(100);
        int middlePost = x + length / 2;
        int endPost = x + length - toPixels(20);

        if (carport.isHasShed()) {
            return new int[] {firstPost, middlePost};
        }

        return new int[] {firstPost, middlePost, endPost};
    }

    private void addPost(int x, int y) {
        svgDrawing.addRectangle(x - 7, y - 7, 14, 14, POST);
    }

    private void addSidePost(int x, int y1, int y2) {
        svgDrawing.addRectangle(x - 5, y1, y2 - y1, 10, POST);
    }

    private void addCross(int x, int y, int length, int width) {
        int margin = toPixels(70);

        svgDrawing.addLine(x + margin, y + margin, x + length - margin, y + width - margin, DASHED_LINE);
        svgDrawing.addLine(x + margin, y + width - margin, x + length - margin, y + margin, DASHED_LINE);
    }

    private int findShedX(int x, int length, int shedLength) {
        if (!carport.isHasShed()) {
            return x + length;
        }

        int lastThirdStart = x + length * 2 / 3;
        int lastThirdLength = length / 3;

        return lastThirdStart + (lastThirdLength - shedLength) / 2;
    }

    private void addBoards(int x, int y1, int y2, int width) {
        for (int currentX = x + 12; currentX < x + width; currentX += 12) {
            svgDrawing.addLine(currentX, y1, currentX, y2, THIN_LINE);
        }
    }

    private void addHorizontalMeasure(int x1, int y, int x2, String text) {
        svgDrawing.addLine(x1, y, x2, y, THIN_LINE);
        svgDrawing.addLine(x1, y - 10, x1, y + 10, THIN_LINE);
        svgDrawing.addLine(x2, y - 10, x2, y + 10, THIN_LINE);
        svgDrawing.addText((x1 + x2) / 2 - 20, y + 25, 0, text);
    }

    private void addVerticalMeasure(int x, int y1, int y2, String text) {
        svgDrawing.addLine(x, y1, x, y2, THIN_LINE);
        svgDrawing.addLine(x - 10, y1, x + 10, y1, THIN_LINE);
        svgDrawing.addLine(x - 10, y2, x + 10, y2, THIN_LINE);
        svgDrawing.addText(x - 20, (y1 + y2) / 2 + 20, -90, text);
    }

    private void addSmallMeasure(int x1, int y, int x2) {
        svgDrawing.addLine(x1, y, x2, y, THIN_LINE);
        svgDrawing.addLine(x1, y - 6, x1, y + 6, THIN_LINE);
        svgDrawing.addLine(x2, y - 6, x2, y + 6, THIN_LINE);
        svgDrawing.addText(x1 + 10, y - 6, 0, "0,55");
    }

    private double findScale() {
        double lengthScale = MAX_LENGTH / (double) carport.getLength();
        double widthScale = MAX_WIDTH / (double) carport.getWidth();

        return Math.min(lengthScale, widthScale);
    }

    private int toPixels(int cm) {
        return (int) Math.round(cm * scale);
    }

    private String formatMeasure(int valueInCm) {
        return String.format(Locale.forLanguageTag("da-DK"), "%.2f", valueInCm / 100.0);
    }
}
