package bikecruzer.aau.dk.bikecruizer;

import android.app.ProgressDialog;

import java.util.ArrayList;

/**
 * Created by michael on 10-05-2016.
 */
public class POIs {
    private static ArrayList<POI> pois = new ArrayList<POI>();
    private static ProgressDialog p = null;
    private static Main_POIfragment sf;
    private POIs(){

    }

    public static ArrayList<POI> getPois(){
        return POIs.pois;
    }

    public static void setPois (ArrayList<POI> pois){
        POIs.pois = pois;
    }

    public static void fetchPois (ProgressDialog p, Main_POIfragment f){
        sf = f;
        startFetchning(p);
    }

    private static void startFetchning (ProgressDialog b){

        p = b;

        p.setTitle("Fetching POI's");
        p.setMessage("Wait...");
        p.show();

        POIGenerator pgen = new POIGenerator();
        pgen.fetchPoints();
    }

    public static void stopFetchning (){
        //unset progressbar
        p.hide();
        p = null;

        sf.drawMap(pois);
        sf = null;

        //draw map by calling fragment method

    }
}
