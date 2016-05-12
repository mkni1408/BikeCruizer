package bikecruzer.aau.dk.bikecruizer;

import android.app.ProgressDialog;
import android.widget.ProgressBar;

import java.util.ArrayList;

/**
 * Created by michael on 10-05-2016.
 */
public final class SpeedRoutes {
    private static ArrayList<SpeedRoute> speedRoutes = new ArrayList<SpeedRoute>();
    private static ProgressDialog p = null;
    private static Main_Speedfragment sf;
    private SpeedRoutes(){

    }

    public static ArrayList<SpeedRoute> getSpeedRoutes(){
        return SpeedRoutes.speedRoutes;
    }

    public static void setSpeedRoutes (ArrayList<SpeedRoute> routes){
        SpeedRoutes.speedRoutes = routes;
    }

    public static void fetchSpeedRoutes (ProgressDialog p, Main_Speedfragment f){
        sf = f;
        startFetchning(p);
    }

    private static void startFetchning (ProgressDialog b){

        p = b;

        p.setTitle("Fetching routes");
        p.setMessage("Wait...");
        p.show();

        SpeedGenerator sgen = new SpeedGenerator();
        sgen.generateRoutes();
    }

    public static void stopFetchning (){
        //unset progressbar
        p.hide();
        p = null;

        sf.drawMap(speedRoutes);
        sf = null;

        //draw map by calling fragment method

    }
}