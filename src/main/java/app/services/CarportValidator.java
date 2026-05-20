package app.services;

import app.dto.CarportForm;

public class CarportValidator {

    public void validate(CarportForm form) {
        if (form.getWidth() < 240 || form.getWidth() > 800) {
            throw new IllegalArgumentException("Bredde skal være mellem 240 og 800 cm");
        }

        if (form.getLength() < 240 || form.getLength() > 1200) {
            throw new IllegalArgumentException("Længde skal være mellem 240 og 1200 cm");
        }

        if (form.getHeight() < 180 || form.getHeight() > 350) {
            throw new IllegalArgumentException("Højde skal være mellem 180 og 350 cm");
        }

        if (form.isHasShed()) {
            if (form.getShedWidth() <= 0 || form.getShedWidth() > form.getWidth()) {
                throw new IllegalArgumentException("Skurbredde er ugyldig");
            }

            if (form.getShedLength() <= 0 || form.getShedLength() > form.getLength()) {
                throw new IllegalArgumentException("Skurlængde er ugyldig");
            }
        }
    }
}
