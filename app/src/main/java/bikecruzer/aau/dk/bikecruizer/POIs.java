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
    public static boolean fetching = false;
    public static ArrayList<POI> walkedPOIS = new ArrayList<POI>();
    private POIs(){

    }

    public static ArrayList<POI> getPois(){
        return POIs.pois;
    }

    public static void setPois (ArrayList<POI> pois){
        POIs.pois = pois;
    }

    public static ArrayList<POI> getWalkedPOIS(){
        return POIs.walkedPOIS;
    }

    public static void setWalkedPOIS (ArrayList<POI> pois){
        POIs.walkedPOIS = pois;
    }

    public static void addWalkedPOIS(POI poi){
        walkedPOIS.add(poi);
    }

    public static void fetchPois (ProgressDialog p, Main_POIfragment f){
        sf = f;
        startFetchning(p);
    }

    private static void startFetchning (ProgressDialog b){

        p = b;
        fetching = true;
        setPois(new ArrayList<POI>());

        p.setTitle("Fetching Areas of Interest");
        p.setMessage("Wait...");
        p.show();

        POIGenerator pgen = new POIGenerator();
        pgen.fetchPoints();
    }

    public static void stopFetchning (){
        fetching = false;
        //unset progressbar
        if(p != null) {
            p.hide();
            p = null;
        }
        if(sf != null){

            sf.drawMap(pois);
            sf = null;
        }

        //draw map by calling fragment method

    }
}
