package bikecruzer.aau.dk.bikecruizer;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by michael on 02/05/16.
 */
public class SpeedGenerator extends AsyncTask<String,Void,ArrayList<SpeedRoute>> {

    private ArrayList<SpeedRoute> routes;
    private GoogleMap map = null;
    private Activity activity;
    private Integer speedRouteCounter = 0;

    public SpeedGenerator() {
    }

    public ArrayList<VolumeRoute> generateRoutes() {
        this.routes = new ArrayList<SpeedRoute>();
        /*this.map = map;
        this.activity = a;
        progressSpinner = new ProgressDialog(a);
        progressSpinner.setTitle("Drawing map");
        progressSpinner.setMessage("Wait...");
        progressSpinner.show();*/
        fetchSpeedRoutes();

        return null;
    }


    public void fetchSpeedRoutes() {
        //Log.i("Querystring", "http://michaelkvistnielsen.dk/CLINES/POI.php/getPOIsWithinRadius/"+ Double.toString(latLng.latitude) +"/" + Double.toString(latLng.longitude) + "/" + Double.toString(radius));
        this.execute("http://michaelkvistnielsen.dk/CLINES/RouteSpeed.php/getSpeedRoutes");
        //+ Double.toString(latLng.latitude) +"/" + Double.toString(latLng.longitude) + "/" + Double.toString(radius));

    }

    private Exception exception;

    protected ArrayList<SpeedRoute> doInBackground(String... urls) {

        try {
            URL url = new URL(urls[0]);
            BufferedInputStream bis = new BufferedInputStream(url.openStream());
            byte[] buffer = new byte[1024];
            StringBuilder sb = new StringBuilder();
            int bytesRead = 0;
            while ((bytesRead = bis.read(buffer)) > 0) {
                String text = new String(buffer, 0, bytesRead);
                sb.append(text);
            }
            bis.close();


            JSONArray jArray = new JSONArray(sb.toString());

            for (int i = 0; i < jArray.length(); i++) {
                try {
                    JSONObject oneObject = jArray.getJSONObject(i);
                    // Pulling items from the array
                    SpeedRoute newRoute = new SpeedRoute(
                            oneObject.getDouble("latStart"),
                            oneObject.getDouble("lngStart"),
                            oneObject.getDouble("latEnd"),
                            oneObject.getDouble("lngEnd"),
                            oneObject.getInt("speed"),
                            oneObject.getInt("id")
                    );
                    routes.add(newRoute);

                } catch (JSONException e) {
                    // Oops
                    return this.routes;
                }
            }


            return this.routes;

        } catch (Exception e) {
            this.exception = e;
            return this.routes;
        }
    }

    protected void onPostExecute(ArrayList<SpeedRoute> mapRoutes) {
        // TODO: check this.exception
        this.speedRouteCounter = this.routes.size() - 1;

        if (this.exception != null) {
            Log.d("exception: in", "SpeedGenerator:onPostExcecute" + exception.getMessage());
        } else {
            if(this.speedRouteCounter > -1) {
                //this.excecuteRouteMapper(this.speedRouteCounter);
            }else{
                //progressSpinner.hide();
            }
        }

        SpeedRoutes.setSpeedRoutes(this.routes);
        SpeedRoutes.stopFetchning();

        //IPFetcher fetcher = new IPFetcher();
        //fetcher.fetch(this.map,this.activity);
    }

    public void excecuteRouteMapper (Integer counter){

        if(counter < 0){
            //progressSpinner.hide();
        }else {
            //SpeedRouteMapper rm = new SpeedRouteMapper(this.routes.get(counter), activity, map, counter, this);
            //rm.execute();
        }


    }
}
