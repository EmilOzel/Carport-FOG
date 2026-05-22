package app.services;

import app.entities.Carport;
import app.entities.MaterialLine;
import app.entities.RoofType;

import java.util.ArrayList;
import java.util.List;

public class StyklisteCalculator {
    private static final int RAFTER_SPACING_CM = 55;
    private static final double POST_PRICE = 400;
    private static final double BEAM_PRICE = 950;
    private static final double RAFTER_PRICE = 325;
    private static final double BATTEN_PRICE = 55;
    private static final double FASCIA_PRICE = 150;
    private static final double CLADDING_PRICE = 35;
    private static final double ROOF_TILE_PRICE = 16;
    private static final double MOUNTING_PACKAGE_PRICE = 1200;

    public int calculatePosts(Carport carport) {
        if (carport.isHasShed()) {
            return 8;
        }

        return 6;
    }

    public int calculateBeams(Carport carport) {
        return 2;
    }

    public int calculateRafters(Carport carport) {
        return (int) Math.ceil(carport.getLength() / (double) RAFTER_SPACING_CM);
    }

    public List<MaterialLine> calculateMaterialList(Carport carport) {
        List<MaterialLine> materialLines = new ArrayList<>();

        materialLines.add(new MaterialLine(
                "Stolpe",
                postDimensions(carport),
                calculatePosts(carport),
                "stk.",
                "Trykimprægneret stolpe",
                POST_PRICE
        ));

        materialLines.add(new MaterialLine(
                "Rem",
                "45 x 195 mm",
                calculateBeams(carport),
                "stk.",
                beamDescription(carport),
                BEAM_PRICE
        ));

        materialLines.add(new MaterialLine(
                "Spær",
                rafterDimensions(carport),
                calculateRafters(carport),
                "stk.",
                rafterDescription(carport),
                RAFTER_PRICE
        ));

        materialLines.add(new MaterialLine(
                "Lægte",
                "T1 38 x 73 mm",
                calculateBattens(carport),
                "stk.",
                "Lægter til tagkonstruktion",
                BATTEN_PRICE
        ));

        materialLines.add(new MaterialLine(
                "Stern",
                "25 x 150 mm",
                calculateFasciaBoards(),
                "stk.",
                "Trykimprægneret sternbræt",
                FASCIA_PRICE
        ));

        if (carport.isHasShed()) {
            materialLines.add(new MaterialLine(
                    "Beklædning",
                    "19 x 100 mm",
                    calculateShedCladding(carport),
                    "stk.",
                    "Trykimprægneret beklædning til redskabsrum",
                    CLADDING_PRICE
            ));
        }

        materialLines.add(new MaterialLine(
                "Tag",
                "Betontagsten",
                calculateRoofTiles(carport),
                "stk.",
                "Sort betontagsten",
                ROOF_TILE_PRICE
        ));

        materialLines.add(new MaterialLine(
                "Søm, skruer og beslag",
                "-",
                1,
                "pakke",
                "Samlet pakke til montering",
                MOUNTING_PACKAGE_PRICE
        ));

        return materialLines;
    }

    private String postDimensions(Carport carport) {
        if (carport.getWidth() >= 600) {
            return "125 x 125 mm";
        }

        return "100 x 100 mm";
    }

    private String beamDescription(Carport carport) {
        if (carport.getWidth() >= 600) {
            return "Spærtræ dobbelt";
        }

        return "Spærtræ";
    }

    private String rafterDimensions(Carport carport) {
        if (carport.getRoofType() == RoofType.RAISED) {
            return "Tilskåret spær";
        }

        return "45 x 120 mm";
    }

    private String rafterDescription(Carport carport) {
        if (carport.getRoofType() == RoofType.RAISED) {
            return "Tilskåret spær - ikke samlet";
        }

        return "Spær til fladt tag";
    }

    private int calculateBattens(Carport carport) {
        return calculateRafters(carport) * 2;
    }

    private int calculateFasciaBoards() {
        return 4;
    }

    private int calculateShedCladding(Carport carport) {
        int shedPerimeter = 2 * (carport.getShedWidth() + carport.getShedLength());
        int boardWidth = 10;

        return (int) Math.ceil(shedPerimeter / (double) boardWidth);
    }

    private int calculateRoofTiles(Carport carport) {
        double roofArea = (carport.getWidth() / 100.0) * (carport.getLength() / 100.0);
        int tilesPerSquareMeter = 10;

        return (int) Math.ceil(roofArea * tilesPerSquareMeter);
    }
}
