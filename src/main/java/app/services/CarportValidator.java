package app.services;

import app.dto.CarportForm;

public class CarportValidator {

    public void validate(CarportForm form) {
        if (!CarportDimensions.isValidWidth(form.getWidth())) {
            throw new IllegalArgumentException("Vælg en gyldig carportbredde");
        }

        if (!CarportDimensions.isValidLength(form.getLength())) {
            throw new IllegalArgumentException("Vælg en gyldig carportlængde");
        }

        if (!CarportDimensions.isValidHeight(form.getHeight())) {
            throw new IllegalArgumentException("Vælg en gyldig carporthøjde");
        }

        if (!CarportDimensions.isValidRoofMaterial(form.getRoofMaterial())) {
            throw new IllegalArgumentException("Vælg et gyldigt tagmateriale");
        }

        if (form.isHasShed()) {
            if (!CarportDimensions.isValidShedWidthForCarport(form.getShedWidth(), form.getWidth())) {
                throw new IllegalArgumentException("Skurbredde skal være 60 cm mindre end carportbredden");
            }

            if (!CarportDimensions.isValidShedLength(form.getShedLength()) || form.getShedLength() <= 0) {
                throw new IllegalArgumentException("Skurlængde er ugyldig");
            }

            if (form.getShedLength() > form.getLength() / 3) {
                throw new IllegalArgumentException("Skurlængde skal kunne være i den sidste tredjedel af carporten");
            }
        }
    }
}
