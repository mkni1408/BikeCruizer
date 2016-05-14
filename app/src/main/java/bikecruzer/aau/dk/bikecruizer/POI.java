package bikecruzer.aau.dk.bikecruizer;

import android.content.Intent;

import com.google.android.gms.drive.internal.StringListResponse;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.maps.android.clustering.ClusterItem;


/**
 * Created by michael on 17/04/16.
 */
public class POI implements ClusterItem {
    private LatLng position;
    private String name;
    private Integer numOfPOI;
    private GroundOverlay overlay;
    private POIRatings ratings = null;

    public POI (){
        position = null;
        name = "";
        numOfPOI = 0;
    }

    public POI (String name, double lat, double lng, Integer numOfPOI,POIRatings ratings){
        this.position = new LatLng(lat,lng);
        this.name = name;
        this.numOfPOI = numOfPOI;
        this.ratings = ratings;
    }

    public POI (String name, LatLng latLng, Integer numOfPOI, POIRatings ratings){
        this.position = latLng;
        this.name = name;
        this.numOfPOI = numOfPOI;
        this.ratings = ratings;
    }

    public void setPosition (double lat, double lng){
        this.position = new LatLng(lat,lng);
    }

    public void setName (String name){
        this.name = name;
    }

    public LatLng getPosition (){
        return position;
    }

    public String getName (){
        return name;
    }

    public void setNumOfPOI (int num){
        this.numOfPOI = num;
    }

    public Integer getNumPOI () {
        return numOfPOI;
    }

    public GroundOverlay getOverlay(){
        return overlay;
    }

    public void setOverlay(GroundOverlay o){
        overlay = o;
    }

    public POIRatings getRatings (){
        return ratings;
    }
}
