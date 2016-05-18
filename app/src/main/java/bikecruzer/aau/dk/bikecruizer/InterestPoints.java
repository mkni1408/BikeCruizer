package bikecruzer.aau.dk.bikecruizer;

import android.app.Activity;
import android.app.ProgressDialog;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

/**
 * Created by michael on 10-05-2016.
 */
public final class InterestPoints {
    private static ArrayList<InterestPoint> interestPoints = new ArrayList<InterestPoint>();
    private static ProgressDialog p = null;
    private static MainActivity ma;
    private InterestPoints(){

    }

    public static ArrayList<InterestPoint> getInterestPoints(){
        return InterestPoints.interestPoints;
    }

    public static void setInterestPoints (ArrayList<InterestPoint> points){
        InterestPoints.interestPoints = points;
    }

    public static void fetchInterestPoints (ProgressDialog p, MainActivity a){
        ma = a;
        startFetchning(p);
    }

    private static void startFetchning (ProgressDialog b){

        p = b;

        p.setTitle("Fetching interest points");
        p.setMessage("Wait...");
        p.show();

        IPFetcher fetcher = new IPFetcher();
        fetcher.fetch();
    }

    public static void stopFetchning (){
        //unset progressbar
        p.hide();
        p = null;

        ma.setMainContentView();
        ma = null;

        //draw map by calling fragment method

    }

    public static void drawIPstoMap(Activity a, GoogleMap map){
        //Log.i("fetcher done fetching","..");
        for (int i = 0 ; i < InterestPoints.interestPoints.size(); i++) {
            BitmapDescriptor iconDesc = Helpers.resolveIPIcon(a, InterestPoints.interestPoints.get(i).getIcon(), map);
            //add location pin
            map.addMarker(new MarkerOptions()
                    .position(InterestPoints.interestPoints.get(i).getPosition())
                    .icon(iconDesc)
                    .title(InterestPoints.interestPoints.get(i).getName()));

        }

    }
}
