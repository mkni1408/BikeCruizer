package bikecruzer.aau.dk.bikecruizer;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

/**
 * Created by michael on 11-05-2016.
 */
public class NavigationHelper {

    private ArrayList<InterestPoint> mIpList = null;

    public NavigationHelper(ArrayList<InterestPoint> ipList){
        this.mIpList = ipList;
    }

    //gets a navigationResult (if Any) from a list of interestpoints
    //radius: radius within where it should look
    public NavigationResult getNavigationResult(LatLng currentLocation, int radius){
        if(Constants.fakeLocation){
            currentLocation = Constants.AA_MIDTBY;
        }

        double dist = 0.0;
        int index = 0;
        if(this.mIpList.size() > -1) {
            for (int i = 0; i < mIpList.size(); i++) {
                double newdist = distance(currentLocation.latitude, currentLocation.longitude, mIpList.get(i).getLatitude(), mIpList.get(i).getLongitude());
                if(dist == 0){
                    dist = newdist;
                    index = i;
                }
                else if(newdist < dist){
                    dist = newdist;
                    index = i;
                }
            }
            if(dist > radius){
                return null;
            }else{
                return new NavigationResult(mIpList.get(index).getName());
            }
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
