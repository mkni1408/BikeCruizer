package bikecruzer.aau.dk.bikecruizer;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by michael on 08/05/16.
 */
public class InterestPoint {

    private int id;
    private LatLng position;
    private String name;
    private int icon;

    public InterestPoint(int ID, double LAT, double LNG, String name, int icon){
        this.id = ID;
        this.position = new LatLng(LAT,LNG);
        this.name = name;
        this.icon = icon;
    }

    public int getId(){
        return id;
    }
    public String getName(){ return  name; }
    public int getIcon(){ return icon;}
    public LatLng getPosition (){
        return position;
    }
    public double getLatitude (){return position.latitude;};
    public double getLongitude (){return position.longitude;};


}
