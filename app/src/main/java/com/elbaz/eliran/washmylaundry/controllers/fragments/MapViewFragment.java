package com.elbaz.eliran.washmylaundry.controllers.fragments;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.elbaz.eliran.washmylaundry.R;
import com.elbaz.eliran.washmylaundry.base.BaseFragment;
import com.elbaz.eliran.washmylaundry.controllers.fragments.bottomSheets.UserPreOrderBottomSheet;
import com.elbaz.eliran.washmylaundry.models.Provider;
import com.elbaz.eliran.washmylaundry.repositories.CurrentUserDataRepository;
import com.elbaz.eliran.washmylaundry.viewmodel.UserViewModel;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import butterknife.OnClick;

import static android.content.ContentValues.TAG;
import static com.elbaz.eliran.washmylaundry.models.Constants.MAXIMUM_ZOOM_PREFERENCE;
import static com.elbaz.eliran.washmylaundry.models.Constants.MINIMUM_ZOOM_PREFERENCE;

/**
 * Created by Eliran Elbaz on 06-Feb-20.
 */
public class MapViewFragment extends BaseFragment implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
//    @BindView(R.id.bottom_menu_current_available_providers) TextView availableProvidersStatus;
    private TextView availableProvidersStatus;

    private FusedLocationProviderClient mFusedLocationProviderClient;
    private static final float DEFAULT_ZOOM = 16.5f ;
    private GoogleMap mMap;
    private LatLng currentLocation;
    private UserViewModel mUserViewModel;
    private List<Provider> mAvailableProvidersList = new ArrayList<>();


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(getFragmentLayout(), container, false);
        // Views
        availableProvidersStatus = (TextView) view.findViewById(R.id.bottom_menu_current_available_providers);
        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity().getApplicationContext());
        // Initialise map
        this.initMap();
        this.configureViewModel();

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        CurrentUserDataRepository.getInstance().getCurrentUserLatLng().observe(this, this::updateCurrentLatLng);
        mUserViewModel.getProvidersList().observe(getViewLifecycleOwner(), new Observer<List<Provider>>() {
            @Override
            public void onChanged(List<Provider> providers) {
                mAvailableProvidersList.clear();
                mAvailableProvidersList.addAll(providers);
                updateMapWithMarkers();
                updateStatusTextView();
            }
        });
    }

    //---------------------
    //  CONFIGURATIONS
    //---------------------
    @Override
    protected int getFragmentLayout() {
        return R.layout.fragment_map_view;
    }

    private void configureViewModel() {
        mUserViewModel = new ViewModelProvider(this).get(UserViewModel.class);
        mUserViewModel.setProviderList("isProvider"); // Trigger the listener to get ProviderList ('isProvider' is always true- to get all default results without delivery filter)
    }

    private void updateCurrentLatLng(LatLng latLng) {
        this.currentLocation = latLng;
    }

    // Initialise Google Map
    private void initMap(){
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Marker click listener
        mMap.setOnMarkerClickListener((GoogleMap.OnMarkerClickListener) this);
        // Map-Zoom limitations
        mMap.setMinZoomPreference(MINIMUM_ZOOM_PREFERENCE);
        mMap.setMaxZoomPreference(MAXIMUM_ZOOM_PREFERENCE);
        // Map configurations
        mMap.setBuildingsEnabled(true);
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.getUiSettings().setIndoorLevelPickerEnabled(false);

        // Hiding Map Features (settings inside style_strings.xml)
        boolean success = googleMap.setMapStyle(new MapStyleOptions(getResources()
                .getString(R.string.style_json)));
        if (!success) {
            Log.e(TAG, "Style parsing failed.");
        }
        moveCamera(new LatLng(currentLocation.latitude, currentLocation.longitude), DEFAULT_ZOOM);
    }

    //---------------
    // ACTIONS
    //---------------

    @OnClick(R.id.top_information_bar)
    public void onInformationLayoutClick(){
        alertDialogAction("Select a Provider", "Please select a nearby provider on the map by clicking on a marker");
    }


    @Override
    public boolean onMarkerClick(Marker marker) {
        Provider provider = new Provider();
        for(int i = 0 ; i<mAvailableProvidersList.size() ; i++){
            if(mAvailableProvidersList.get(i).getPid().equals(marker.getTag())) {
                provider = mAvailableProvidersList.get(i);
            }
        }
        UserPreOrderBottomSheet.newInstance("providerObject", new Gson().toJson(provider)).show(getActivity().getSupportFragmentManager(), "preOrder");
        return false;
    }


    //---------------
    // UI
    //---------------

    // A method to move the camera(map) to specific location by passing LatLng and Zoom
    private void moveCamera(LatLng latLng, float zoom){
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));
    }

    private void updateStatusTextView(){
        if(mAvailableProvidersList.size() > 0){
            availableProvidersStatus.setText(getString(R.string.currently_available_providers, String.valueOf(mAvailableProvidersList.size())));
            availableProvidersStatus.setTextColor(getResources().getColor(R.color.quantum_grey600));
        }else {
            availableProvidersStatus.setText(getString(R.string.currently_no_available_providers));
            availableProvidersStatus.setTextColor(Color.RED);
        }

//        availableProvidersStatus.setText(String.valueOf(mAvailableProvidersList.size()));
    }

    private void updateMapWithMarkers() {
        Log.d(TAG, "updateMapWithMarkers: ");
        if(mMap != null) mMap.clear(); // Clear all markers before every update
        for (int i = 0 ; i<mAvailableProvidersList.size() ; i++){
            LatLng latLng = new LatLng(mAvailableProvidersList.get(i).getProviderLatCoordinates(), mAvailableProvidersList.get(i).getProviderLngCoordinates());
            setCustomMarker(latLng, i, mAvailableProvidersList.get(i).getPid());
        }
    }

    // set custom marker
    private void setCustomMarker(LatLng latLng, int i, String tagID){
        try {
            mMap.addMarker(new MarkerOptions().position(latLng)
                    .icon(BitmapDescriptorFactory.fromBitmap(createCustomMarker(getActivity(), R.drawable.amu_bubble_mask, i , tagID))))
                    .setTag(tagID);
        }catch (Exception e){ }
    }

    // CustomMarker Bitmap
    public Bitmap createCustomMarker(Context context, @DrawableRes int resource, int index, String restaurantID) {
        View marker;
        marker = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.custom_marker_layout, null);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        marker.setLayoutParams(new ViewGroup.LayoutParams(52, ViewGroup.LayoutParams.WRAP_CONTENT));
        marker.measure(displayMetrics.widthPixels, displayMetrics.heightPixels);
        marker.layout(0, 0, displayMetrics.widthPixels, displayMetrics.heightPixels);
        marker.buildDrawingCache();
        Bitmap bitmap = Bitmap.createBitmap(marker.getMeasuredWidth(), marker.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        marker.draw(canvas);

        return bitmap;
    }

}
