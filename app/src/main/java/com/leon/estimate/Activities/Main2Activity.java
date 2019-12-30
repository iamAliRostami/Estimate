package com.leon.estimate.Activities;


import android.Manifest;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.location.LocationManagerCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.room.Room;

import com.google.android.material.navigation.NavigationView;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.leon.estimate.Enums.DialogType;
import com.leon.estimate.Enums.ProgressType;
import com.leon.estimate.R;
import com.leon.estimate.Tables.Calculation;
import com.leon.estimate.Tables.CalculationUserInput;
import com.leon.estimate.Tables.DaoCalculation;
import com.leon.estimate.Tables.DaoCalculationUserInput;
import com.leon.estimate.Tables.MyDatabase;
import com.leon.estimate.Utils.CustomDialog;
import com.leon.estimate.Utils.HttpClientWrapper;
import com.leon.estimate.Utils.IAbfaService;
import com.leon.estimate.Utils.ICallback;
import com.leon.estimate.Utils.NetworkHelper;
import com.leon.estimate.Utils.SimpleMessage;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.maps.MapboxMap;

import org.jetbrains.annotations.NotNull;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.infowindow.BasicInfoWindow;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Retrofit;

public class Main2Activity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, LocationListener {
    public double latitude;
    public double longitude;
    LocationManager locationManager;
    //    String accessToken = "pk.eyJ1IjoiaWFtYWxpcm9zdGFtaSIsImEiOiJjanhjbmptcmowMjZnM3BvdnY0YWx4ampxIn0.iv9I6s34q_-k9GqCiz2seg";
    String accessToken = "pk.eyJ1IjoiYWxpLWFuZ2VsIiwiYSI6ImNrNHBxenN0azB5YXozZXM3N2hiYWRndXMifQ.uinG5vJijYWskpmA52REfw";
    int REQUEST_LOCATION_CODE = 1236;
    ImageView imageViewExit, imageViewDownload, imageViewUpload, imageViewPaper, imageViewForm;
    String trackNumber;
    DrawerLayout drawer;
    Context context;
    private MapboxMap mapboxMap;
    private PermissionsManager permissionsManager;
    private MapView mapView = null;
    private MyLocationNewOverlay locationOverlay;
    private List<Point> routeCoordinates;
    View.OnClickListener onClickListener = view -> {
        Intent intent;
        switch (view.getId()) {
            case R.id.imageViewForm:
                intent = new Intent(getApplicationContext(), ListActivity.class);
                startActivity(intent);
                break;
            case R.id.imageViewDownload:
                download();
                break;
            case R.id.imageViewUpload:
                break;
            case R.id.imageViewPaper:
                intent = new Intent(getApplicationContext(), PaperActivity.class);
                startActivity(intent);
                break;
            case R.id.imageViewExit:
                finishAffinity();
                break;
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        context = this;
        if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            askPermission();
        } else {
            Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context));
            Mapbox.getInstance(this, accessToken);
            setContentView(R.layout.main2_activity);
            GpsEnabled();
            initialize();
        }
