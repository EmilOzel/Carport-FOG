package app.services;

import app.Main;
import app.entities.Carport;

public class StyklisteCalculator {

    public int calculatePosts(Carport carport) {
        if (carport.isHasShed()) {
            return 8;
        } else return 6;
    }

    public int calculateBeams(Carport carport) {
        return 2;
    }

    public int calculateRafters(Carport carport) {
        int spacing = 55;
        return (int) Math.ceil(carport.getLength() / (double) spacing);
    }
}
