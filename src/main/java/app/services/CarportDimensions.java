package app.services;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class CarportDimensions {
    public static final int SHED_WIDTH_DIFFERENCE = 60;

    public static final List<Integer> WIDTH_OPTIONS = range(240, 780, 60);
    public static final List<Integer> LENGTH_OPTIONS = range(240, 1200, 60);
    public static final List<Integer> HEIGHT_OPTIONS = List.of(210, 230, 250);
    public static final List<Integer> SHED_WIDTH_OPTIONS = range(180, 720, 60);
    public static final List<Integer> SHED_LENGTH_OPTIONS = range(180, 400, 60);

    public static final int DEFAULT_WIDTH = 600;
    public static final int DEFAULT_LENGTH = 780;
    public static final int DEFAULT_HEIGHT = 230;
    public static final int DEFAULT_SHED_WIDTH = DEFAULT_WIDTH - SHED_WIDTH_DIFFERENCE;
    public static final int DEFAULT_SHED_LENGTH = 240;

    private CarportDimensions() {
    }

    public static boolean isValidWidth(int width) {
        return WIDTH_OPTIONS.contains(width);
    }

    public static boolean isValidLength(int length) {
        return LENGTH_OPTIONS.contains(length);
    }

    public static boolean isValidHeight(int height) {
        return HEIGHT_OPTIONS.contains(height);
    }

    public static boolean isValidShedWidth(int shedWidth) {
        return SHED_WIDTH_OPTIONS.contains(shedWidth);
    }

    public static boolean isValidShedLength(int shedLength) {
        return SHED_LENGTH_OPTIONS.contains(shedLength);
    }

    public static boolean isValidShedWidthForCarport(int shedWidth, int carportWidth) {
        return isValidShedWidth(shedWidth) && shedWidth == carportWidth - SHED_WIDTH_DIFFERENCE;
    }

    private static List<Integer> range(int first, int last, int step) {
        List<Integer> values = new ArrayList<>();

        for (int value = first; value <= last; value += step) {
            values.add(value);
        }

        return Collections.unmodifiableList(values);
    }
}
