package bikecruzer.aau.dk.bikecruizer;

import android.app.Activity;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by michael on 29/04/16.
 */
public class VolumeRouteMapper extends AsyncTask<VolumeRoute,Void,ArrayList<VolumeRoute>> {
    private Activity mainActivity;
    private GoogleMap gmap;
    private VolumeRoute volRoute;
    private Main_Volumefragment context;
    private Integer count;

    public VolumeRouteMapper(VolumeRoute route, Activity a, GoogleMap map, Integer counter, Main_Volumefragment context){
        this.volRoute = route;
        this.mainActivity = a;
        this.gmap = map;
        this.context = context;
        this.count = counter;
    }

    public void drawRoutes (){
        if(volRoute != null){
            this.execute();
        }
    }

    @Override
    protected ArrayList<VolumeRoute> doInBackground(VolumeRoute... urls){

        RequestQueue mRequestQueue;

        if(mainActivity != null) {
            // Instantiate the cache
            Cache cache = new DiskBasedCache(mainActivity.getCacheDir(), 1024 * 1024); // 1MB cap
            // Set up the network to use HttpURLConnection as the HTTP client.
            Network network = new BasicNetwork(new HurlStack());
            // Instantiate the RequestQueue with the cache and network.
            mRequestQueue = new RequestQueue(cache, network);
            // Start the queue
            mRequestQueue.start();

            VolumeRoute v = volRoute;
            String url = makeRouteURL(v.getStartPosition().latitude, v.getStartPosition().longitude,
                    v.getEndPosition().latitude, v.getEndPosition().longitude);

            // Formulate the request and handle the response.
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            // Do something with the response
                            drawPath(response);
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // Handle error
                        }
                    });

            // Add the request to the RequestQueue.
            mRequestQueue.add(stringRequest);
        }
        return null;
    }

    public String makeRouteURL (double sourcelat, double sourcelog, double destlat, double destlog ){
        StringBuilder urlString = new StringBuilder();
        urlString.append("https://maps.googleapis.com/maps/api/directions/json");
        urlString.append("?origin=");// from
        urlString.append(Double.toString(sourcelat));
        urlString.append(",");
        urlString
                .append(Double.toString( sourcelog));
        urlString.append("&destination=");// to
        urlString
                .append(Double.toString( destlat));
        urlString.append(",");
        urlString.append(Double.toString( destlog));
        urlString.append("&sensor=false&mode=bicycling&alternatives=true");
        urlString.append("&key=AIzaSyAZEBEren6GVmSbjumPL3b3nwJGEL2COZ8");
        return urlString.toString();
    }

    public void drawPath(String result) {

        try {
            //Tranform the string into a json object
            final JSONObject json = new JSONObject(result);
            JSONArray routeArray = json.getJSONArray("routes");
            JSONObject routes = routeArray.getJSONObject(0);
            JSONObject overviewPolylines = routes.getJSONObject("overview_polyline");
            String encodedString = overviewPolylines.getString("points");
            List<LatLng> list = decodePoly(encodedString);
            Polyline line = this.gmap.addPolyline(new PolylineOptions()
                    .addAll(list)
                    .width(10)
                    .color(Helpers.volColorConverter(this.volRoute.getVolume()))//Google maps blue color
                    .geodesic(true)
            );
                this.count --;
                this.context.executeVolumeMapper(this.count);

        }
        catch (JSONException e) {

        }
    }

    private List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<LatLng>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng( (((double) lat / 1E5)),
                    (((double) lng / 1E5) ));
            poly.add(p);
        }

        return poly;
    }


}
