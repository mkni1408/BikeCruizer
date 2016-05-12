package bikecruzer.aau.dk.bikecruizer;

import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.WindowManager;

import com.google.android.gms.location.ActivityRecognitionResult;
import com.google.android.gms.location.DetectedActivity;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.CameraPosition;

import java.util.ArrayList;
import java.util.List;

import bikecruzer.aau.dk.bikecruizer.Constants;

public class DetectedActivitiesIntentService extends IntentService {

    protected static final String TAG = "DetectedActivitiesIS";

    /**
     * This constructor is required, and calls the super IntentService(String)
     * constructor with the name for a worker thread.
     */
    public DetectedActivitiesIntentService() {
        // Use the TAG to name the worker thread.
        super(TAG);
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    /**
     * Handles incoming intents.
     * @param intent The Intent is provided (inside a PendingIntent) when requestActivityUpdates()
     *               is called.
     */
    @Override
    protected void onHandleIntent(Intent intent) {
        ActivityRecognitionResult result = ActivityRecognitionResult.extractResult(intent);
        Intent localIntent = new Intent(Constants.BROADCAST_ACTION);

        // Get the list of the probable activities associated with the current state of the
        // device. Each activity is associated with a confidence level, which is an int between
        // 0 and 100.
        ArrayList<DetectedActivity> detectedActivities = (ArrayList) result.getProbableActivities();

        DetectedActivity activity = this.walkingOrCykeling(result.getProbableActivities());

        //will undim screen if not walking, running or cyceling
        if(activity != null) {
            switch (activity.getType()) {
                case 1:
                case 2:
                case 3:
                    //setMapToWalk();
                default:
                    //setMapToCycle();
            }
        }else{
            setMapToWalk();
        }

        // Broadcast the list of detected activities.
        localIntent.putExtra(Constants.ACTIVITY_EXTRA, detectedActivities);
        LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
    }

    public void setMapToWalk(){
        Constants.walkOrCycle = 2;

    };
    public void setMapToCycle(){
        Constants.walkOrCycle = 4;
    };

    private DetectedActivity walkingOrCykeling(List<DetectedActivity> probableActivities) {
        DetectedActivity myActivity = null;
        int confidence = 0;
        for (DetectedActivity activity : probableActivities) {
            //if different than these - continue
            if (activity.getType() != DetectedActivity.STILL
                    && activity.getType() != DetectedActivity.WALKING
                    && activity.getType() != DetectedActivity.RUNNING
                    && activity.getType() != DetectedActivity.WALKING)
                continue;
            //else we look at confidence
            if (activity.getConfidence() > confidence)
                myActivity = activity;
        }

        return myActivity;
    }
}