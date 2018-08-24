package by.antonkablash.stemulator.model;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;

/**
 * Created by Anton_Kablash on 7/10/2018.
 */
public class Stage {
    private Long stageId;
    private String geoFrom;
    private String geoTo;
    private String status;
    private String geoFromName;
    private String geoToName;
    private Vector vector;

    public Stage(JSONObject stage) {
        try {
            this.stageId = stage.getLong("stageId");
            this.geoFrom = stage.getString("geoFrom");
            this.geoTo = stage.getString("geoTo");
            this.status = stage.getString("status");
            this.geoFromName = stage.getString("geoFromName");
            this.geoToName = stage.getString("geoToName");
            this.vector = new Vector(stage.getJSONObject("vector"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getGeoFrom() {
        return geoFrom;
    }

    public void setGeoFrom(String geoFrom) {
        this.geoFrom = geoFrom;
    }

    public String getGeoFromName() {
        return geoFromName;
    }

    public void setGeoFromName(String geoFromName) {
        this.geoFromName = geoFromName;
    }

    public String getGeoTo() {
        return geoTo;
    }

    public void setGeoTo(String geoTo) {
        this.geoTo = geoTo;
    }

    public String getGeoToName() {
        return geoToName;
    }

    public void setGeoToName(String geoToName) {
        this.geoToName = geoToName;
    }

    public Long getStageId() {
        return stageId;
    }

    public void setStageId(Long stageId) {
        this.stageId = stageId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Vector getVector() {
        return vector;
    }

    public void setVector(Vector vector) {
        this.vector = vector;
    }
}
