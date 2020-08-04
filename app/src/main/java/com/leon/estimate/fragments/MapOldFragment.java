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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.leon.estimate.Enums.BundleEnum;
import com.leon.estimate.R;
import com.leon.estimate.Tables.CalculationUserInput;
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
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.infowindow.BasicInfoWindow;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static android.content.Context.LOCATION_SERVICE;


public class MapOldFragment extends Fragment implements LocationListener {
    private static final String ARG_PARAM2 = "param2";
    List<OverlayItem> overlayItemList = new ArrayList<OverlayItem>();
    String trackNumber;
    MapFragmentBinding binding;
    private double latitude;
    private double longitude;
    private LocationManager locationManager;
    private int polygonIndex;
    private int placeIndex;
    private MyLocationNewOverlay locationOverlay;
    private ArrayList<GeoPoint> polygonPoint = new ArrayList<>();
    private View findViewById;
    private MapView mapView = null;
    private Context context;
    private ExaminerDuties examinerDuties;
    private CalculationUserInput calculationUserInput;

    public MapOldFragment() {
    }

    public static MapOldFragment newInstance(ExaminerDuties examinerDuties, String param2) {
        MapOldFragment fragment = new MapOldFragment();
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
        if (getArguments() != null) {
            String json = getArguments().getString(BundleEnum.REQUEST.getValue());
            Gson gson = new GsonBuilder().create();
            examinerDuties = gson.fromJson(json, ExaminerDuties.class);
            String mParam2 = getArguments().getString(ARG_PARAM2);
        }
        context = getActivity();
        Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        findViewById = inflater.inflate(R.layout.map_fragment, container, false);
        binding = MapFragmentBinding.inflate(inflater, container, false);
        initialize();
        return binding.getRoot();
    }

    private void initialize() {
        initializeMap();
        binding.buttonNext.setOnClickListener(v -> {
//            mapView.setDrawingCacheEnabled(true);
//            Bitmap bitmap = mapView.getDrawingCache(true);
//            ((FormActivity) getActivity()).nextPage(bitmap);
            if (prepareForm()) {
                calculationUserInput = new CalculationUserInput();
                calculationUserInput.nationalId = binding.editTextNationNumber.getText().toString();
                calculationUserInput.firstName = binding.editTextName.getText().toString();
                calculationUserInput.sureName = binding.editTextFamily.getText().toString();
                calculationUserInput.fatherName = binding.editTextFatherName.getText().toString();
                calculationUserInput.postalCode = binding.editTextPostalCode.getText().toString();
                calculationUserInput.radif = binding.editTextRadif.getText().toString();
                calculationUserInput.phoneNumber = binding.editTextPhone.getText().toString();
                calculationUserInput.mobile = binding.editTextMobile.getText().toString();
                calculationUserInput.address = binding.editTextAddress.getText().toString();
                calculationUserInput.description = binding.editTextDescription.getText().toString();
                ((FormActivity) Objects.requireNonNull(getActivity())).nextPage(
                        convertMapToBitmap(), calculationUserInput);
            }
        });
        initializeField();
    }

    private Bitmap convertMapToBitmap() {
        mapView.setDrawingCacheEnabled(true);
        return mapView.getDrawingCache(true);
    }

    private boolean prepareForm() {
        return checkIsNoEmpty(binding.editTextShenasname)
                && checkIsNoEmpty(binding.editTextName)
                && checkIsNoEmpty(binding.editTextFamily)
                && checkIsNoEmpty(binding.editTextFatherName)
                && checkIsNoEmpty(binding.editTextEshterak)
                && checkIsNoEmpty(binding.editTextRadif)
                && checkIsNoEmpty(binding.editTextAddress)
                && checkOtherIsNoEmpty()
                ;
    }

    private boolean checkIsNoEmpty(EditText editText) {
        View focusView;
        if (editText.getText().toString().length() < 1) {
            focusView = editText;
            focusView.requestFocus();
            return false;
        }
        return true;
    }

    private boolean checkOtherIsNoEmpty() {
        View focusView;
        if (binding.editTextNationNumber.getText().toString().length() < 10) {
            focusView = binding.editTextNationNumber;
            focusView.requestFocus();
            return false;
        } else if (binding.editTextPostalCode.getText().toString().length() < 10) {
            focusView = binding.editTextPostalCode;
            focusView.requestFocus();
            return false;
        } else if (binding.editTextPhone.getText().toString().length() < 8) {
            focusView = binding.editTextPhone;
            focusView.requestFocus();
            return false;
        } else if (binding.editTextMobile.getText().toString().length() < 9) {
            focusView = binding.editTextMobile;
            focusView.requestFocus();
            return false;
        }
        return true;
    }

