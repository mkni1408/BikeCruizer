package bikecruzer.aau.dk.bikecruizer;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by michael on 29/04/16.
 */
public class VolumeRoute {
    private LatLng startPosition;
    private Integer volume;
    private LatLng endPosition;

    public VolumeRoute (double latStart, double lngStart, double latEnd, double lngEnd, Integer vol){
        this.startPosition = new LatLng(latStart,lngStart);
        this.endPosition = new LatLng(latEnd,lngEnd);
        this.volume = vol;
    }


    public VolumeRoute (LatLng start, LatLng end, Integer vol){
        this.startPosition = start;
        this.endPosition = end;
        this.volume = vol;
    }

    public LatLng getStartPosition(){return this.startPosition;};

    public LatLng getEndPosition(){return this.endPosition;};

    public Integer getVolume () {return this.volume; };

}
