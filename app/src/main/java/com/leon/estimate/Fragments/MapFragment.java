package com.leon.estimate.Fragments;

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
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.leon.estimate.Activities.FormActivity;
import com.leon.estimate.Enums.BundleEnum;
import com.leon.estimate.R;
import com.leon.estimate.Tables.CalculationUserInput;
import com.leon.estimate.Tables.ExaminerDuties;
import com.leon.estimate.Utils.FontManager;
import com.mapbox.mapboxsdk.Mapbox;

import org.jetbrains.annotations.NotNull;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase;
import org.osmdroid.tileprovider.tilesource.TileSourcePolicy;
import org.osmdroid.tileprovider.tilesource.XYTileSource;
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

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.Context.LOCATION_SERVICE;


public class MapFragment extends Fragment implements LocationListener {
    private static final String ARG_PARAM2 = "param2";
    private double latitude;
    private double longitude;
    private LocationManager locationManager;
    List<OverlayItem> overlayItemList = new ArrayList<OverlayItem>();
    String trackNumber;
    private int polygonIndex;
    private int placeIndex;
    private View findViewById;
    private MapView mapView = null;
    private MyLocationNewOverlay locationOverlay;
    private ArrayList<GeoPoint> polygonPoint = new ArrayList<>();

    @BindView(R.id.relativeLayout)
    RelativeLayout relativeLayout;
    @BindView(R.id.button_next)
    Button buttonNext;

    @BindView(R.id.editTextNationNumber)
    EditText editTextNationNumber;
    @BindView(R.id.editTextShenasname)
    EditText editTextShenasname;
    @BindView(R.id.editTextName)
    EditText editTextName;
    @BindView(R.id.editTextFamily)
    EditText editTextFamily;
    @BindView(R.id.editTextPostalCode)
    EditText editTextPostalCode;
    @BindView(R.id.editTextRadif)
    EditText editTextRadif;
    @BindView(R.id.editTextPhone)
    EditText editTextPhone;
    @BindView(R.id.editTextMobile)
    EditText editTextMobile;
    @BindView(R.id.editTextAddress)
    EditText editTextAddress;
    @BindView(R.id.editTextEshterak)
    EditText editText26;
    @BindView(R.id.editTextDescription)
    EditText editTextDescription;
    @BindView(R.id.editTextFatherName)
    EditText editTextFatherName;
    private OnlineTileSourceBase CUSTOM = new XYTileSource("Mapnik",
            0, 19, 256, ".png", new String[]{
            "https://172.18.12.242:80"}, "© OpenStreetMap contributors",
            new TileSourcePolicy(2,
                    TileSourcePolicy.FLAG_NO_BULK
                            | TileSourcePolicy.FLAG_NO_PREVENTIVE
                            | TileSourcePolicy.FLAG_USER_AGENT_MEANINGFUL
                            | TileSourcePolicy.FLAG_USER_AGENT_NORMALIZED
            ));
    private Context context;
    private ExaminerDuties examinerDuties;
    private CalculationUserInput calculationUserInput;

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
        if (getArguments() != null) {
            String json = getArguments().getString(BundleEnum.REQUEST.getValue());
            Gson gson = new GsonBuilder().create();
            examinerDuties = gson.fromJson(json, ExaminerDuties.class);
            String mParam2 = getArguments().getString(ARG_PARAM2);
        }
        context = getActivity();
        Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context));
        String accessToken = "pk.eyJ1IjoiYWxpLWFuZ2VsIiwiYSI6ImNrNHBxenN0azB5YXozZXM3N2hiYWRndXMifQ.uinG5vJijYWskpmA52REfw";
        Mapbox.getInstance(context, accessToken);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        findViewById = inflater.inflate(R.layout.map_fragment, container, false);
        ButterKnife.bind(this, findViewById);
        initialize();
        return findViewById;
    }

    private void initialize() {
        initializeMap();
        FontManager fontManager = new FontManager(context);
        fontManager.setFont(relativeLayout);
        buttonNext.setOnClickListener(v -> {
//            mapView.setDrawingCacheEnabled(true);
//            Bitmap bitmap = mapView.getDrawingCache(true);
//            ((FormActivity) getActivity()).nextPage(bitmap);
            if (prepareForm()) {
                calculationUserInput = new CalculationUserInput();
                calculationUserInput.nationalId = editTextNationNumber.getText().toString();
                calculationUserInput.firstName = editTextName.getText().toString();
                calculationUserInput.sureName = editTextFamily.getText().toString();
                calculationUserInput.fatherName = editTextFatherName.getText().toString();
                calculationUserInput.postalCode = editTextPostalCode.getText().toString();
                calculationUserInput.radif = editTextRadif.getText().toString();
                calculationUserInput.phoneNumber = editTextPhone.getText().toString();
                calculationUserInput.mobile = editTextMobile.getText().toString();
                calculationUserInput.address = editTextAddress.getText().toString();
                calculationUserInput.description = editTextDescription.getText().toString();
                ((FormActivity) getActivity()).nextPage(convertMapToBitmap(), calculationUserInput);
            }
        });
        initializeField();
    }

    private Bitmap convertMapToBitmap() {
        mapView.setDrawingCacheEnabled(true);
        return mapView.getDrawingCache(true);
    }

    private boolean prepareForm() {
        return checkIsNoEmpty(editTextShenasname)
                && checkIsNoEmpty(editTextName)
                && checkIsNoEmpty(editTextFamily)
                && checkIsNoEmpty(editTextFatherName)
                && checkIsNoEmpty(editText26)
                && checkIsNoEmpty(editTextRadif)
                && checkIsNoEmpty(editTextAddress)
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
        if (editTextNationNumber.getText().toString().length() < 10) {
            focusView = editTextNationNumber;
            focusView.requestFocus();
            return false;
        } else if (editTextPostalCode.getText().toString().length() < 10) {
            focusView = editTextPostalCode;
            focusView.requestFocus();
            return false;
        } else if (editTextPhone.getText().toString().length() < 8) {
            focusView = editTextPhone;
            focusView.requestFocus();
            return false;
        } else if (editTextMobile.getText().toString().length() < 9) {
            focusView = editTextMobile;
            focusView.requestFocus();
            return false;
        }
        return true;
    }

    private void initializeField() {
        editTextAddress.setText(examinerDuties.getAddress());
        editTextName.setText(examinerDuties.getFirstName());
        editTextFamily.setText(examinerDuties.getSureName());
        editTextNationNumber.setText(examinerDuties.getNationalId());
        editTextFatherName.setText(examinerDuties.getFatherName());
        editTextDescription.setText(examinerDuties.getDescription());
        editTextPhone.setText(examinerDuties.getPhoneNumber());
        editTextMobile.setText(examinerDuties.getMobile());
        editText26.setText(examinerDuties.getEshterak().trim());
        editTextPostalCode.setText(examinerDuties.getPostalCode());
        editTextRadif.setText(examinerDuties.getRadif());
    }

    @SuppressLint("MissingPermission")
    private void initializeMap() {
        mapView = findViewById.findViewById(R.id.mapView);

//        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setTileSource(CUSTOM);
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
