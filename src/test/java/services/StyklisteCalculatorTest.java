package app.services;

import app.entities.Carport;
import app.entities.RoofType;
import org.junit.jupiter.api.Test;

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
}