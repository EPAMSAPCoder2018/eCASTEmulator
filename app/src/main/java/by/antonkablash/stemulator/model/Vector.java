package by.antonkablash.stemulator.model;

import android.graphics.Color;
import android.widget.ListAdapter;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Anton_Kablash on 7/10/2018.
 */
public class Vector {
    private Long vectorId;
    private String coordinates;
    private List<LatLng> points;

    public Vector(JSONObject vector) {
        try {
            vectorId = vector.getLong("vectorId");
            this.setCoordinates(vector.getString("coordinates"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(String coordinates) {
        this.coordinates = coordinates;
        String[] points = coordinates.split(";");
        this.points = new ArrayList<>();
        for (int i = 0; i < points.length; i++) {
            String[] point = points[i].split(",");
            this.points.add(new LatLng(Double.parseDouble(point[1]), Double.parseDouble(point[0])));
        }
    }

    public Long getVectorId() {
        return vectorId;
    }

    public void setVectorId(Long vectorId) {
        this.vectorId = vectorId;
    }

    public List<LatLng> getPoints() {
        return points;
    }

    public void setPoints(List<LatLng> points) {
        this.points = points;
    }
}
