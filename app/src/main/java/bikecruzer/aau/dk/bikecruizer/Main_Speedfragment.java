package bikecruzer.aau.dk.bikecruizer;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.DecimalFormat;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Main_Speedfragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Main_Speedfragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Main_Speedfragment extends Fragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener, GoogleMap.OnCameraChangeListener{
    // TODO: Rename parameter arguments, choose names that match

    public GoogleMap map = null;
    private OnFragmentInteractionListener mListener;
    //private SpeedGenerator sgen = new SpeedGenerator();
    private boolean mInitialized = false;

    public Main_Speedfragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Main_Volumefragment.
     */
    // TODO: Rename and change types and number of parameters
    public static Main_Volumefragment newInstance(String param1, String param2) {
        Main_Volumefragment fragment = new Main_Volumefragment();
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
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        MapFragment mapFragment = (MapFragment) getChildFragmentManager().findFragmentById(R.id.speedmap);

        Constants.currentFragmentIndex = 0;
        Constants.currentFragment = this;

        if (mapFragment != null) {
            LocationManager locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            if (locationManager != null) {
                mapFragment.getMapAsync(this);
                //after this you do whatever you want with the map

            }
        }
        ProgressDialog progressSpinner = new ProgressDialog(this.getActivity());

        SpeedRoutes.fetchSpeedRoutes(progressSpinner,this);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        this.map = googleMap;
        this.map.clear();

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
            latlng = Constants.FAKELOCATION;
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
                    latlng = Constants.FAKELOCATION;
                }
                Helpers.setCameraZoomAndCenter(getActivity(), map, l, false);
                return true;
            }
        });

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                if(Constants.useMapTouch){
                    Constants.FAKELOCATION = latLng;
                    Helpers.setCameraZoomAndCenter(getActivity(), map, null, false);
                }
            }
        });
        //set zoomlevel
        Helpers.setCameraZoomAndCenter(this.getActivity(), map, l, false);


        //listener to listen on markers
        googleMap.setOnMarkerClickListener(this);
        googleMap.setOnCameraChangeListener(this);


        //spinner.setVisibility(View.VISIBLE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_main__speedfragment, container, false);
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
        void onSpeedFragmentInteraction(Uri uri);
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        Toast t = Toast.makeText(getActivity(),marker.getTitle(),Toast.LENGTH_LONG);
        t.show();
        return false;
    }

    @Override
    public void onCameraChange(CameraPosition cameraPosition) {

        if(!Constants.isMapBeingRedrawn) {
            Constants.startInteractionWithMap();
        }


        Constants.isMapBeingRedrawn = false;

        mInitialized = true;
    }

    public void updateLocation(LatLng location) {

        //this.sgen = new SpeedGenerator();

        //update location
        Helpers.updateLocation(this.getActivity(),new LatLng(location.latitude,location.longitude),this.map,this.mInitialized);

        Double radius = Helpers.getRadius(this.map);

        mInitialized = true;
    }

    public void drawMap (ArrayList<SpeedRoute> routes){
        if(this.map != null) {
            this.map.clear();
            if (routes.size() > -1) {
                this.excecuteRouteMapper(routes.size() - 1);
            } else {
                //progressSpinner.hide();
            }
        }
    }

    public void excecuteRouteMapper (Integer counter){

        if(counter < 0){
            //progressSpinner.hide();
            if(Constants.walkOrCycle > 2) {
                InterestPoints.drawIPstoMap(this.getActivity(), this.map);
            }
            Helpers.setCameraZoomAndCenter(this.getActivity(), this.map, null, false);
        }else {
            SpeedRouteMapper rm = new SpeedRouteMapper(SpeedRoutes.getSpeedRoutes().get(counter), this.getActivity(), map, counter, this);
            rm.execute();
        }


    }
}


