package services;

import app.entities.Carport;
import app.entities.MaterialLine;
import app.entities.RoofType;
import app.services.StyklisteCalculator;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class StyklisteCalculatorTest {

    @Test
    void calculatePostsWithoutShed() {
        Carport carport = new Carport(600, 780, 230, RoofType.FLAT, false, 0, 0);
        StyklisteCalculator calculator = new StyklisteCalculator();

        int result = calculator.calculatePosts(carport);

        assertEquals(6, result);
    }

    @Test
    void calculatePostsWithShed() {
        Carport carport = new Carport(600, 780, 230, RoofType.FLAT, true, 540, 210);
        StyklisteCalculator calculator = new StyklisteCalculator();

        int result = calculator.calculatePosts(carport);

        assertEquals(8, result);
    }

    @Test
    void calculateRafters() {
        Carport carport = new Carport(600, 780, 230, RoofType.FLAT, false, 0, 0);
        StyklisteCalculator calculator = new StyklisteCalculator();

        int result = calculator.calculateRafters(carport);

        assertEquals(15, result);
    }

    @Test
    void calculateMaterialListWithShed() {
        Carport carport = new Carport(600, 780, 380, RoofType.RAISED, true, 540, 210);
        StyklisteCalculator calculator = new StyklisteCalculator();

        List<MaterialLine> result = calculator.calculateMaterialList(carport);

        assertEquals("Stolpe", result.get(0).getName());
        assertEquals("125 x 125 mm", result.get(0).getDimensions());
        assertEquals(8, result.get(0).getQuantity());
        assertEquals("Rem", result.get(1).getName());
        assertEquals("Spær", result.get(2).getName());
        assertEquals("Beklædning", result.get(5).getName());
    }

    @Test
    void calculateMaterialListWithoutShed() {
        Carport carport = new Carport(390, 780, 310, RoofType.FLAT, false, 0, 0);
        StyklisteCalculator calculator = new StyklisteCalculator();

        List<MaterialLine> result = calculator.calculateMaterialList(carport);

        assertEquals("100 x 100 mm", result.get(0).getDimensions());
        assertEquals(6, result.get(0).getQuantity());
        assertEquals(7, result.size());
    }
}
