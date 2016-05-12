package bikecruzer.aau.dk.bikecruizer;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by michael on 02/05/16.
 */
public class SpeedRoute {
    private LatLng startPosition;
    private Integer speed;
    private LatLng endPosition;
    private Integer id;

    public SpeedRoute (double latStart, double lngStart, double latEnd, double lngEnd, Integer speed, Integer id){
        this.startPosition = new LatLng(latStart,lngStart);
        this.endPosition = new LatLng(latEnd,lngEnd);
        this.speed = speed;
        this.id = id;
    }


    public SpeedRoute (LatLng start, LatLng end, Integer speed, Integer id){
        this.startPosition = start;
        this.endPosition = end;
        this.speed = speed;
        this.id = id;
    }

    public LatLng getStartPosition(){return this.startPosition;};

    public LatLng getEndPosition(){return this.endPosition;};

    public Integer getSpeed () {return this.speed; };
}
