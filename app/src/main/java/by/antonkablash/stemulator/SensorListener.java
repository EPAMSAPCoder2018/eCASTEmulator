package by.antonkablash.stemulator;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import by.antonkablash.stemulator.model.MessageTypes;
import by.antonkablash.stemulator.model.Order;
import by.antonkablash.stemulator.model.SensorsConfig;

/**
 * Created by Anton_Kablash on 6/26/2018.
 */
public class SensorListener implements CompoundButton.OnCheckedChangeListener  {

    private final static String SERVICE_URL = "https://iotmmss0019634792trial.hanatrial.ondemand.com/com.sap.iotservices.mms/v1/api/http/data/";
    private static SensorListener instance;
    private boolean isGPSTrackingEnabled = false;
    private View rootView;
    private MainActivity context;
    private Switch switchStartProcess;
    private ToggleButton bucketToggleButton;
    private ToggleButton brushesToggleButton;
    private ToggleButton saltSpreaderToggleButton;
    private LinearLayout bucketIdLayout;

    private SensorListener(MainActivity context) {
        this.context = context;
    }

    public static SensorListener getInstance(MainActivity context) {
        if(instance == null && context != null){
            instance = new SensorListener(context);
        }
        return instance;
    }

    public void init(View rootView){
        this.rootView = rootView;
        switchStartProcess = (Switch) rootView.findViewById(R.id.switchStartProcess);
        bucketToggleButton = (ToggleButton) rootView.findViewById(R.id.toggleButton);
        brushesToggleButton = (ToggleButton) rootView.findViewById(R.id.brushesToggleButton);
        saltSpreaderToggleButton = (ToggleButton) rootView.findViewById(R.id.saltSpreaderToggleButton);
        bucketIdLayout = (LinearLayout) rootView.findViewById(R.id.bucketIdLayout);
        final Button getOrderButton = (Button) rootView.findViewById(R.id.getOrderButton);
        final Button finishOrderButton = (Button) rootView.findViewById(R.id.finishOrderButton);
        final LinearLayout sensorsLayout = (LinearLayout) rootView.findViewById(R.id.sensorsLayout);
        getOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Button button = (Button)v;
                // Request a string response from the provided URL.
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, MainActivity.BASE_XSA_URL + "/getAssignedOrderByCar.xsjs?carId=" + context.getUid(), null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try {
                                    try {
                                        response.getJSONObject("result");
                                        final Order order = new Order(response.getJSONObject("result"));
                                        RouteGenarator.getInstance().setOrder(order);
                                        button.setVisibility(View.INVISIBLE);
                                        finishOrderButton.setVisibility(View.VISIBLE);
                                        sensorsLayout.setVisibility(View.VISIBLE);
                                        RouteGenarator.getInstance().generateRoute();
                                        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, MainActivity.BASE_XSA_URL + "/updateOrderStatusByID.xsjs?status=I&orderId=" + order.getOrderId(), null,
                                                new Response.Listener<JSONObject>() {
                                                    @Override
                                                    public void onResponse(JSONObject response) {
                                                        Log.e("CUSTOM", "Status successfuly updated to 'I' for order" + order.getOrderId());
                                                    }
                                                }, new Response.ErrorListener() {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                try{
                                                    Log.e("CUSTOM", "Error when updates status to 'I' for order" + order.getOrderId() + ". Error:" + error.getMessage());
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }
                                        );
                                        // Add the request to the RequestQueue.
                                        RequestsQueue.getInstance(context).addToRequestQueue(jsonObjectRequest);
                                    }catch(Exception e){
                                        Toast.makeText(context, "Закрепленных за машиной заказов не найдено, попробуйте позже.", Toast.LENGTH_LONG).show();
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                Log.e("CUSTOM", "Register success " + response.toString());
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("CUSTOM", "Register error " + error.toString());
                    }
                }
                );
                // Add the request to the RequestQueue.
                RequestsQueue.getInstance(context).addToRequestQueue(jsonObjectRequest);
            }
        });
        finishOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Button button = (Button)v;
                button.setVisibility(View.INVISIBLE);
                getOrderButton.setVisibility(View.VISIBLE);
                sensorsLayout.setVisibility(View.INVISIBLE);
                if(switchStartProcess.isChecked()){
                    switchStartProcess.setChecked(false);
                }
                RouteGenarator.getInstance().endRouteGenarating();
                for ( int i = 0; i < bucketIdLayout.getChildCount();  i++ ){
                    View view = bucketIdLayout.getChildAt(i);
                    view.setEnabled(false); // Or whatever you want to do with the view.
                }
                final long orderId = RouteGenarator.getInstance().getOrder().getOrderId();
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, MainActivity.BASE_XSA_URL + "/updateOrderStatusByID.xsjs?status=D&orderId=" + orderId, null,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                try{
                                    Log.e("CUSTOM", "Status successfuly updated to 'D' for order" + orderId + ". " + response.toString());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        try{
                            Log.e("CUSTOM", "Error when updates status to 'D' for order" + orderId);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                );
                // Add the request to the RequestQueue.
                RequestsQueue.getInstance(context).addToRequestQueue(jsonObjectRequest);
                MapManager.getInstance(null).removeOrder();
                RouteGenarator.getInstance().setOrder(null);
            }
        });
        for ( int i = 0; i < bucketIdLayout.getChildCount();  i++ ){
            View view = bucketIdLayout.getChildAt(i);
            view.setEnabled(false); // Or whatever you want to do with the view.
        }
        if(bucketToggleButton != null){
            bucketToggleButton.setOnCheckedChangeListener(this);
        }
        if(brushesToggleButton != null){
            brushesToggleButton.setOnCheckedChangeListener(this);
        }
        if(saltSpreaderToggleButton != null){
            saltSpreaderToggleButton.setOnCheckedChangeListener(this);
        }
        if(switchStartProcess != null) {
            switchStartProcess.setOnCheckedChangeListener(this);
        }

        TextView uidTextView = (TextView) rootView.findViewById(R.id.uidTextView);
        uidTextView.setText(uidTextView.getText() + context.getUid());
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        String operation = null;
        MessageTypes operationCode = null;
        SensorsConfig sensor = null;
        LatLng coordinates = LocationUpdatesManager.getInstance(null).getBestLocation();
        switch (buttonView.getId()) {
            case R.id.switchStartProcess:
                operation = isChecked ? "Cleaning process has been started" : "Cleaning process has been finished";
                for ( int i = 0; i < bucketIdLayout.getChildCount();  i++ ){
                    View view = bucketIdLayout.getChildAt(i);
                    view.setEnabled(isChecked); // Or whatever you want to do with the view.
                }
                if(isChecked){
                    RouteGenarator.getInstance().startRouteGenarating();
                } else {
                    if(bucketToggleButton.isChecked()){
                        bucketToggleButton.setChecked(false);
                    }
                    if(brushesToggleButton.isChecked()){
                        brushesToggleButton.setChecked(false);
                    }
                    if(saltSpreaderToggleButton.isChecked()){
                        saltSpreaderToggleButton.setChecked(false);
                    }
                    RouteGenarator.getInstance().endRouteGenarating();
                }
                isGPSTrackingEnabled = isChecked;
                sensor = SensorsConfig.GPS;
                break;
            case R.id.toggleButton:
                operation = isChecked ? "Snowplow has been raised" : "Snowplow has been lowered";
                sensor = SensorsConfig.SNOWPLOW;
                break;
            case R.id.brushesToggleButton:
                operation = isChecked ? "Brushes has been switched on" : "Brushes has been switched off";
                sensor = SensorsConfig.BRUSH;
                break;
            case R.id.saltSpreaderToggleButton:
                operation = isChecked ? "Salt spreader has been switched on" : "Salt spreader has been switched off";
                sensor = SensorsConfig.SALT_SPREADER;
                break;
        }
        operationCode = isChecked ? MessageTypes.START : MessageTypes.STOP;

        Log.e("CUSTOM", context.getUid() + " : " + operation + ": " + coordinates);
        Toast.makeText(context, operation + ": " + coordinates, Toast.LENGTH_LONG).show();

        if(coordinates != null) {
            sendSensorRequest(prepareIoTRequestBody(operationCode, coordinates), sensor);
        }
    }

    public JSONObject prepareIoTRequestBody(MessageTypes messageType, LatLng coordinates){
        JSONObject body = new JSONObject();
        JSONArray messages = new JSONArray();
        JSONObject message = new JSONObject();
        try {
            body.put("mode", "sync");
            body.put("messageType", messageType.getOperationId());

            message.put("timestamp", System.currentTimeMillis()/1000);
            message.put("longitude", coordinates.longitude);
            message.put("latitude", coordinates.latitude);
            message.put("orderId", RouteGenarator.getInstance().getOrder().getOrderId());
            message.put("add", "");
            messages.put(0,message);

            body.put("messages", messages);
        } catch (JSONException e) {
            Log.e("CUSTOM", e.getMessage());
        }
        return body;
    }

    public void sendSensorRequest(final JSONObject jsonObject, final SensorsConfig sensorsConfig){
        // Request a string response from the provided URL.
        Log.e("CUSTOM", "sensorsConfig " + sensorsConfig.toString());
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, SERVICE_URL + sensorsConfig.getSensorId(), jsonObject,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("CUSTOM", "success " + response.toString());
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("CUSTOM", "error " + error.toString());
            }
        }){
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("Authorization", "Bearer " + sensorsConfig.getoAuthToken());
                params.put("Content-Type", "application/json");
                return params;
            }
        };
        // Add the request to the RequestQueue.
        RequestsQueue.getInstance(context).addToRequestQueue(jsonObjectRequest);
        final long orderId =  RouteGenarator.getInstance().getOrder().getOrderId();
        JsonObjectRequest statusRequest = new JsonObjectRequest(Request.Method.GET, MainActivity.BASE_XSA_URL + "/setStageStatus.xsjs?orderId=" + orderId, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.e("CUSTOM", "Status successfuly updated for stages in order=" + orderId + ". Results:" + response.getString("results"));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                try {
                    Log.e("CUSTOM", "Error when updates status for stages in order=" + orderId + ". Error:" + error.getMessage());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        );
        // Add the request to the RequestQueue.
        RequestsQueue.getInstance(context).addToRequestQueue(statusRequest);
    }

    public void sendGPSCoordinatesToIoTService(LatLng location) {
        if(isGPSTrackingEnabled) {
            sendSensorRequest(prepareIoTRequestBody(MessageTypes.INPROCESS, location), SensorsConfig.GPS);
        }
    }

}
