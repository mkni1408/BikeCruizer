package bikecruzer.aau.dk.bikecruizer;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by michael on 11-05-2016.
 */
public class NavigationHelper {

    private ArrayList<InterestPoint> mIpList = null;
    private ArrayList<POI> mPOIList = null;

    public NavigationHelper(ArrayList<POI> poiList){
        //remember to init the list if null!!!
        this.mPOIList = poiList;
    }


    //gets a navigationResult (if Any) from a list of interestpoints
    //radius: radius within where it should look
    public NavigationResult getNavigationResult(LatLng currentLocation, int radius){
        if(Constants.fakeLocation || currentLocation == null){
            currentLocation = Constants.FAKELOCATION;
        }

        double dist = 0.0;
        int index = 0;
        if(this.mPOIList.size() > -1) {
            for (int i = 0; i < POIs.getPois().size(); i++) {
                double newdist = distance(currentLocation.latitude, currentLocation.longitude, POIs.getPois().get(i).getPosition().latitude, POIs.getPois().get(i).getPosition().longitude) - ((POIs.getPois().get(i).getNumPOI() / 10) * 10) ;


                Log.i("found",Double.toString(newdist));
                //if distance is below zero and we are not already in this POI
                if(newdist < 1 && !POIs.getPois().get(i).getInThisPOI()){
                    POIs.getPois().get(i).setInThisPOI(true);
                    dist = newdist;
                    index = i;
                    return new NavigationResult(POIs.getPois().get(index).getName(),
                            POIs.getPois().get(index).getId(), POIs.getPois().get(index));
                }
                //if we are leaving a poi and
                if(newdist > 1 && POIs.getPois().get(i).getInThisPOI()){
                    POIs.getPois().get(i).setInThisPOI(false);
                }

//                else if(newdist < dist){
//                    dist = newdist;
//                    index = i;
//                }
//                Log.i("newDist",Double.toString(newdist));
            }
            return null;


        }


        return null;
    }

    private double distance(double lat1, double lon1, double lat2, double lon2) {
        // haversine great circle distance approximation, returns meters
        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2))
                + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60; // 60 nautical miles per degree of seperation
        dist = dist * 1852; // 1852 meters per nautical mile
        return (dist);
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
    }


}
