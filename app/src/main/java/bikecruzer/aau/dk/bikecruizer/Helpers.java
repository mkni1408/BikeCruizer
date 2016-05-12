package bikecruzer.aau.dk.bikecruizer;

import android.Manifest;
import android.app.Activity;
import android.app.Fragment;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Icon;
import android.location.Location;
import android.os.SystemClock;
import android.util.Log;

import com.google.android.gms.appdatasearch.DocumentContents;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.DecimalFormat;

/**
 * Created by michael on 29/04/16.
 */
public class Helpers {

    public static int volColorConverter (int vol){
        switch (vol){
            case 0:
                return Color.parseColor("#F5E000");
            case 1:
                return Color.parseColor("#F1BA00");
            case 2:
                return Color.parseColor("#EE9500");
            case 3:
                return Color.parseColor("#EB7000");
            case 4:
                return Color.parseColor("#E84B00");
            default:
                return Color.parseColor("#E52600");
        }
    }

    public static int speedColorConverter (int color){

        if(color < 5){
            return Color.parseColor("#F5E000");
        }
        else if (color >= 5 && color < 10){
            return Color.parseColor("#F1BA00");
        }
        else if (color >= 10 && color < 15){
            return Color.parseColor("#EE9500");
        }
        else if (color >= 15 && color < 20){
            return Color.parseColor("#EB7000");
        }
        else if (color >= 20){
            return Color.parseColor("#E84B00");
        }
        else {
            return Color.parseColor("#E52600");
        }
    }

    public static double calculationByDistance(LatLng StartP, LatLng EndP) {
        int Radius = 6371;// radius of earth in Km
        double lat1 = StartP.latitude;
        double lat2 = EndP.latitude;
        double lon1 = StartP.longitude;
        double lon2 = EndP.longitude;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = Radius * c;
        double km = valueResult / 1;
        DecimalFormat newFormat = new DecimalFormat("####");
        int kmInDec = Integer.valueOf(newFormat.format(km));
        double meter = valueResult % 1000;

        return Radius * c;
    }

    public static BitmapDescriptor resolveIcon (Activity activity){
        int icon = 0;

        if(Constants.walkOrCycle == 0){
            icon = R.drawable.maps_arrow;
        }
        else if(Constants.walkOrCycle == 2){
            icon = R.drawable.maps_arrow;
        }
        else if(Constants.walkOrCycle == 4){
            icon = R.drawable.maps_arrow;
        }
        else{
            icon = R.drawable.maps_arrow;
        }


        Bitmap bIcon = BitmapFactory.decodeResource(activity.getResources(),
                icon);
        Bitmap scaledIcon = Bitmap.createScaledBitmap(bIcon, 80, 80, false);

        return BitmapDescriptorFactory.fromBitmap(scaledIcon);

    }

    public static BitmapDescriptor resolveIPIcon (Activity activity, int icon, GoogleMap map){
        Double radius = Helpers.getRadius(map);
        int i;

        if(icon == 0){
            i = R.drawable.marker0;

        }
        else if(icon == 1){
            i = R.drawable.marker1;

        }
        else if(icon == 2){
            i = R.drawable.marker2;

        }
        else if(icon == 3){
            i = R.drawable.marker3;

        }
        else if(icon == 4){
            i = R.drawable.marker4;

        }
        else if(icon == 5){
            i = R.drawable.marker0;

        }
        else{
            i = R.drawable.marker0;
        }


        Bitmap bIcon = BitmapFactory.decodeResource(activity.getResources(),
                i);

        //float zoom = map.getCameraPosition().zoom;


        //Double iconSize = (((1 / radius) * 40));
        //Log.i("double val", Float.toString((1 / zoom)));

        Bitmap scaledIcon = Bitmap.createScaledBitmap(bIcon, 130, 130, false);

        return BitmapDescriptorFactory.fromBitmap(scaledIcon);
    }

    public static float getCameraZoomLevel (){

        if(Constants.walkOrCycle == 0 ||Constants.walkOrCycle == 2 ){
            return Constants.walkZoomLevel;
        }
        else if(Constants.walkOrCycle == 4){
            return Constants.bikeZoomLevel;

        }
        else{
            return Constants.bikeZoomLevel;
        }
    }

