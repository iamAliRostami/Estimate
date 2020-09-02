package com.leon.estimate.fragments;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.leon.estimate.Enums.BundleEnum;
import com.leon.estimate.R;
import com.leon.estimate.Tables.ExaminerDuties;
import com.leon.estimate.activities.FormActivity;
import com.leon.estimate.databinding.MapFragmentBinding;

import org.jetbrains.annotations.NotNull;
import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static android.content.Context.LOCATION_SERVICE;


public class MapFragment extends Fragment implements LocationListener {
    private static final String ARG_PARAM2 = "param2";
    private double latitude;
    private double longitude;
    private LocationManager locationManager;
    private int polygonIndex;
    private int placeIndex;
    private ArrayList<GeoPoint> polygonPoint = new ArrayList<>();
    MapFragmentBinding binding;
    private Context context;

    public MapFragment() {
    }

    public static MapFragment newInstance(ExaminerDuties examinerDuties, String param2) {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        Gson gson = new Gson();
        String json = gson.toJson(examinerDuties);
        args.putString(BundleEnum.REQUEST.getValue(), json);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getActivity();
        ((FormActivity) Objects.requireNonNull(getActivity())).setActionBarTitle(
                context.getString(R.string.app_name).concat(" / ").concat(context.getString(R.string.location)));
        Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context));
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = MapFragmentBinding.inflate(inflater, container, false);
        initialize();
        return binding.getRoot();
    }

    private void initialize() {
        initializeMap();
        binding.imageViewRefresh.setOnClickListener(view -> {
            binding.mapView.getOverlays().clear();
            placeIndex = 0;
            polygonIndex = 0;
            polygonPoint.removeAll(polygonPoint);
            initializeMap();
        });
    }

    public Bitmap convertMapToBitmap() {
        binding.mapView.setDrawingCacheEnabled(true);
        return binding.mapView.getDrawingCache(true);
    }

    @SuppressLint("ObsoleteSdkInt")
    public void addRouteOverlay(GeoPoint startPoint, GeoPoint endPoint) {
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }

        RoadManager roadManager = new OSRMRoadManager(context);
        ArrayList<GeoPoint> wayPoints = new ArrayList<>();
        wayPoints.add(startPoint);
        wayPoints.add(endPoint);
        Road road = roadManager.getRoad(wayPoints);
        Polyline roadOverlay = RoadManager.buildRoadOverlay(road);
        binding.mapView.getOverlays().add(roadOverlay);
        binding.mapView.invalidate();
    }

    @SuppressLint("MissingPermission")
    private void initializeMap() {
        binding.mapView.setBuiltInZoomControls(true);
        binding.mapView.setMultiTouchControls(true);
        IMapController mapController = binding.mapView.getController();
        mapController.setZoom(19.5);

        locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String bestProvider = String.valueOf(locationManager.getBestProvider(criteria, true));
        Location location = getLastKnownLocation();
        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        } else {
            locationManager.requestLocationUpdates(bestProvider, 0, 0, this);
        }
        GeoPoint startPoint = new GeoPoint(latitude, longitude);
//        startPoint = new GeoPoint(48.8583, 2.2944);
        mapController.setCenter(startPoint);
        MyLocationNewOverlay locationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(context), binding.mapView);
        locationOverlay.enableMyLocation();
        binding.mapView.getOverlays().add(locationOverlay);
        binding.mapView.getOverlays().add(new MapEventsOverlay(new MapEventsReceiver() {
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint p) {
//                Log.e("location1", p.toString());
                createPolygon(p);
                return false;
            }

            @Override
            public boolean longPressHelper(GeoPoint p) {
//                Log.e("location2", p.toString());
                addPlace(p);
                return false;
            }
        }));
//        addRouteOverlay(startPoint, new GeoPoint(48.8583, 2.2944));
    }

    private void addPlace(GeoPoint p) {
        GeoPoint startPoint = new GeoPoint(p.getLatitude(), p.getLongitude());
        Marker startMarker = new Marker(binding.mapView);
        startMarker.setPosition(startPoint);
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        if (placeIndex != 0) {//TODO crash on paging...
            binding.mapView.getOverlays().remove(placeIndex);
        }
        binding.mapView.getOverlays().add(startMarker);
        placeIndex = binding.mapView.getOverlays().size() - 1;
    }

    private void createPolygon(GeoPoint geoPoint) {
        Polyline line = new Polyline(binding.mapView);
        if (polygonIndex != 0) {//TODO crash on paging...
            binding.mapView.getOverlays().remove(polygonIndex);
        }
        binding.mapView.getOverlays().add(line);
//        ArrayList<GeoPoint> pts = new ArrayList<>(polygonPoint);
//        pts.add(geoPoint);
//        pts.add(pts.get(0));
//        line.setPoints(pts);
        polygonPoint.add(geoPoint);
        polygonPoint.add(polygonPoint.get(0));
        line.setPoints(polygonPoint);
        polygonPoint.remove(polygonPoint.size() - 1);
        polygonIndex = binding.mapView.getOverlays().size() - 1;
    }

    private Location getLastKnownLocation() {
        Location l = null;
        LocationManager mLocationManager = (LocationManager) Objects.requireNonNull(
                getActivity()).getSystemService(LOCATION_SERVICE);
        assert mLocationManager != null;
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            if (ContextCompat.checkSelfPermission(context,
                    Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                l = mLocationManager.getLastKnownLocation(provider);
            }
            if (l == null) {
                continue;
            }
            if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                bestLocation = l;
            }
        }
        return bestLocation;
    }

    @Override
    public void onLocationChanged(Location location) {
        locationManager.removeUpdates(this);
        latitude = location.getLatitude();
        longitude = location.getLongitude();
        initializeMap();
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override
    public void onProviderEnabled(@NotNull String provider) {
    }

    @Override
    public void onProviderDisabled(@NotNull String provider) {
    }

    @Override
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
