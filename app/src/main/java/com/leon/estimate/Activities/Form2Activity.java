package com.leon.estimate.Activities;

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
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.leon.estimate.R;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.maps.MapboxMap;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.infowindow.BasicInfoWindow;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.List;

public class Form2Activity extends AppCompatActivity implements LocationListener {
    public double latitude;
    public double longitude;
    LocationManager locationManager;
    String accessToken = "pk.eyJ1IjoiYWxpLWFuZ2VsIiwiYSI6ImNrNHBxenN0azB5YXozZXM3N2hiYWRndXMifQ.uinG5vJijYWskpmA52REfw";
    String trackNumber;
    Context context;
    List<OverlayItem> overlayItemList = new ArrayList<OverlayItem>();
    int polygonIndex;
    private MapboxMap mapboxMap;
    private MapView mapView = null;
    private MyLocationNewOverlay locationOverlay;
    private ArrayList<GeoPoint> polygonPoint = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        context = this;
        Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context));
        Mapbox.getInstance(this, accessToken);
        setContentView(R.layout.form2_activity);
        initialize();
    }

    void initialize() {
        initializeMap();
    }

    @SuppressLint("MissingPermission")
    void initializeMap() {
        mapView = findViewById(R.id.mapView);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setBuiltInZoomControls(true);
        mapView.setMultiTouchControls(true);
        IMapController mapController = mapView.getController();
        mapController.setZoom(19.5);

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
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
                Log.e("location2", p.toString());
                return false;
            }
        }));
    }

    void createPolygon(GeoPoint geoPoint) {
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
        ImageView imageView = findViewById(R.id.imageView1);
        if (polygonIndex != 0) {
            mapView.getOverlayManager().remove(polygonIndex);
            imageView = findViewById(R.id.imageView2);
        }

        mapView.getOverlayManager().add(line);
        polygonIndex = mapView.getOverlays().size() - 1;

        mapView.setDrawingCacheEnabled(true);
        Bitmap bitMap = mapView.getDrawingCache(true);

        imageView.setImageBitmap(null);
        imageView.setImageBitmap(bitMap);

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
        LocationManager mLocationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        assert mLocationManager != null;
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
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
}
