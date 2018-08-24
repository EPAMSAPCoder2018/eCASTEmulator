package by.antonkablash.stemulator;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Anton_Kablash on 6/26/2018.
 */
public class LocationUpdatesManager {
    private MainActivity context;
    private LocationTracker gpsTracker;
    private LocationTracker networkTracker;
    private LocationManager mLocationManager;
    private static LocationUpdatesManager instance;

    private LocationUpdatesManager(MainActivity context) {
        this.context = context;
    }

    public static LocationUpdatesManager getInstance(MainActivity context){
        if(instance == null){
            instance = new LocationUpdatesManager(context);
        }
        return instance;
    }

    public void init(){
        checkNetwork();
        gpsTracker = new LocationTracker(this);
        networkTracker = new LocationTracker(this);
        mLocationManager = (LocationManager) context.getSystemService(context.LOCATION_SERVICE);
        if(!mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Internet Connection Error")
                    .setMessage("Please connect to working Internet connection")
                    .setCancelable(false)
                    .setNegativeButton("Enable Network",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                                    context.startActivity(intent);
                                }
                            });
            AlertDialog alert = builder.create();
            alert.show();
        }
        if (context.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && context.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            context.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, context.gpsPermissionRequestCode);
            return;
        }else{
            Log.e("CUSTOM", "GPS Tracker is enabled");
           // mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, gpsTracker);
        }
        if(context.checkSelfPermission(Manifest.permission.INTERNET) != PackageManager.PERMISSION_GRANTED ){
            context.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, context.networkPermissionRequestCode);
        }else {
            Log.e("CUSTOM", "NETWORK Tracker is enabled");
           // mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, networkTracker);
        }
    }
    private void checkNetwork() {
        ConnectionDetector cd = new ConnectionDetector(context);
        // Check if Internet present
        if (!cd.isConnectingToInternet()) {
            // Internet Connection is not present
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Internet Connection Error")
                    .setMessage("Please connect to working Internet connection")
                    .setCancelable(false)
                    .setNegativeButton("Enable Network",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                                    context.startActivity(intent);
                                }
                            });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }

    public void onLocationChanged(LatLng location){
        SensorListener.getInstance(context).sendGPSCoordinatesToIoTService(location);
    }

    public LatLng getBestLocation(){
        LatLng gpsCoordinates = gpsTracker.getLocation();
        LatLng networkCoordinates = networkTracker.getLocation();
        if(gpsCoordinates == null){
            return networkCoordinates;
        }
        return gpsCoordinates;
    }

    public LocationTracker getGpsTracker() {
        return gpsTracker;
    }

    public LocationTracker getNetworkTracker() {
        return networkTracker;
    }
}
