package bikecruzer.aau.dk.bikecruizer;

import android.Manifest;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.ActivityRecognitionApi;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import bikecruzer.aau.dk.bikecruizer.Adapters.PoiOverlayAdapter;
import bikecruzer.aau.dk.bikecruizer.Main_Speedfragment;
import bikecruzer.aau.dk.bikecruizer.Main_Volumefragment;
import bikecruzer.aau.dk.bikecruizer.Main_POIfragment;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
                        Main_Speedfragment.OnFragmentInteractionListener,
                        Main_POIfragment.OnFragmentInteractionListener,
                        Main_Volumefragment.OnFragmentInteractionListener,
                        Main_Settingsfragment.OnFragmentInteractionListener,
                        GoogleApiClient.OnConnectionFailedListener,
                        GoogleApiClient.ConnectionCallbacks, ResultCallback<Status>, com.google.android.gms.location.LocationListener {


    public static GoogleApiClient mGoogleApiClient;
    public static Location mLastLocation;
    public static Activity mActivity;
    protected static final String TAG = "MainActivity";
    protected ActivityDetectionBroadcastReceiver mBroadcastReceiver;
    private ArrayList<DetectedActivity> mDetectedActivities;
    private boolean mRequestingLocationUpdates = false;
    private LocationRequest mLocationRequest;
    private LocationListener locListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        // Get a receiver for broadcasts from ActivityDetectionIntentService.
        mBroadcastReceiver = new ActivityDetectionBroadcastReceiver();

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .addApi(ActivityRecognition.API)
                .build();



        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        // Reuse the value of mDetectedActivities from the bundle if possible. This maintains state
        // across device orientation changes. If mDetectedActivities is not stored in the bundle,
        // populate it with DetectedActivity objects whose confidence is set to 0. Doing this
        // ensures that the bar graphs for only only the most recently detected activities are
        // filled in.
        if (savedInstanceState != null && savedInstanceState.containsKey(
                Constants.DETECTED_ACTIVITIES)) {
            mDetectedActivities = (ArrayList<DetectedActivity>) savedInstanceState.getSerializable(
                    Constants.DETECTED_ACTIVITIES);
        } else {
            mDetectedActivities = new ArrayList<DetectedActivity>();

            // Set the confidence level of each monitored activity to zero.
            for (int i = 0; i < Constants.MONITORED_ACTIVITIES.length; i++) {
                mDetectedActivities.add(new DetectedActivity(Constants.MONITORED_ACTIVITIES[i], 0));
            }
        }

        //will set window to fullscreen
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mActivity = this;


        setMainContentView();

    }

    public static void showBikeOverlay(boolean show){
        if(!Constants.useMapTouch) {
            RelativeLayout l = (RelativeLayout) mActivity.findViewById(R.id.navigationOverlay);
            l.setVisibility(show ? View.VISIBLE : View.GONE);

            if (show && Constants.fakeLocation) {
                l.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View view, MotionEvent motionEvent) {
                        view.setVisibility(View.GONE);
                        Constants.walkOrCycle = 2;
                        return false;
                    }
                });
            }
        }
    }

    public void setMainContentView(){
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        //int defaultValue = R.id.nav_speed;
        int view = sharedPref.getInt("LAST_VIEW", R.id.nav_speed);


        Log.i("navigation",Integer.toString(view));

        displayView(view);
    }

    public void displayView(int viewId) {

        Fragment fragment = null;
        String title = getString(R.string.app_name);

        switch (viewId) {
            case R.id.nav_speed:
                fragment = new Main_Speedfragment();
                title  = "Speed Routes";
                break;
            case R.id.nav_volume:
                fragment = new Main_Volumefragment();
                title  = "Volume Routes";
                break;
            case R.id.nav_pointsofinterest:
                fragment = new Main_POIfragment();
                title  = "Areas of Interest";
                break;
            case R.id.nav_settings:
                fragment = new Main_Settingsfragment();
                title  = "App settings";
                break;

        }

        if (fragment != null) {
            FragmentTransaction ft = getFragmentManager().beginTransaction();
            ft.replace(R.id.main_content_frame, fragment);
            ft.commit();
            getSupportFragmentManager().executePendingTransactions();

        }

        // set the toolbar title
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //TextView tv = (TextView) findViewById(R.id.bikevswalktextview);

        if(id == R.id.setWalk){
            Constants.walkOrCycle = 2;
            //tv.setText("Walk mode");
            Helpers.updateMap(Constants.currentFragment,Constants.currentFragmentIndex,
                    null);
            showBikeOverlay(false);
        }
        else if(id == R.id.setBike){
            Constants.walkOrCycle = 4;
            //tv.setText("Bike mode");
            Helpers.updateMap(Constants.currentFragment,Constants.currentFragmentIndex,
                    null);
            showBikeOverlay(true);
            onLocationChanged(null);
        }
        else if(id == R.id.fakeLocation){
            if(Constants.fakeLocation){
                Constants.fakeLocation = false;
            }else{
                Constants.fakeLocation = true;
            }

            Helpers.updateMap(Constants.currentFragment,Constants.currentFragmentIndex,
                    null);
            Toast.makeText(this,Constants.fakeLocation ? "Fake location is on" : "Fake location is off", Toast.LENGTH_SHORT).show();
        }
        else if(id == R.id.useMapTouch){
            if(Constants.useMapTouch){
                Constants.useMapTouch = false;
            }else{
                Constants.useMapTouch = true;
                Constants.isInteractingWithMap = false;
            }
            Toast.makeText(this,Constants.useMapTouch ? "Map touch location is on" : "Map touch is off", Toast.LENGTH_LONG).show();
        }
        else if(id == R.id.ratePOI){
            if(Constants.ratePOIS){
                Constants.ratePOIS = false;
            }else{
                Constants.ratePOIS = true;
            }
            Toast.makeText(this,Constants.ratePOIS ? "Rate pois on" : "Rate pois off", Toast.LENGTH_LONG).show();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        displayView(id);
        SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putInt("LAST_VIEW", id);
        editor.commit();
        Log.i("navigation committed",Integer.toString(id));

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void onResult(Status status) {
        if (status.isSuccess()) {
            // Toggle the status of activity updates requested, and save in shared preferences.
            boolean requestingUpdates = !getUpdatesRequestedState();
            setUpdatesRequestedState(requestingUpdates);

        } else {
            Log.e(TAG, "Error adding or removing activity detection: " + status.getStatusMessage());
        }
    }
    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        //InterestPoints.fetchInterestPoints(new ProgressDialog(this),this);
        super.onStart();
    }
    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }
    @Override
    protected void onResume() {
        // Register the broadcast receiver that informs this activity of the DetectedActivity
        // object broadcast sent by the intent service.
        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver,
                new IntentFilter(Constants.BROADCAST_ACTION));

        if (mGoogleApiClient.isConnected() && !mRequestingLocationUpdates) {
            startLocationUpdates();
        }
        super.onResume();
    }
    @Override
    protected void onPause() {
        // Unregister the broadcast receiver that was registered during onResume().
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver);
        if (mGoogleApiClient.isConnected()) {
            stopLocationUpdates();
        }
        super.onPause();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // An unresolvable error has occurred and a connection to Google APIs
        // could not be established. Display an error message, or handle
        // the failure silently
        Log.i(TAG, "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
        // ...
    }
    @Override
    public void onConnected(Bundle connectionHint) {

        Log.i(TAG, "Connected to GoogleApiClient");
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }

        mLocationRequest = new LocationRequest();

        mLocationRequest.setInterval(Constants.LOCATION_DETECTION_INTERVAL_IN_MILLISECONDS);

        mLocationRequest.setFastestInterval(Constants.LOCATION_DETECTION_INTERVAL_IN_MILLISECONDS);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }

        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                mGoogleApiClient);

        if (!mGoogleApiClient.isConnected()) {
            return;
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient,
                mLocationRequest,
                this);


        ActivityRecognition.ActivityRecognitionApi.requestActivityUpdates(
                mGoogleApiClient,
                Constants.DETECTION_INTERVAL_IN_MILLISECONDS,
                getActivityDetectionPendingIntent()
        ).setResultCallback(this);

    }
    @Override
    public void onConnectionSuspended(int i) {
        Log.i(TAG, "Connection suspended");
        return;
    }


    private SharedPreferences getSharedPreferencesInstance() {
        return getSharedPreferences(Constants.SHARED_PREFERENCES_NAME, MODE_PRIVATE);
    }

    /**
     * Retrieves the boolean from SharedPreferences that tracks whether we are requesting activity
     * updates.
     */
    private boolean getUpdatesRequestedState() {
        return getSharedPreferencesInstance()
                .getBoolean(Constants.ACTIVITY_UPDATES_REQUESTED_KEY, false);
    }

    /**
     * Sets the boolean in SharedPreferences that tracks whether we are requesting activity
     * updates.
     */
    private void setUpdatesRequestedState(boolean requestingUpdates) {
        getSharedPreferencesInstance()
                .edit()
                .putBoolean(Constants.ACTIVITY_UPDATES_REQUESTED_KEY, requestingUpdates)
                .commit();
    }

    /**
     * Stores the list of detected activities in the Bundle.
     */
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putSerializable(Constants.DETECTED_ACTIVITIES, mDetectedActivities);
        super.onSaveInstanceState(savedInstanceState);
    }


    protected void startLocationUpdates() {

        if (android.os.Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);

    }
                    @Override
                    public void onLocationChanged(Location location) {

                        if(Constants.walkOrCycle > 2 && Constants.currentFragmentIndex != 2){
                            displayView(R.id.nav_pointsofinterest);
                        }
                        if(location != null){
                            if(!Constants.isInteractingWithMap) {
                                if(Constants.walkOrCycle > 2) {
                                    NavigationHelper h = new NavigationHelper(POIs.getPois());
                                    NavigationResult result = h.getNavigationResult(new LatLng(location.getLatitude(), location.getLongitude()), Constants.navRadiusInMeters);
                                    if (result != null) {
                                        Log.i("Adding  poi", result.getmPOI().getName());
                                        final MediaPlayer mp = MediaPlayer.create(this, R.raw.ding);
                                        mp.start();
                                    }

                                    for (int i = 0; i < POIs.getPois().size(); i++) {
                                        if(POIs.getPois().get(i).getInThisPOI()){
                                        }
                                    }
                                }
                                if(Constants.walkOrCycle < 4) {
                                    Log.i("Walking about", ".....");

                                    NavigationHelper h = new NavigationHelper(POIs.getPois());
                                    NavigationResult result = h.getNavigationResult(new LatLng(location.getLatitude(), location.getLongitude()), Constants.navRadiusInMeters);
                                    if (result != null) {
                                        Log.i("Entered loop", result.getmPOI().getName());
                                        boolean found = false;
                                        for (int i = 0; i < POIs.getWalkedPOIS().size(); i++) {
                                            if(POIs.getWalkedPOIS().get(i).getId() == result.getmId()){
                                                found = true;
                                            }
                                        }
                                        if(!found){
                                            Log.i("Adding Rating poi", result.getmPOI().getName());
                                            POIs.addWalkedPOIS(result.getmPOI());
                                        }
                                    }
                                }

                                if(Constants.ratePOIS){
                                    ratePOIS();
                                }
                            }
                        }
                        //Helpers.setCameraZoomAndCenter(this,);
                    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }


    /**
     * Gets a PendingIntent to be sent for each activity detection.
     */
    private PendingIntent getActivityDetectionPendingIntent() {
        Intent intent = new Intent(this, DetectedActivitiesIntentService.class);


        // We use FLAG_UPDATE_CURRENT so that we get the same pending intent back when calling
        // requestActivityUpdates() and removeActivityUpdates().
        return PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }
    public class ActivityDetectionBroadcastReceiver extends BroadcastReceiver {
        protected static final String TAG = "activity-detection-response-receiver";

        @Override
        public void onReceive(Context context, Intent intent) {
            ArrayList<DetectedActivity> updatedActivities =
                    intent.getParcelableArrayListExtra(Constants.ACTIVITY_EXTRA);
        }
    }

    @Override
    public void onSpeedFragmentInteraction(Uri uri) {

    }

    @Override
    public void onVolumeFragmentInteraction(Uri uri) {

    }

    @Override
    public void onPOIFragmentInteraction(Uri uri) {

    }

    @Override
    public void onSettingsFragmentInteraction(Uri uri) {

    }

    public void ratePOIS (){
        for (int i = 0; i < POIs.getWalkedPOIS().size(); i++) {
            //Toast.makeText(this,"Rate this Aoi" + POIs.getWalkedPOIS().get(i).getId(),Toast.LENGTH_LONG).show();

            final Dialog myDialog;
            myDialog = new Dialog(this);
            myDialog.setContentView(R.layout.ratepoi_dialog);
            myDialog.setCancelable(true);

            myDialog.setTitle("Rate your trip");
            myDialog.setCancelable(false);



            Button okbtn = (Button)myDialog.findViewById(R.id.ratepoi_dialog_btnyes);
            okbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    myDialog.dismiss();
                }
            });

            Button nobtn = (Button)myDialog.findViewById(R.id.ratepoi_dialog_btnno);
            nobtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    myDialog.dismiss();
                }
            });
            //ListView v = (ListView) myDialog.findViewById(R.id.ratingsListView);


            myDialog.show();
        }
    }
}