    public static void setCameraZoomAndCenter(Activity a, GoogleMap map, Location l){

        //do some version checking
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            if (a.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && a.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }

        //set lat lng - if not set we set it to aa midtby - is overridden if fake location is true

        LatLng latlng;
        if(Constants.fakeLocation){

            map.setMyLocationEnabled(false);
            latlng = Constants.AA_MIDTBY;
        }else{
            map.setMyLocationEnabled(true);
            if (l != null) {
                latlng = new LatLng(l.getLatitude(),l.getLongitude());

            }else{

                l = LocationServices.FusedLocationApi.getLastLocation(
                        MainActivity.mGoogleApiClient);
                if(l == null) {
                    latlng = Constants.AA_MIDTBY;
                }else{
                    latlng = new LatLng(l.getLatitude(),l.getLongitude());
                }
            }
        }


        Constants.isMapBeingRedrawn = true;

        if(map != null) {
            //here we set the new camera position
            if (l != null && Constants.fakeLocation != true) {
                Log.i("has bearing", Float.toString(l.getBearing()));
                CameraPosition cameraPosition = new CameraPosition.Builder()
                        .target(latlng)             // Sets the center of the map to current location
                        .zoom(Helpers.getCameraZoomLevel())                   // Sets the zoom
                        .bearing(l.getBearing()) // Sets the orientation of the camera to east
                        .tilt(0)                   // Sets the tilt of the camera to 0 degrees
                        .build();                   // Creates a CameraPosition from the builder
                //map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                map.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            } else {
                //zoom to view
                CameraPosition cameraPosition = new CameraPosition.Builder().target(latlng).zoom(Helpers.getCameraZoomLevel()).build();
                CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
                map.moveCamera(cameraUpdate);
                if(Constants.fakeLocation){
                    createFakeLocation(a,latlng,map);
                }
            }
        }

    }

    public static void updateMap(Fragment f, int type, Location l){

        if(type == 0){
            setSpeedMap(f, l);
        }
        else if(type == 1){
            setVolMap(f, l);
        }
        else if(type == 2){
            setPOIMap(f, l);
        }

    }

    public static void setSpeedMap(Fragment f, Location l){
        Main_Speedfragment frag = (Main_Speedfragment) f;
        //frag.updateLocation(l);
        if(frag != null) {
                Constants.isMapBeingRedrawn = true;
                frag.drawMap(SpeedRoutes.getSpeedRoutes());
        }

    }

    public static void setVolMap(Fragment f, Location l){

        Main_Volumefragment frag = (Main_Volumefragment) f;
        //frag.updateLocation(l);
        if(frag != null) {
            Constants.isMapBeingRedrawn = true;
            frag.drawMap(VolumeRoutes.getVolumeRoutes());
        }
    }

    public static void setPOIMap(Fragment f, Location l){

        Main_POIfragment frag = (Main_POIfragment) f;
        //frag.updateLocation(l);
        if(frag != null) {
            Constants.isMapBeingRedrawn = true;
            frag.drawMap(POIs.getPois());
        }
    }

    public static void createFakeLocation(Activity a, LatLng location, GoogleMap map){


        //try{
        //    Constants.currentMarker.setPosition(location);
        //    Constants.currentMarker.setRotation(44);
        //    Log.i("old icon","used");
        //}catch (Exception e){

        Constants.isMapBeingRedrawn = true;
        if(Constants.currentMarker != null) {
            Constants.currentMarker.remove();
        }

            Log.i("new icon","created");
            //add location pin
            BitmapDescriptor iconDesc = Helpers.resolveIcon(a);
            //add location pin
            Constants.currentMarker = map.addMarker(new MarkerOptions()
                    .position(location)
                    .icon(iconDesc));

            Constants.currentMarker.setRotation(0);
        //}


    };
    public static void updateLocation(Activity a, LatLng location, GoogleMap map, boolean init){
        //do some version checking
        if (android.os.Build.VERSION.SDK_INT >= 23) {
            if (a.checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && a.checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }


        return;
    }

    public static Double getRadius(GoogleMap map){
        //zoom to view
        LatLngBounds bounds = map.getProjection().getVisibleRegion().latLngBounds;
        LatLng northeast = bounds.northeast;
        LatLng southwest = bounds.southwest;
        return Helpers.calculationByDistance(northeast,southwest);
    }


}
