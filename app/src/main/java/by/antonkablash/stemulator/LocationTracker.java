package by.antonkablash.stemulator;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by Anton_Kablash on 6/13/2018.
 */
public class LocationTracker implements LocationListener {

    private LatLng location;
    private LocationUpdatesManager context;

    public LocationTracker() {
    }

    public LocationTracker(LocationUpdatesManager context) {
        this.context = context;
    }

    public LatLng getLocation() {
        return location;
    }

    public void setLocation(LatLng location) {
        this.location = location;
    }

    @Override
    public void onLocationChanged(Location loc) {
        this.location = new LatLng(loc.getLatitude(), loc.getLongitude());
        if(context != null){
            context.onLocationChanged(location);
        }
        Log.e("CUSTOM", "location has been changed " + location.toString());
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        Log.e("CUSTOM", provider + " changes the status to " + status);
    }

    @Override
    public void onProviderEnabled(String provider) {
        Log.e("CUSTOM", provider + " is enabled");
    }

    @Override
    public void onProviderDisabled(String provider) {
        Log.e("CUSTOM", provider + " is disabled");
    }
}