package bikecruzer.aau.dk.bikecruizer;

import android.Manifest;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewOverlay;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.identity.intents.Address;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.GroundOverlay;
import com.google.android.gms.maps.model.GroundOverlayOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

import org.json.JSONException;

import java.io.IOException;
import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import bikecruzer.aau.dk.bikecruizer.Adapters.PoiOverlayAdapter;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Main_POIfragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Main_POIfragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Main_POIfragment extends android.app.Fragment implements OnMapReadyCallback {
    // TODO: Rename parameter arguments, choose names that match
    private OnFragmentInteractionListener mListener;
    public GoogleMap map = null;
    private boolean mInitialized = false;
    private ClusterManager<POI> mClusterManager;

    public Main_POIfragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameterx$s.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Main_POIfragment.
     */
    // TODO: Rename and change types and number of parameters
    public static Main_POIfragment newInstance(String param1, String param2) {
        Main_POIfragment fragment = new Main_POIfragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }


    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_main__poifragment, container, false);


    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MapFragment mapFragment = (MapFragment) getChildFragmentManager().findFragmentById(R.id.poimap);

        Constants.currentFragmentIndex = 2;
        Constants.currentFragment = this;

        if (mapFragment != null) {
            LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            if (locationManager != null) {
                mapFragment.getMapAsync(this);
                //after this you do whatever you want with the map

            }
        }

        ProgressDialog progressSpinner = new ProgressDialog(this.getActivity());

        POIs.fetchPois(progressSpinner,this);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onPOIFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        this.map = googleMap;
        this.map.clear();
        //do some version checking

        if (android.os.Build.VERSION.SDK_INT >= 23) {
            if (getActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && getActivity().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
        }

        Location l = LocationServices.FusedLocationApi.getLastLocation(
                MainActivity.mGoogleApiClient);

        LatLng latlng;

        if (l != null) {
            latlng = new LatLng(l.getLatitude(),l.getLongitude());
        }else{
            latlng = Constants.AA_MIDTBY;
        }

        googleMap.setMyLocationEnabled(true);

        googleMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                if (android.os.Build.VERSION.SDK_INT >= 23) {
                    if (getActivity().checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                            && getActivity().checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    }
                }
                Location l = LocationServices.FusedLocationApi.getLastLocation(
                        MainActivity.mGoogleApiClient);

                LatLng latlng;

                if (l != null) {
                    latlng = new LatLng(l.getLatitude(),l.getLongitude());
                }else{
                    latlng = Constants.AA_MIDTBY;
                }
                Helpers.setCameraZoomAndCenter(getActivity(), map, l);
                return true;
            }
        });

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if(Constants.useMapTouch){
                    Constants.AA_MIDTBY = latLng;
                    Helpers.setCameraZoomAndCenter(getActivity(), map, null);
                }
            }
        });

        //set zoomlevel
        Helpers.setCameraZoomAndCenter(this.getActivity(), map, l);


        //listener to listen on markers
        //googleMap.setOnMarkerClickListener(this);
        //googleMap.setOnCameraChangeListener(this);

        //IPFetcher fetcher = new IPFetcher();
        //fetcher.fetch(this.map,this.getActivity());

        //spinner.setVisibility(View.VISIBLE);
    }

    public void updateLocation(LatLng location) {

        //update location
        Helpers.updateLocation(this.getActivity(),new LatLng(location.latitude,location.longitude),this.map,this.mInitialized);

        mInitialized = true;
    }


    /*@Override
    public void onCameraChange(CameraPosition cameraPosition) {

        if(!Constants.isMapBeingRedrawn) {
            Constants.startInteractionWithMap();
        }

        Constants.isMapBeingRedrawn = false;
        mInitialized = true;
    }*/

    public void drawMap (ArrayList<POI> points){

        this.map.clear();
        if(points.size() > -1) {
            this.addPointsToMap(points);
        }else{
            //progressSpinner.hide();
        }
        if(Constants.walkOrCycle > 2) {
            //InterestPoints.drawIPstoMap(this.getActivity(), this.map);
        }
        //Helpers.setCameraZoomAndCenter(this.getActivity(), this.map, null);
    }

    public void addPointsToMap (ArrayList<POI> points){

        //get and scale icon
        Log.i("Points to draw", Integer.toString(points.size()));

        mClusterManager = new ClusterManager<POI>(this.getActivity(), map);
        mClusterManager.setRenderer(new CustomRenderer(this.getActivity(), map, mClusterManager));

        map.setOnCameraChangeListener(mClusterManager);
        map.setOnMarkerClickListener(mClusterManager);
        mClusterManager.setOnClusterClickListener(new ClusterManager.OnClusterClickListener<POI>() {
            @Override
            public boolean onClusterClick(Cluster<POI> cluster) {
                //sString firstName = cluster.getItems().iterator().next().getName();
                //Toast.makeText(getActivity(), cluster.getSize() + " (including " + firstName + ")", Toast.LENGTH_SHORT).show();
                return false;
            }
        });

       /* map.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
              *//* LayoutInflater layoutInflater = LayoutInflater.from( getActivity() );
                ViewGroup root = (ViewGroup) getActivity().findViewById(R.id.nav_view);
                FrameLayout f = ( FrameLayout )layoutInflater.inflate( R.layout.poi_overlay, root,
                        false );

                root.getOverlay().add(f);*//*
                Dialog myDialog;
                myDialog =  new Dialog(getActivity());
                myDialog.setContentView(R.layout.poi_overlay);
                myDialog.setCancelable(true);
                myDialog.show();
                return false;
            }
        });*/

        /*map.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {

                return;
            }
        });*/

        mClusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<POI>() {
            @Override
            public boolean onClusterItemClick(POI poi) {
                Dialog myDialog;
                myDialog =  new Dialog(getActivity());
                myDialog.setContentView(R.layout.poi_overlay);
                myDialog.setCancelable(true);
                myDialog.setTitle(R.string.point_of_interest_ratings);

                ListView v = (ListView) myDialog.findViewById(R.id.ratingsListView);
                v.setAdapter(new PoiOverlayAdapter(getActivity(),poi.getRatings().getRatingsArray()));


                myDialog.show();
                return false;
            }
        });

        mClusterManager.addItems(points);



        //for (int i = 0 ; i < points.size(); i++) {


            /*this.map.addCircle(new CircleOptions()
                    .center(points.get(i).getPosition())
                    .radius(points.get(i).getNumPOI())
                    .strokeColor(Color.TRANSPARENT)
                    .fillColor(0x55ffb3ec)
                    .strokeWidth(5));*/

            //}

        //IPFetcher fetcher = new IPFetcher();
        //fetcher.fetch(this.map,this.activity);

        //this.progressSpinner.hide();

    }


    private GroundOverlay addcircleAroundMarker(int size, double lat, double lng) {
        // circle settings
        int radiusM = size;
        double latitude = lat;
        double longitude = lng;
        LatLng latLng = new LatLng(latitude,longitude);

        // draw circle
        // diameter
        int d = 500;
        Bitmap bm = Bitmap.createBitmap(d, d, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bm);
        Paint p = new Paint();
        p.setColor(0x55ffb3ec);
        c.drawCircle(d/2, d/2, d/2, p);

        BitmapDescriptor bmD = BitmapDescriptorFactory.fromBitmap(bm);

        return map.addGroundOverlay(new GroundOverlayOptions().
                image(bmD).
                position(latLng,radiusM*2,radiusM*2).
                transparency(0.4f));
    }



    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onPOIFragmentInteraction(Uri uri);
    }


    class CustomRenderer extends DefaultClusterRenderer<POI>
    {
        public CustomRenderer(Context context, GoogleMap map, ClusterManager<POI> clusterManager) {
            super(context, map, clusterManager);
        }



        protected void onBeforeClusterItemRendered(POI item, MarkerOptions markerOptions) {


            /*Log.i("zoom",Float.toString(map.getCameraPosition().zoom));
            float zoom = map.getCameraPosition().zoom;
            int size = (int)zoom * 7;

            Bitmap mDotMarkerBitmap = Bitmap.createBitmap(size,size, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(mDotMarkerBitmap);
            Drawable shape = getResources().getDrawable(R.drawable.poicircle);
            shape.setBounds(0, 0, size, size);
            shape.draw(canvas);*/

            //markerOptions.icon(BitmapDescriptorFactory.fromBitmap(mDotMarkerBitmap));
            markerOptions.title(item.getName());
            super.onBeforeClusterItemRendered(item, markerOptions);
            item.setOverlay(addcircleAroundMarker(item.getNumPOI(), item.getPosition().latitude, item.getPosition().longitude));
        }

        protected void onBeforeClusterRendered(Cluster<POI> cluster, MarkerOptions m){

            for (POI elem : cluster.getItems()) {
                if(elem.getOverlay() != null){
                    elem.getOverlay().remove();
                    elem.setOverlay(null);
                }
            }


            super.onBeforeClusterRendered(cluster, m);
        }



        @Override
        protected boolean shouldRenderAsCluster(Cluster<POI> cluster) {
            //start clustering if at least 2 items overlap
            return cluster.getSize() > 1;
        }

    }

}


