package by.antonkablash.stemulator.model;

import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Anton_Kablash on 7/10/2018.
 */
public class Order {
    private Long orderId;
    private String status;
    private Date orderDate;
    private List<Stage> stages = new ArrayList<>();
    private Car car;

    public Order(JSONObject order) {
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            orderId = order.getLong("orderId");
            status = order.getString("status");
            JSONArray stagesArray = order.getJSONArray("stages");
            for(int i=0; i < stagesArray.length(); i++){
                stages.add(new Stage(stagesArray.getJSONObject(i)));
            }
            car = new Car(order.getJSONObject("car"));
            //orderDate = order.getString("orderDate");
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public List<Stage> getStages() {
        return stages;
    }

    public void setStages(List<Stage> stages) {
        this.stages = stages;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<LatLng> getPoints(){
        List<LatLng> points = new ArrayList<LatLng>();
        for(Stage stage : stages) {
            points.addAll(stage.getVector().getPoints());
        }
        return points;
    }
}
