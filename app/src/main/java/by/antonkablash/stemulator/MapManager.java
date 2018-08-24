package by.antonkablash.stemulator;

import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.List;

/**
 * Created by Anton_Kablash on 6/27/2018.
 */
public class MapManager  implements OnMapReadyCallback {
    private MapView mapView;
    private Marker carMarker;
    private GoogleMap googleMap;
    private MainActivity context;
    private static MapManager instance;
    private Boolean isOrderRendered = false;

    private MapManager(MainActivity context) {
        this.context = context;
    }

    public static MapManager getInstance(MainActivity context) {
        if(instance == null && context != null){
            instance = new MapManager(context);
        }
        return instance;
    }

    public void init(View rootView, Bundle savedInstanceState){
        mapView = (MapView) rootView.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        MapsInitializer.initialize(context);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;
        this.googleMap.setMyLocationEnabled(true);
        showOrder();
    }

    public void showOrder(){
        if(RouteGenarator.getInstance().getPoints() != null && !isOrderRendered){
            List<LatLng> points = RouteGenarator.getInstance().getPoints();
            LatLng startPosition = points.get(0);
            this.googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(startPosition, 13));
            PolylineOptions options = new PolylineOptions();

            options.color(Color.parseColor("#CC0000FF"));
            options.width(5);
            options.visible(true);
            options.addAll(points);

            MarkerOptions carMarkerOptions = new MarkerOptions();
            carMarkerOptions.position(startPosition);
            carMarkerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
            this.carMarker = this.googleMap.addMarker(carMarkerOptions);
            this.googleMap.addPolyline(options);
            this.isOrderRendered = true;
        }
        mapView.onResume();
    }

    public void removeOrder(){
        this.carMarker = null;
        this.googleMap.clear();
        isOrderRendered = false;
        mapView.onResume();
    }

    public void showSpot(final LatLng point) {
        if(this.carMarker != null) {
            context.runOnUiThread(new Runnable() {
                public void run() {
                    animateMarker(carMarker, point, false);
                }
            });

        }
    }

    public void animateMarker(final Marker marker, final LatLng toPosition,
                              final boolean hideMarker) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection proj = this.googleMap.getProjection();
        Point startPoint = proj.toScreenLocation(marker.getPosition());
        final LatLng startLatLng = proj.fromScreenLocation(startPoint);
        final long duration = 5000;

        final Interpolator interpolator = new LinearInterpolator();

        handler.post(new Runnable() {
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                float t = interpolator.getInterpolation((float) elapsed
                        / duration);
                double lng = t * toPosition.longitude + (1 - t)
                        * startLatLng.longitude;
                double lat = t * toPosition.latitude + (1 - t)
                        * startLatLng.latitude;
                marker.setPosition(new LatLng(lat, lng));

                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                } else {
                    if (hideMarker) {
                        marker.setVisible(false);
                    } else {
                        marker.setVisible(true);
                    }
                }
            }
        });
    }
}
