package bikecruzer.aau.dk.bikecruizer;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.InputSource;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import bikecruzer.aau.dk.bikecruizer.POI;

/**
 * Created by michael on 18/04/16.
 */
public class POIGenerator extends AsyncTask<String,Void,ArrayList<POI>> {

    private ArrayList<POI> points;
    //private GoogleMap map = null;
    //private Activity activity;
    //private ProgressDialog progressSpinner = null;

    public POIGenerator (){
        this.points = new ArrayList<POI>();
    }

    public ArrayList<POI> generatePoints (){

        fetchPoints();

        return null;
    }


    public void fetchPoints () {
        this.execute("http://michaelkvistnielsen.dk/CLINES/POI.php/getAllPOIs");

    }

    private Exception exception;

    protected ArrayList<POI> doInBackground(String... urls) {

        try {
            URL url= new URL(urls[0]);
            BufferedInputStream bis = new BufferedInputStream(url.openStream());
            byte[] buffer = new byte[1024];
            StringBuilder sb = new StringBuilder();
            int bytesRead = 0;
            while((bytesRead = bis.read(buffer)) > 0) {
                String text = new String(buffer, 0, bytesRead);
                sb.append(text);
            }
            bis.close();

            JSONArray jArray = new JSONArray(sb.toString());

            for (int i=0; i < jArray.length(); i++)
            {
                try {
                    JSONObject oneObject = jArray.getJSONObject(i);

                    //JSONObject jsonArr = oneObject.getJSONObject("ratings");

                    POIRatings ratings = new POIRatings(oneObject.getInt("dining"),oneObject.getInt("barsandpubs"),
                            oneObject.getInt("attractions"),oneObject.getInt("sights"),oneObject.getInt("museums"), oneObject.getInt("cafe"));

                    // Pulling items from the array
                    POI newPoi = new POI(
                            oneObject.getString("description"),
                            oneObject.getDouble("lat"),
                            oneObject.getDouble("lng"),
                            oneObject.getInt("size") * 10,
                            ratings,
                            oneObject.getInt("id")
                            );
                    points.add(newPoi);

                } catch (JSONException e) {
                    // Oops
                    Log.e(e.getMessage(),e.getLocalizedMessage());
                }
            }

            return null;

        } catch (Exception e) {
            this.exception = e;
            Log.e(e.getMessage(),e.getLocalizedMessage());
            return null;
        }
    }

    protected void onPostExecute(ArrayList<POI> feed) {
        // TODO: check this.exception
        // TODO: do something with the feed
        //if(this.points.size() < 1){
        //    this.progressSpinner.hide();
        //}else {
            //this.addPointsToMap();
        //}

        POIs.setPois(this.points);
        POIs.stopFetchning();
    }



}