//        Room.databaseBuilder(context, MyDatabase.class, "MyDatabase")
//                .fallbackToDestructiveMigration()
//                .addMigrations(MyDatabase.MIGRATION_11_12).build();
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
        this.locationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(context), mapView);
        this.locationOverlay.enableMyLocation();

        test();

        mapView.getOverlays().add(locationOverlay);
        mapView.getOverlays().add(new MapEventsOverlay(new MapEventsReceiver() {
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint p) {
                Log.e("location1", p.toString());
                return false;
            }

            @Override
            public boolean longPressHelper(GeoPoint p) {
                Log.e("location2", p.toString());
                return false;
            }
        }));
    }

    void test() {
        Polyline line = new Polyline(mapView);
//        line.setTitle("Central Park, NYC");
        line.setSubDescription(Polyline.class.getCanonicalName());
        line.setWidth(20f);
        line.setColor(R.color.green1);
        List<GeoPoint> pts = new ArrayList<>();
        //here, we create a polygon, note that you need 5 points in order to make a closed polygon (rectangle)

        pts.add(new GeoPoint(32.70347921245878, 51.71537283422978));
        pts.add(new GeoPoint(32.704279694809834, 51.71409512700282));
        pts.add(new GeoPoint(32.70246839703522, 51.71404849535219));
        pts.add(new GeoPoint(32.86055430536678, 51.563165144538516));
        pts.add(new GeoPoint(32.861076853343896, 51.56335859857319));
        line.setPoints(pts);
        line.setGeodesic(true);
        line.setInfoWindow(new BasicInfoWindow(R.layout.bonuspack_bubble, mapView));
        //Note, the info window will not show if you set the onclick listener
        //line can also attach click listeners to the line

        line.setOnClickListener((polyline, mapView, eventPos) -> {
            return false;
        });
        mapView.getOverlayManager().add(line);
    }

    void initialize() {
        initializeMap();
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        drawer = findViewById(R.id.drawer_layout);
        drawer.openDrawer(GravityCompat.START);
        setImageViewFindByViewId();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    private void initRouteCoordinates(int i) {
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        assert lm != null;
        @SuppressLint("MissingPermission") Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        assert location != null;
        double longitude = location.getLongitude();
        double latitude = location.getLatitude();
        routeCoordinates = new ArrayList<>();
        routeCoordinates.add(Point.fromLngLat(longitude, latitude));
        routeCoordinates.add(Point.fromLngLat(longitude + i * 10, latitude + i * 10));
    }

    void setImageViewFindByViewId() {
        imageViewDownload = findViewById(R.id.imageViewDownload);
        imageViewDownload.setOnClickListener(onClickListener);

        imageViewUpload = findViewById(R.id.imageViewUpload);
        imageViewUpload.setOnClickListener(onClickListener);


        imageViewPaper = findViewById(R.id.imageViewPaper);
        imageViewPaper.setOnClickListener(onClickListener);

        imageViewExit = findViewById(R.id.imageViewExit);
        imageViewExit.setOnClickListener(onClickListener);

        imageViewForm = findViewById(R.id.imageViewForm);
        imageViewForm.setOnClickListener(onClickListener);
    }

    void download() {
        Retrofit retrofit = NetworkHelper.getInstance(true, "header");
        final IAbfaService getKardex = retrofit.create(IAbfaService.class);
        Call<List<Calculation>> call = getKardex.getMyWorks();
        Download download = new Download();
        HttpClientWrapper.callHttpAsync(call, download, context, ProgressType.SHOW.getValue());
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

    void send() {
        MyDatabase dataBase = Room.databaseBuilder(context, MyDatabase.class, "MyDatabase")
                .allowMainThreadQueries().build();
        DaoCalculationUserInput daoCalculationUserInput = dataBase.daoCalculationUserInput();
        List<CalculationUserInput> calculationUserInputList = daoCalculationUserInput.getCalculationUserInput();
        Log.e("size", String.valueOf(calculationUserInputList.size()));
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NotNull MenuItem item) {
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public final void askPermission() {
        PermissionListener permissionlistener = new PermissionListener() {
            @Override
            public void onPermissionGranted() {
                Toast.makeText(getApplicationContext(), "مجوز ها داده شده", Toast.LENGTH_SHORT).show();
                Intent intent = getIntent();
                finish();
                startActivity(intent);
            }

            @Override
            public void onPermissionDenied(ArrayList<String> deniedPermissions) {
                Toast.makeText(getApplicationContext(), "مجوز رد شد \n" + deniedPermissions.toString(), Toast.LENGTH_SHORT).show();
                forceClose(context);
            }
        };
        new TedPermission(this)
                .setPermissionListener(permissionlistener)
                .setRationaleMessage("جهت استفاده از برنامه مجوزهای پیشنهادی را قبول فرمایید")
                .setDeniedMessage("در صورت رد این مجوز قادر به استفاده از این دستگاه نخواهید بود" + "\n" +
                        "لطفا با فشار دادن دکمه اعطای دسترسی و سپس در بخش دسترسی ها با این مجوز ها موافقت نمایید")
////                .setGotoSettingButtonText("اعطای دسترسی")
                .setPermissions(
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                ).check();
    }

    private void forceClose(Context context) {
        new CustomDialog(DialogType.Red, context,
                context.getString(R.string.permission_not_completed),
                context.getString(R.string.dear_user),
                context.getString(R.string.call_operator),
                context.getString(R.string.force_close));
        finishAffinity();
    }

    private boolean GpsEnabled() {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean enabled = LocationManagerCompat.isLocationEnabled(Objects.requireNonNull(locationManager));
        if (!enabled) {
            AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
            alertDialog.setCancelable(false);
            alertDialog.setTitle("تنظیمات جی پی اس");
            alertDialog.setMessage("مکان یابی شما غیر فعال است، آیا مایلید به قسمت تنظیمات مکان یابی منتقل شوید؟");
            alertDialog.setPositiveButton("تنظیمات", (dialog, which) -> {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivityForResult(intent, REQUEST_LOCATION_CODE);
            });
            alertDialog.setNegativeButton("بستن برنامه", (dialog, which) -> finishAffinity());
            alertDialog.show();
        }
        return enabled;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    class Download implements ICallback<List<Calculation>> {
        @Override
        public void execute(List<Calculation> calculations) {
            MyDatabase dataBase = Room.databaseBuilder(context, MyDatabase.class, "MyDatabase")
                    .allowMainThreadQueries().build();
            DaoCalculation daoCalculation = dataBase.daoCalculateCalculation();
            daoCalculation.insertAll(calculations);
            new CustomDialog(DialogType.Green, context, "تعداد ".concat(String.valueOf(calculations.size())).concat(" مسیر بارگیری شد."),
                    getString(R.string.dear_user), getString(R.string.receive), getString(R.string.accepted));
        }
    }

    class SendCalculation implements ICallback<SimpleMessage> {
        @Override
        public void execute(SimpleMessage simpleMessage) {
            MyDatabase dataBase = Room.databaseBuilder(context, MyDatabase.class, "MyDatabase")
                    .allowMainThreadQueries().build();
            DaoCalculationUserInput daoCalculationUserInput = dataBase.daoCalculationUserInput();
            daoCalculationUserInput.updateCalculationUserInput(true, trackNumber);
        }
    }
}
