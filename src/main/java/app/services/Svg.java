package app.services;

import java.util.Locale;

public class Svg {
    private final StringBuilder svg = new StringBuilder();

    public Svg(int x, int y, String viewBox, String width, String height) {
        svg.append(String.format(
                "<svg x=\"%d\" y=\"%d\" viewBox=\"%s\" width=\"%s\" height=\"%s\" xmlns=\"http://www.w3.org/2000/svg\">",
                x, y, viewBox, width, height
        ));
    }

    public void addRectangle(int x, int y, double height, double width, String style) {
        svg.append(String.format(Locale.US,
                "<rect x=\"%d\" y=\"%d\" height=\"%.2f\" width=\"%.2f\" style=\"%s\" />",
                x, y, height, width, style
        ));
    }

    public void addLine(int x1, int y1, int x2, int y2, String style) {
        svg.append(String.format(
                "<line x1=\"%d\" y1=\"%d\" x2=\"%d\" y2=\"%d\" style=\"%s\" />",
                x1, y1, x2, y2, style
        ));
    }

    public void addText(int x, int y, int rotation, String text) {
        svg.append(String.format(
                "<text x=\"%d\" y=\"%d\" transform=\"rotate(%d %d %d)\" font-size=\"18\" font-family=\"Arial\">%s</text>",
                x, y, rotation, x, y, escapeText(text)
        ));
    }

    @Override
    public String toString() {
        return svg + "</svg>";
    }

    private String escapeText(String text) {
        return text
                .replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;");
    }
}
