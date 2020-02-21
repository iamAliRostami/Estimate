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
import android.widget.RelativeLayout;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.leon.estimate.Activities.FormActivity;
import com.leon.estimate.R;
import com.leon.estimate.Utils.FontManager;
import com.mapbox.mapboxsdk.Mapbox;

import org.jetbrains.annotations.NotNull;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.infowindow.BasicInfoWindow;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.content.Context.LOCATION_SERVICE;


public class MapFragment extends Fragment implements LocationListener {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private double latitude;
    private double longitude;
    LocationManager locationManager;
    private String accessToken = "pk.eyJ1IjoiYWxpLWFuZ2VsIiwiYSI6ImNrNHBxenN0azB5YXozZXM3N2hiYWRndXMifQ.uinG5vJijYWskpmA52REfw";
    List<OverlayItem> overlayItemList = new ArrayList<OverlayItem>();
    String trackNumber;
    private int polygonIndex;
    private int placeIndex;
    private View findViewById;
    private MapView mapView = null;
    private MyLocationNewOverlay locationOverlay;
    private ArrayList<GeoPoint> polygonPoint = new ArrayList<>();
    private String mParam1;
    private String mParam2;
    @BindView(R.id.relativeLayout)
    RelativeLayout relativeLayout;
    @BindView(R.id.button_next)
    Button buttonNext;
    private Context context;

    public MapFragment() {

    }

    public static MapFragment newInstance(String param1, String param2) {
        MapFragment fragment = new MapFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
        context = getActivity();
        Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context));
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
            mapView.setDrawingCacheEnabled(true);
            Bitmap bitmap = mapView.getDrawingCache(true);
            ((FormActivity) getActivity()).nextPage(bitmap);
        });
    }

    @SuppressLint("MissingPermission")
    private void initializeMap() {
        mapView = findViewById.findViewById(R.id.mapView);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
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

    private Bitmap convertMapToBitmap() {
        mapView.setDrawingCacheEnabled(true);
        return mapView.getDrawingCache(true);
    }

    private byte[] convertBitmapToByte(Bitmap bitmap) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        return bos.toByteArray();
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
    public void onAttach(@NotNull Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
