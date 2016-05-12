package bikecruzer.aau.dk.bikecruizer;

import android.app.Activity;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.concurrent.Exchanger;

/**
 * Created by michael on 08/05/16.
 */
public class IPFetcher extends AsyncTask<String,Void, ArrayList<InterestPoint>> {

    private ArrayList<InterestPoint> points = new ArrayList<InterestPoint>();
    private Exception exception;
    //private GoogleMap map;
    //private Activity a;

    public IPFetcher(){

    }

    public void fetch(){
        //this.map = map;
        //this.a = a;
        this.execute("http://michaelkvistnielsen.dk/CLINES/InterestPoint.php/getInterestPoints");
    }

    @Override
    protected ArrayList<InterestPoint> doInBackground(String... strings) {
        Log.i("fetcher fetching",strings[0]);
        try {
            URL url= new URL(strings[0]);
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
                    // Pulling items from the array
                    InterestPoint newIP = new InterestPoint(oneObject.getInt("id"),
                            oneObject.getDouble("lat"),
                            oneObject.getDouble("lng"),
                            oneObject.getString("name"),
                            oneObject.getInt("icon")
                    );

                    Log.i("icon", Integer.toString(newIP.getIcon()));
                    points.add(newIP);

                } catch (JSONException e) {
                    // Oops
                }
            }


            return null;

        } catch (Exception e) {
            this.exception = e;
            return null;
        }
    }

    protected void onPostExecute(ArrayList<InterestPoint> feed) {
        // TODO: check this.exception
        // TODO: do something with the feed
        if(this.points.size() < 1){
        }else {
            //this.drawIPtoMap();
            InterestPoints.setInterestPoints(points);
            InterestPoints.stopFetchning();
        }
    }

}
