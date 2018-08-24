package by.antonkablash.stemulator.model;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Anton_Kablash on 7/10/2018.
 */
public class Car {
    private String carId;
    private String status;
    private String licPlate;
    private String carName;
    private String carModel;
    private String VIN;
    private String avgSpeed;

    public Car(JSONObject car) {
        try {
            carId = car.getString("carId");
            status = car.getString("status");
            licPlate = car.getString("licPlate");
            carName = car.getString("carName");
            carModel = car.getString("carModel");
            VIN = car.getString("VIN");
            avgSpeed = car.getString("avgSpeed");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String getAvgSpeed() {
        return avgSpeed;
    }

    public void setAvgSpeed(String avgSpeed) {
        this.avgSpeed = avgSpeed;
    }

    public String getCarId() {
        return carId;
    }

    public void setCarId(String carId) {
        this.carId = carId;
    }

    public String getCarModel() {
        return carModel;
    }

    public void setCarModel(String carModel) {
        this.carModel = carModel;
    }

    public String getCarName() {
        return carName;
    }

    public void setCarName(String carName) {
        this.carName = carName;
    }

    public String getLicPlate() {
        return licPlate;
    }

    public void setLicPlate(String licPlate) {
        this.licPlate = licPlate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getVIN() {
        return VIN;
    }

    public void setVIN(String VIN) {
        this.VIN = VIN;
    }
}
