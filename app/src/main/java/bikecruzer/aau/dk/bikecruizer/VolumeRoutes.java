package bikecruzer.aau.dk.bikecruizer;

import android.app.ProgressDialog;

import java.util.ArrayList;

/**
 * Created by michael on 10-05-2016.
 */
public final class VolumeRoutes {
    private static ArrayList<VolumeRoute> volumeRoutes = new ArrayList<VolumeRoute>();
    private static ProgressDialog p = null;
    private static Main_Volumefragment sf;
    private VolumeRoutes(){

    }

    public static ArrayList<VolumeRoute> getVolumeRoutes(){
        return VolumeRoutes.volumeRoutes;
    }

    public static void setVolumeRoutes (ArrayList<VolumeRoute> routes){
        VolumeRoutes.volumeRoutes = routes;
    }

    public static void fetchVolumeRoutes (ProgressDialog p, Main_Volumefragment f){
        sf = f;
        startFetchning(p);
    }

    private static void startFetchning (ProgressDialog b){

        p = b;

        p.setTitle("Fetching routes");
        p.setMessage("Wait...");
        p.show();

        VolumeGenerator vgen = new VolumeGenerator();
        vgen.generateRoutes();
    }

    public static void stopFetchning (){
        //unset progressbar
        if(p != null) {
            p.hide();
            p = null;
        }
        if(sf != null){

            sf.drawMap(volumeRoutes);
            sf = null;
        }


        //draw map by calling fragment method

    }
}
