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
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.leon.estimate.Enums.BundleEnum;
import com.leon.estimate.Enums.ProgressType;
import com.leon.estimate.Infrastructure.IAbfaService;
import com.leon.estimate.Infrastructure.ICallback;
import com.leon.estimate.Infrastructure.ICallbackError;
import com.leon.estimate.Infrastructure.ICallbackIncomplete;
import com.leon.estimate.R;
import com.leon.estimate.Tables.ExaminerDuties;
import com.leon.estimate.Tables.GISToken;
import com.leon.estimate.Tables.Place;
import com.leon.estimate.Utils.CoordinateConversion;
import com.leon.estimate.Utils.HttpClientWrapper;
import com.leon.estimate.Utils.NetworkHelper;
import com.leon.estimate.activities.FormActivity;
import com.leon.estimate.databinding.MapFragmentBinding;

import org.jetbrains.annotations.NotNull;
import org.osmdroid.api.IMapController;
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

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

import static android.content.Context.LOCATION_SERVICE;
import static com.leon.estimate.activities.FormActivity.examinerDuties;


public class MapFragment extends Fragment implements LocationListener {
    private static final String ARG_PARAM2 = "param2";
    CoordinateConversion conversion;
    private LocationManager locationManager;
    private double latitude, longitude;
    private ArrayList<GeoPoint> polygonPoint = new ArrayList<>();
    private int polygonIndex, place1Index, place2Index, place3Index;
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
            place1Index = 0;
            place2Index = 0;
            polygonIndex = 0;
            polygonPoint.removeAll(polygonPoint);
            initializeMap();
        });
    }

    public Bitmap convertMapToBitmap() {
        binding.mapView.setDrawingCacheEnabled(true);
        return binding.mapView.getDrawingCache(true);
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
        mapController.setCenter(startPoint);
        MyLocationNewOverlay locationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(context), binding.mapView);
        locationOverlay.enableMyLocation();
        binding.mapView.getOverlays().add(locationOverlay);
        conversion = new CoordinateConversion();

        if (examinerDuties.getBillId() != null)
            getXY(examinerDuties.getBillId());
        else getXY(examinerDuties.getNeighbourBillId());
        getGISToken();
        binding.mapView.getOverlays().add(new MapEventsOverlay(new MapEventsReceiver() {
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint p) {
                Log.e("location1", p.toString());
                createPolygon(p);
                return false;
            }

            @Override
            public boolean longPressHelper(GeoPoint p) {
                Log.e("location2", p.toString());
                addPlace(p);
                return false;
            }
        }));
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void addPlace(GeoPoint p) {
        GeoPoint startPoint = new GeoPoint(p.getLatitude(), p.getLongitude());
        Marker startMarker = new Marker(binding.mapView);
        startMarker.setPosition(startPoint);
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        if (place1Index != 0 && place2Index == 0) {//TODO crash on paging...
            startMarker.setIcon(getResources().getDrawable(R.drawable.map_siphon_drop_point));
            binding.mapView.getOverlays().add(startMarker);
            place2Index = binding.mapView.getOverlays().size() - 2;
        } else if (place2Index != 0) {
            binding.mapView.getOverlays().remove(place1Index);
            binding.mapView.getOverlays().remove(place2Index);
            place1Index = 0;
            place2Index = 0;
        }
        if (place1Index == 0) {
            startMarker.setIcon(getResources().getDrawable(R.drawable.map_water_drop_point));
            binding.mapView.getOverlays().add(startMarker);
            place1Index = binding.mapView.getOverlays().size() - 1;
        }
    }

    private void addUserPlace(GeoPoint p) {
        GeoPoint startPoint = new GeoPoint(p.getLatitude(), p.getLongitude());
        Marker startMarker = new Marker(binding.mapView);
        startMarker.setPosition(startPoint);
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        binding.mapView.getOverlayManager().add(startMarker);
    }

    private void createPolygon(GeoPoint geoPoint) {
        Polyline line = new Polyline(binding.mapView);
        if (polygonIndex != 0) {//TODO crash on paging...
            binding.mapView.getOverlays().remove(polygonIndex);
        }
        binding.mapView.getOverlays().add(line);
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

    void getXY(String billId) {
        Retrofit retrofit = NetworkHelper.getInstance(true, "");
        IAbfaService iAbfaService = retrofit.create(IAbfaService.class);
        Call<Place> call = iAbfaService.getXY(billId);
        HttpClientWrapper.callHttpAsync(call, ProgressType.SHOW.getValue(), context,
                new GetXY(), new GetXYIncomplete(), new GetError());
    }

    void getGISToken() {
        Retrofit retrofit = NetworkHelper.getInstance(true, "");
        IAbfaService iAbfaService = retrofit.create(IAbfaService.class);
        Call<GISToken> call = iAbfaService.getGISToken();
        HttpClientWrapper.callHttpAsync(call, ProgressType.SHOW.getValue(), context,
                new GetGISToken(), new GetGISTokenIncomplete(), new GetError());
    }

    class GetGISToken implements ICallback<GISToken> {
        @Override
        public void execute(GISToken gisToken) {

        }
    }

    class GetGISTokenIncomplete implements ICallbackIncomplete<GISToken> {
        @Override
        public void executeIncomplete(Response<GISToken> response) {

        }
    }

    class GetXY implements ICallback<Place> {
        @Override
        public void execute(Place place) {
            if (place.getX() != 0 && place.getY() != 0) {
                String utm = "39 S ".concat(String.valueOf(place.getX())).concat(" ")
                        .concat(String.valueOf(place.getY()));
                double[] latLong = conversion.utm2LatLon(utm);
                addUserPlace(new GeoPoint(latLong[0], latLong[1]));
            }
        }
    }

    class GetXYIncomplete implements ICallbackIncomplete<Place> {
        @Override
        public void executeIncomplete(Response<Place> response) {
        }
    }

    class GetError implements ICallbackError {
        @Override
        public void executeError(Throwable t) {
        }
    }
}
