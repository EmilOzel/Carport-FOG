package app.services;

import app.dto.CarportForm;
import app.entities.Carport;
public class CarportService {
    private final CarportValidator validator = new CarportValidator();

    public Carport createCarportFromForm(CarportForm form) {
        validator.validate(form);

        return new Carport(
                form.getWidth(),
                form.getLength(),
                form.getHeight(),
                form.isHasShed(),
                form.getShedWidth(),
                form.getShedLength()
        );
    }
}
