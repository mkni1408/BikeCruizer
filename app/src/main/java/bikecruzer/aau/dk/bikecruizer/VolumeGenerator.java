package bikecruzer.aau.dk.bikecruizer;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ProgressBar;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by michael on 29/04/16.
 */
public class VolumeGenerator extends AsyncTask<String,Void,ArrayList<VolumeRoute>> {

    private ArrayList<VolumeRoute> routes;
    private GoogleMap map = null;
    private Activity activity;
    private Integer volumeCounter = 0;

    public VolumeGenerator(){ }

    public ArrayList<VolumeRoute> generateRoutes(){
        this.routes = new ArrayList<VolumeRoute>();
        this.map = map;

        this.fetchVolRoutes();
        return null;
    }


    public void fetchVolRoutes () {

        //Log.i("Querystring", "http://michaelkvistnielsen.dk/CLINES/POI.php/getPOIsWithinRadius/"+ Double.toString(latLng.latitude) +"/" + Double.toString(latLng.longitude) + "/" + Double.toString(radius));
        this.execute("http://michaelkvistnielsen.dk/CLINES/RouteVolume.php/getVolumeRoutes");
                //+ Double.toString(latLng.latitude) +"/" + Double.toString(latLng.longitude) + "/" + Double.toString(radius));

    }

    private Exception exception;

    protected ArrayList<VolumeRoute> doInBackground(String... urls) {

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
                    // Pulling items from the array
                    VolumeRoute newRoute = new VolumeRoute(
                            oneObject.getDouble("latStart"),
                            oneObject.getDouble("lngStart"),
                            oneObject.getDouble("latEnd"),
                            oneObject.getDouble("lngEnd"),
                            oneObject.getInt("volume")
                    );
                    routes.add(newRoute);

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

    protected void onPostExecute(ArrayList<VolumeRoute> mapRoutes) {
        this.volumeCounter = this.routes.size() - 1;
        // TODO: check this.exception
        if(this.exception != null){
            Log.d("exception: in","VolumeGenerator:onPostExcecute" + exception.getMessage());
        }else{
            if(this.volumeCounter > -1) {
                //this.executeVolumeMapper(this.volumeCounter);
            }else{
                //progressSpinner.hide();
            }
        }

        VolumeRoutes.setVolumeRoutes(this.routes);
        VolumeRoutes.stopFetchning();

        //IPFetcher fetcher = new IPFetcher();
        //fetcher.fetch(this.map,this.activity);
    }


    public void executeVolumeMapper (Integer counter){

        /*if(counter < 0){
            progressSpinner.hide();
        }else {
            VolumeRouteMapper rm = new VolumeRouteMapper(activity, map, counter, this.routes.get(counter), this);
            rm.execute();
        }*/

    }
//    public void addPointsToMap (){
//
//        //get and scale icon
//
//        for (int i = 0 ; i < this.points.size(); i++) {
//            //get and scale icon
//            /*icon = BitmapFactory.decodeResource(this.activity.getResources(),
//                    R.drawable.bullseye);
//            scaledIcon = Bitmap.createScaledBitmap(icon, 10 * this.points.get(i).getNumPOI(), 10 * this.points.get(i).getNumPOI(), false);
//            iconDesc = BitmapDescriptorFactory.fromBitmap(scaledIcon);*/
//            this.map.addCircle(new CircleOptions()
//                    .center(this.points.get(i).getPosition())
//                    .radius(this.points.get(i).getNumPOI())
//                    .strokeColor(Color.WHITE)
//                    .fillColor(Color.LTGRAY)
//                    .strokeWidth(5));
//
//            /*this.map.addMarker(new MarkerOptions()
//                    .position(this.points.get(i).getPosition())
//                    .title(this.points.get(i).getName())
//                    .icon(iconDesc));*/
//        }
//
//    }
}
