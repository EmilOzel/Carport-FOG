package app.services;

import app.entities.Carport;

import java.util.HashMap;
import java.util.Map;

public class DrawingService {

    public Map<String, Object> createDrawingData(Carport carport) {
        Map<String, Object> data = new HashMap<>();

        data.put("width", carport.getWidth());
        data.put("length", carport.getLength());
        data.put("height", carport.getHeight());
        data.put("roofType", carport.getRoofType().name());
        data.put("hasShed", carport.isHasShed());
        data.put("shedWidth", carport.getShedWidth());
        data.put("shedLength", carport.getShedLength());

        return data;
    }
}
