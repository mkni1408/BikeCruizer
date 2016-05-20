package bikecruzer.aau.dk.bikecruizer;

import android.app.Fragment;
import android.content.Context;
import android.content.res.Resources;
import android.location.Location;
import android.util.Log;

import com.google.android.gms.gcm.Task;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by michael on 05/05/16.
 */
public final class Constants {
    private Constants() {
    }

    public static final String PACKAGE_NAME = "com.google.android.gms.location.activityrecognition";

    public static LatLng FAKELOCATION = new LatLng(57.047879, 9.924348);

    public static final String BROADCAST_ACTION = PACKAGE_NAME + ".BROADCAST_ACTION";

    public static final String ACTIVITY_EXTRA = PACKAGE_NAME + ".ACTIVITY_EXTRA";

    public static final String SHARED_PREFERENCES_NAME = PACKAGE_NAME + ".SHARED_PREFERENCES";

    public static final String ACTIVITY_UPDATES_REQUESTED_KEY = PACKAGE_NAME +
            ".ACTIVITY_UPDATES_REQUESTED";

    public static final String DETECTED_ACTIVITIES = PACKAGE_NAME + ".DETECTED_ACTIVITIES";

    public static int walkOrCycle = 0; //0, 2, 4
    public static int currentFragmentIndex = 0; //0,1,2
    public static Fragment currentFragment = null;
    public static Marker currentMarker = null;
    public static boolean fakeLocation = false;
    public static boolean useMapTouch = false;
    public static boolean ratePOIS = false;
    public static boolean isInteractingWithMap = true;
    public static ExecutorService excs = null;
    public static int navRadiusInMeters = 200; // meters
    public static boolean isMapBeingRedrawn = false;
    public static float walkZoomLevel = 16f;
    public static float bikeZoomLevel = 18f;
    public static boolean disableActivityService = false;
    /**
     * The desired time between activity detections. Larger values result in fewer activity
     * detections while improving battery life. A value of 0 results in activity detections at the
     * fastest possible rate. Getting frequent updates negatively impact battery life and a real
     * app may prefer to request less frequent updates.
     */
    public static final long DETECTION_INTERVAL_IN_MILLISECONDS = 15000;

    public static final long LOCATION_DETECTION_INTERVAL_IN_MILLISECONDS = 5000;




    /**
     * List of DetectedActivity types that we monitor in this sample.
     */
    protected static final int[] MONITORED_ACTIVITIES = {
            DetectedActivity.STILL,
            DetectedActivity.ON_FOOT,
            DetectedActivity.WALKING,
            DetectedActivity.RUNNING,
            DetectedActivity.ON_BICYCLE,
            DetectedActivity.IN_VEHICLE,
            DetectedActivity.TILTING,
            DetectedActivity.UNKNOWN
    };

    /**
     * Returns a human readable String corresponding to a detected activity type.
     */
    public static String getActivityString(Context context, int detectedActivityType) {
        Resources resources = context.getResources();
        switch(detectedActivityType) {
            case DetectedActivity.IN_VEHICLE:
                return resources.getString(R.string.in_vehicle);
            case DetectedActivity.ON_BICYCLE:
                return resources.getString(R.string.on_bicycle);
            case DetectedActivity.ON_FOOT:
                return resources.getString(R.string.on_foot);
            case DetectedActivity.RUNNING:
                return resources.getString(R.string.running);
            case DetectedActivity.STILL:
                return resources.getString(R.string.still);
            case DetectedActivity.TILTING:
                return resources.getString(R.string.tilting);
            case DetectedActivity.UNKNOWN:
                return resources.getString(R.string.unknown);
            case DetectedActivity.WALKING:
                return resources.getString(R.string.walking);
            default:
                return resources.getString(R.string.unidentifiable_activity, detectedActivityType);
        }
    }
    public static void startInteractionWithMap(){

        Constants.isInteractingWithMap = true;
        if(Constants.excs != null){
            Constants.excs.shutdownNow();
            Constants.excs = Executors.newSingleThreadExecutor();
            Constants.excs.submit(new SetMapInteractionTask());
        }else{
            Constants.excs = Executors.newSingleThreadExecutor();
            Constants.excs.submit(new SetMapInteractionTask());
        }

    }

}

//will set isInteracting var to false after 10 sec of no action
final class SetMapInteractionTask implements Callable<Void> {
    @Override
    public Void call() throws Exception {
        Log.i("starting interaction","true");
        Thread.sleep(10000); // Just a long running task.
        Constants.isInteractingWithMap = false;
        return null;
    }
}
