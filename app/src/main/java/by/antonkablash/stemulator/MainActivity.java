package by.antonkablash.stemulator;

import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.provider.Settings;
import android.support.v4.content.IntentCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import by.antonkablash.stemulator.model.SensorsConfig;

public class MainActivity extends AppCompatActivity {
    public static final int gpsPermissionRequestCode = 123;
    public static final int networkPermissionRequestCode = 124;
//    public static final String BASE_XSA_URL = "p1941607290trial-kablash-dev-xs.cfapps.eu10.hana.ondemand.com/services";
    public static final String BASE_XSA_URL = "https://fhctph1qjaqneuos-astanaapp-xs.cfapps.eu10.hana.ondemand.com/services";
    private String uid = null;
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);


        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.addOnPageChangeListener(mSectionsPagerAdapter);
        uid = "54463d412cb4e449";//Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        handleSSLHandshake();
        sendRegisterRequest(BASE_XSA_URL + "/registerCar.xsjs?carId=" + uid);
    }

    public void sendRegisterRequest(final String url){
        // Request a string response from the provided URL.
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        JSONObject carDevices = response.getJSONObject("carDevices");
                        SensorsConfig.GPS.setoAuthToken(carDevices.getString("GPSTrackerToken"));
                        SensorsConfig.BRUSH.setoAuthToken(carDevices.getString("BrushToken"));
                        SensorsConfig.SNOWPLOW.setoAuthToken(carDevices.getString("SnowPlowToken"));
                        SensorsConfig.SALT_SPREADER.setoAuthToken(carDevices.getString("SaltSpreaderToken"));

                        SensorsConfig.GPS.setSensorId(carDevices.getString("GPSTrackerId"));
                        SensorsConfig.BRUSH.setSensorId(carDevices.getString("BrushId"));
                        SensorsConfig.SNOWPLOW.setSensorId(carDevices.getString("SnowPlowId"));
                        SensorsConfig.SALT_SPREADER.setSensorId(carDevices.getString("SaltSpreaderId"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    Log.e("CUSTOM", "Register success " + response.toString());
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("CUSTOM", "Register error " + error.toString());
                    AlertDialog alertDialog = new AlertDialog.Builder(MainActivity.this).create();
                    alertDialog.setTitle("Connection problem");
                    alertDialog.setMessage("Problem with connecting car to the system has been found, application will be restarted");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    restart();
                                }
                            });
                    alertDialog.show();
                }
            }
        );
        // Add the request to the RequestQueue.
        RequestsQueue.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }

    public void restart(){
        PackageManager packageManager = this.getPackageManager();
        Intent intent = packageManager.getLaunchIntentForPackage(this.getPackageName());
        ComponentName componentName = intent.getComponent();
        Intent mainIntent = IntentCompat.makeRestartActivityTask(componentName);
        this.startActivity(mainIntent);
        System.exit(0);
    }


    public String getUid() {
        return uid;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,int[] grantResults){
        if(requestCode == gpsPermissionRequestCode){
            if(grantResults[0] != PackageManager.PERMISSION_GRANTED || grantResults[0] != PackageManager.PERMISSION_GRANTED){
                System.exit(0);
            }else{
                Intent mStartActivity = new Intent(MainActivity.this, this.getClass());
                int mPendingIntentId = 123456;
                PendingIntent mPendingIntent = PendingIntent.getActivity(MainActivity.this, mPendingIntentId, mStartActivity, PendingIntent.FLAG_CANCEL_CURRENT);
                System.exit(0);
            }
        }else if(requestCode == networkPermissionRequestCode){
            if(grantResults[0] != PackageManager.PERMISSION_GRANTED){
                System.exit(0);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main_activity2, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Enables https connections
     */
    @SuppressLint("TrulyRandom")
    public static void handleSSLHandshake() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }

                @Override
                public void checkClientTrusted(X509Certificate[] certs, String authType) {
                }

                @Override
                public void checkServerTrusted(X509Certificate[] certs, String authType) {
                }
            }};

            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                @Override
                public boolean verify(String arg0, SSLSession arg1) {
                    return true;
                }
            });
        } catch (Exception ignored) {
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter implements ViewPager.OnPageChangeListener {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            PlaceholderFragment placeholderFragment = PlaceholderFragment.newInstance(position + 1);
            if(position == 2){
                placeholderFragment.showOrder();
            }
            return placeholderFragment;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
            }
            return null;
        }

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            PlaceholderFragment placeholderFragment = PlaceholderFragment.newInstance(position + 1);
            if(position + 1 == 2){
                placeholderFragment.showOrder();
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {


        private LocationUpdatesManager locationUpdatesManager;
        private SensorListener sensorListener;
        private MapManager mapManager;
        private static Map<Integer, PlaceholderFragment> placeholderFragmentsInstances = new HashMap<>();

        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment;
            if(placeholderFragmentsInstances.isEmpty() || placeholderFragmentsInstances.get(sectionNumber - 1) == null){
                fragment = new PlaceholderFragment();
                Bundle args = new Bundle();
                args.putInt(ARG_SECTION_NUMBER, sectionNumber);
                fragment.setArguments(args);
                placeholderFragmentsInstances.put(sectionNumber - 1, fragment);
            }
            fragment = placeholderFragmentsInstances.get(sectionNumber - 1);

            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            int secNumber = this.getArguments().getInt(ARG_SECTION_NUMBER);
            View rootView;
            MainActivity context = (MainActivity) this.getActivity();
            if(secNumber == 1) {
                rootView = inflater.inflate(R.layout.fragment_order, container, false);

                sensorListener = SensorListener.getInstance(context);
                sensorListener.init(rootView);

                locationUpdatesManager = LocationUpdatesManager.getInstance(context);
                locationUpdatesManager.init();
            }else{
                rootView = inflater.inflate(R.layout.fragment_map, container, false);
                mapManager= MapManager.getInstance(context);
                mapManager.init(rootView, savedInstanceState);
            }
            return rootView;
        }

        public void showOrder(){
            if(mapManager != null) {
                mapManager.showOrder();
            }
        }

        public void sendGPSCoordinatesToIoTService(LatLng location) {
            sensorListener.sendGPSCoordinatesToIoTService(location);
        }
    }

}
