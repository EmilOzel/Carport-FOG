package app.services;

import app.dto.CarportForm;
import app.entities.Carport;
import app.entities.RoofType;

public class CarportService {
    private final CarportValidator validator = new CarportValidator();

    public Carport createCarportFromForm(CarportForm form) {
        validator.validate(form);

        RoofType roofType = RoofType.valueOf(form.getRoofType());

        return new Carport(
                form.getWidth(),
                form.getLength(),
                form.getHeight(),
                roofType,
                form.isHasShed(),
                form.getShedWidth(),
                form.getShedLength()
        );
    }
}