    private void initializeField() {
        binding.editTextAddress.setText(examinerDuties.getAddress());
        binding.editTextName.setText(examinerDuties.getFirstName());
        binding.editTextFamily.setText(examinerDuties.getSureName());
        binding.editTextNationNumber.setText(examinerDuties.getNationalId());
        binding.editTextFatherName.setText(examinerDuties.getFatherName());
        binding.editTextDescription.setText(examinerDuties.getDescription());
        binding.editTextPhone.setText(examinerDuties.getPhoneNumber());
        binding.editTextMobile.setText(examinerDuties.getMobile());
        binding.editTextEshterak.setText(examinerDuties.getEshterak().trim());
        binding.editTextPostalCode.setText(examinerDuties.getPostalCode());
        binding.editTextRadif.setText(examinerDuties.getRadif());
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
        mapView.getOverlays().add(roadOverlay);
        mapView.invalidate();
    }

    @SuppressLint("MissingPermission")
    private void initializeMap() {
        mapView = findViewById.findViewById(R.id.mapView);
//        mapView.setTileSource(TileSourceFactory.MAPNIK);
//        mapView.setTileSource(CUSTOM);
        mapView.setBuiltInZoomControls(true);
        mapView.setMultiTouchControls(true);
        IMapController mapController = mapView.getController();
        mapController.setZoom(19.5);

        locationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        String bestProvider = String.valueOf(locationManager.getBestProvider(criteria, true));
        Location location = getLastKnownLocation();
        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
        } else {
            locationManager.requestLocationUpdates(bestProvider, 1000, 0, this);
        }
        GeoPoint startPoint = new GeoPoint(latitude, longitude);
//        startPoint = new GeoPoint(48.8583, 2.2944);
        mapController.setCenter(startPoint);
        locationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(context), mapView);
        locationOverlay.enableMyLocation();
        mapView.getOverlays().add(locationOverlay);
        mapView.getOverlays().add(new MapEventsOverlay(new MapEventsReceiver() {
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint p) {
                Log.e("location1", p.toString());
                createPolygon(p);
                return false;
            }

            @Override
            public boolean longPressHelper(GeoPoint p) {
                addPlace(p);
                Log.e("location2", p.toString());
                return false;
            }
        }));
    }

    private void addPlace(GeoPoint p) {
        GeoPoint startPoint = new GeoPoint(p.getLatitude(), p.getLongitude());
        Marker startMarker = new Marker(mapView);
        startMarker.setPosition(startPoint);
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
//        mapView.getOverlays().add(startMarker);
        if (placeIndex != 0) {//TODO crash on paging...
            mapView.getOverlayManager().remove(placeIndex);
        }
        mapView.getOverlayManager().add(startMarker);
        placeIndex = mapView.getOverlays().size() - 1;
    }

    private void createPolygon(GeoPoint geoPoint) {
        Polyline line = new Polyline(mapView);
        line.setTitle("محل شما");
        line.setSubDescription(Polyline.class.getCanonicalName());
        line.setWidth(20f);
        line.setColor(R.color.green1);

        List<GeoPoint> pts = new ArrayList<>(polygonPoint);
        pts.add(geoPoint);
        pts.add(pts.get(0));
        polygonPoint.add(geoPoint);

        line.setPoints(pts);
        line.setGeodesic(true);
        line.setInfoWindow(new BasicInfoWindow(R.layout.bonuspack_bubble, mapView));
        line.setOnClickListener((polyline, mapView, eventPos) -> {
            return false;
        });
        if (polygonIndex != 0) {//TODO crash on paging...
            mapView.getOverlayManager().remove(polygonIndex);
        }

        mapView.getOverlayManager().add(line);
        polygonIndex = mapView.getOverlays().size() - 1;
    }


    private Location getLastKnownLocation() {
        Location l = null;
        LocationManager mLocationManager = (LocationManager) Objects.requireNonNull(getActivity()).getSystemService(LOCATION_SERVICE);
        assert mLocationManager != null;
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
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
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

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
