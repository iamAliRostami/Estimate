package com.leon.estimate.activities;

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
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.location.LocationManagerCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.room.Room;

import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.gun0912.tedpermission.PermissionListener;
import com.gun0912.tedpermission.TedPermission;
import com.leon.estimate.Enums.BundleEnum;
import com.leon.estimate.Enums.DialogType;
import com.leon.estimate.Enums.ProgressType;
import com.leon.estimate.Enums.SharedReferenceKeys;
import com.leon.estimate.Enums.SharedReferenceNames;
import com.leon.estimate.Infrastructure.IAbfaService;
import com.leon.estimate.Infrastructure.ICallback;
import com.leon.estimate.Infrastructure.ICallbackError;
import com.leon.estimate.Infrastructure.ICallbackIncomplete;
import com.leon.estimate.MyApplication;
import com.leon.estimate.R;
import com.leon.estimate.Tables.CalculationUserInput;
import com.leon.estimate.Tables.CalculationUserInputSend;
import com.leon.estimate.Tables.DaoCalculationUserInput;
import com.leon.estimate.Tables.DaoExaminerDuties;
import com.leon.estimate.Tables.DaoKarbariDictionary;
import com.leon.estimate.Tables.DaoNoeVagozariDictionary;
import com.leon.estimate.Tables.DaoQotrEnsheabDictionary;
import com.leon.estimate.Tables.DaoServiceDictionary;
import com.leon.estimate.Tables.DaoTaxfifDictionary;
import com.leon.estimate.Tables.ExaminerDuties;
import com.leon.estimate.Tables.Input;
import com.leon.estimate.Tables.MyDatabase;
import com.leon.estimate.Tables.Place;
import com.leon.estimate.Utils.CoordinateConversion;
import com.leon.estimate.Utils.CustomDialog;
import com.leon.estimate.Utils.CustomErrorHandlingNew;
import com.leon.estimate.Utils.CustomProgressBar;
import com.leon.estimate.Utils.HttpClientWrapper;
import com.leon.estimate.Utils.HttpClientWrapperOld;
import com.leon.estimate.Utils.NetworkHelper;
import com.leon.estimate.Utils.SharedPreferenceManager;
import com.leon.estimate.Utils.SimpleMessage;
import com.leon.estimate.databinding.MainActivityBinding;

import org.jetbrains.annotations.NotNull;
import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.config.Configuration;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.infowindow.InfoWindow;
import org.osmdroid.views.overlay.infowindow.MarkerInfoWindow;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, LocationListener {
    public double latitude, longitude;
    LocationManager locationManager;
    MainActivityBinding binding;
    String trackNumber;
    DrawerLayout drawer;
    int REQUEST_LOCATION_CODE = 1236, wayIndex = 0, counter = 0;
    Polyline roadOverlay;
    MapView mapView;
    Context context;
    ArrayList<Integer> indexArray;
    ArrayList<GeoPoint> wayPoints, placesPoints;
    CoordinateConversion conversion;
    List<ExaminerDuties> examinerDuties, examinerDutiesReady;
    //    boolean isShown = false;
    ArrayList<Boolean> isShown;
    boolean doubleBackToExitPressedOnce = false;
    List<CalculationUserInput> calculationUserInputList;
    Toolbar toolbar;
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
                send();
                break;
            case R.id.imageViewPaper:
                intent = new Intent(getApplicationContext(), TakeOtherPhotoActivity.class);
                startActivity(intent);
                break;
            case R.id.imageViewExit:
                finishAffinity();
                break;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        context = this;
        binding = MainActivityBinding.inflate(getLayoutInflater());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED ||
                    checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED
            ) {
                askPermission();
            } else {
                Configuration.getInstance().load(context,
                        PreferenceManager.getDefaultSharedPreferences(context));
                setContentView(binding.getRoot());
                initialize();
            }
        }
//        Room.databaseBuilder(context, MyDatabase.class, MyApplication.getDBNAME())
//                .fallbackToDestructiveMigration()
//                .addMigrations(MyDatabase.MIGRATION_22_23).build();
//        readData();
    }

    public void readData() {
        File sdcard = Environment.getExternalStorageDirectory();
        File file = new File(sdcard, "json.txt");
        StringBuilder text = new StringBuilder();
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String line;
            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
            br.close();
        } catch (IOException ignored) {
        }
        String json = text.toString();
        Log.e("json", json);
        Gson gson = new GsonBuilder().create();
        Input input = gson.fromJson(json, Input.class);
        List<ExaminerDuties> examinerDutiesList = input.getExaminerDuties();
        for (int i = 0; i < examinerDutiesList.size(); i++) {
            Gson gson1 = new Gson();
            examinerDutiesList.get(i).setRequestDictionaryString(
                    gson1.toJson(examinerDutiesList.get(i).getRequestDictionary()));
        }
        MyDatabase dataBase = Room.databaseBuilder(context, MyDatabase.class, MyApplication.getDBNAME())
                .allowMainThreadQueries().build();
        DaoExaminerDuties daoExaminerDuties = dataBase.daoExaminerDuties();
        List<ExaminerDuties> examinerDutiesListTemp = daoExaminerDuties.getExaminerDuties();
        for (int i = 0; i < examinerDutiesList.size(); i++) {
            examinerDutiesList.get(i).setTrackNumber(
                    examinerDutiesList.get(i).getTrackNumber().replace(".0", ""));
            examinerDutiesList.get(i).setRadif(
                    examinerDutiesList.get(i).getRadif().replace(".0", ""));
            ExaminerDuties examinerDuties = examinerDutiesList.get(i);
            for (int j = 0; j < examinerDutiesListTemp.size(); j++) {
                ExaminerDuties examinerDutiesTemp = examinerDutiesListTemp.get(j);
                if (examinerDuties.getTrackNumber().equals(examinerDutiesTemp.getTrackNumber())) {
                    examinerDutiesList.remove(i);
                    j = examinerDutiesListTemp.size();
                    i--;
                }
            }
        }
        daoExaminerDuties.insertAll(examinerDutiesList);
        DaoNoeVagozariDictionary daoNoeVagozariDictionary = dataBase.daoNoeVagozariDictionary();
        daoNoeVagozariDictionary.insertAll(input.getNoeVagozariDictionary());
        DaoQotrEnsheabDictionary daoQotrEnsheabDictionary = dataBase.daoQotrEnsheabDictionary();
        daoQotrEnsheabDictionary.insertAll(input.getQotrEnsheabDictionary());
        DaoServiceDictionary daoServiceDictionary = dataBase.daoServiceDictionary();
        daoServiceDictionary.insertAll(input.getServiceDictionary());
        DaoTaxfifDictionary daoTaxfifDictionary = dataBase.daoTaxfifDictionary();
        daoTaxfifDictionary.insertAll(input.getTaxfifDictionary());
        DaoKarbariDictionary daoKarbariDictionary = dataBase.daoKarbariDictionary();
        daoKarbariDictionary.insertAll(input.getKarbariDictionary());
    }

    @SuppressLint("WrongConstant")
    void initialize() {
        toolbar = findViewById(R.id.toolbar);
        drawer = findViewById(R.id.drawer_layout);
        setActionBarTitle("خانه");
        drawer.openDrawer(GravityCompat.START);
        setImageViewClickListener();
        initializeMap();
    }

    private Location getLastKnownLocation() {
        Location l = null;
        LocationManager mLocationManager = (LocationManager)
                getApplicationContext().getSystemService(LOCATION_SERVICE);
        assert mLocationManager != null;
        List<String> providers = mLocationManager.getProviders(true);
        Location bestLocation = null;
        for (String provider : providers) {
            if (ContextCompat.checkSelfPermission(this,
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

    @SuppressLint("MissingPermission")
    void initializeMap() {
        if (!GpsEnabled()) {
            initialize();
        } else {
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
//            E/long: 51.7134364        E/lat: 32.7031978
            Log.e("long", String.valueOf(longitude));
            Log.e("lat", String.valueOf(latitude));
            GeoPoint startPoint = new GeoPoint(latitude, longitude);
            mapController.setCenter(startPoint);
            MyLocationNewOverlay locationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(context), mapView);
            locationOverlay.enableMyLocation();
            mapView.getOverlays().add(locationOverlay);
            conversion = new CoordinateConversion();
            initializePlace();
        }
    }

    void initializePlace() {
        placesPoints = new ArrayList<>();
        indexArray = new ArrayList<>();
        isShown = new ArrayList<>();
        MyDatabase dataBase = Room.databaseBuilder(context, MyDatabase.class, MyApplication.getDBNAME())
                .allowMainThreadQueries().build();
        DaoExaminerDuties daoExaminerDuties = dataBase.daoExaminerDuties();
        examinerDuties = daoExaminerDuties.ExaminerDuties();
        examinerDutiesReady = new ArrayList<>();
        if (examinerDuties != null && examinerDuties.size() > 0) {
            setActionBarTitle("در حال جانمایی میسرها...");
            getXY(examinerDuties.get(0).getBillId());
        }
    }

    @SuppressLint("WrongConstant")
    void setActionBarTitle(String title) {
        toolbar.setTitle(title);
        setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle
                (this, drawer, toolbar, R.string.open, R.string.close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        toolbar.setNavigationOnClickListener(view1 -> {
            drawer.openDrawer(Gravity.START);
        });
    }

    void getXY(String billId) {
        Retrofit retrofit = NetworkHelper.getInstance(true, "");
        IAbfaService iAbfaService = retrofit.create(IAbfaService.class);
        GetXY getXY = new GetXY();
        GetXYIncomplete incomplete = new GetXYIncomplete();
        GetError error = new GetError();
        Call<Place> call = iAbfaService.getXY(billId);
        HttpClientWrapper.callHttpAsync(call, ProgressType.NOT_SHOW.getValue(), context,
                getXY, incomplete, error);
    }

    private void addPlace(GeoPoint p) {
        placesPoints.add(p);
        GeoPoint startPoint = new GeoPoint(p.getLatitude(), p.getLongitude());
        Marker startMarker = new Marker(mapView);
        startMarker.setPosition(startPoint);
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);

//        MarkerInfoWindow infoWindow = new MarkerInfoWindow(
//                org.osmdroid.bonuspack.R.layout.bonuspack_bubble, mapView);
        MarkerInfoWindow infoWindow = new MarkerInfoWindow(
                R.layout.custom_info_window, mapView);
        startMarker.setInfoWindow(infoWindow);
        startMarker.setOnMarkerClickListener((marker, mapView) -> {
            //TODO start index from 1
            int overlayIndex = mapView.getOverlayManager().indexOf(startMarker) / 2;
            ExaminerDuties examinerDuties = examinerDutiesReady.get(overlayIndex);
            Log.e("name", examinerDuties.getNameAndFamily());
            InfoWindow.closeAllInfoWindowsOn(mapView);
            infoWindow.close();
            if (isShown.get(overlayIndex)/* && mapView.getOverlayManager().indexOf(startMarker) == wayIndex*/) {
                mapView.getOverlays().remove(roadOverlay);
                if (examinerDuties.isPeymayesh()) {
                    Toast.makeText(context, R.string.is_peymayesh, Toast.LENGTH_LONG).show();
                } else {
                    Intent intent = new Intent(context, FormActivity.class);
                    intent.putExtra(BundleEnum.TRACK_NUMBER.getValue(), examinerDuties.getTrackNumber());
                    intent.putExtra(BundleEnum.SERVICES.getValue(), examinerDuties.getRequestDictionaryString());
                    context.startActivity(intent);
                }
            } else {
                new AddRoutOverlay().execute(new GeoPoint(getLastKnownLocation()), startPoint);
                startMarker.setTitle(examinerDuties.getNameAndFamily().concat("\n")
                        .concat(examinerDuties.getServiceGroup()));
                startMarker.setSubDescription(examinerDuties.getAddress().concat("\n")
                        .concat(examinerDuties.getMobile()));
                startMarker.showInfoWindow();
            }
            for (int i = 0; i < isShown.size(); i++)
                isShown.set(i, false);
            isShown.set(overlayIndex, !isShown.get(overlayIndex));
            wayIndex = mapView.getOverlayManager().indexOf(startMarker);
            Log.e("index", String.valueOf(wayIndex));
            return false;
        });
        mapView.getOverlayManager().add(startMarker);
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
        int placeIndex = mapView.getOverlays().size() - 1;
        Log.e("placeIndex", String.valueOf(placeIndex));
    }

    @SuppressLint("ObsoleteSdkInt")
    public void addRouteOverlay(GeoPoint startPoint, GeoPoint endPoint) {
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        if (roadOverlay != null) {
            mapView.getOverlays().remove(roadOverlay);
        }

        RoadManager roadManager = new OSRMRoadManager(context);
        wayPoints = new ArrayList<>();
        wayPoints.add(startPoint);
        wayPoints.add(endPoint);
        Road road = roadManager.getRoad(wayPoints);
        roadOverlay = RoadManager.buildRoadOverlay(road);
        roadOverlay.setOnClickListener((polyline, mapView, eventPos) -> {
//            mapView.getOverlays().remove(polyline);
//            InfoWindow.closeAllInfoWindowsOn(mapView);
//            isShown = !isShown;
            return false;
        });
        mapView.getOverlays().add(roadOverlay);
        mapView.invalidate();
    }

    void setImageViewClickListener() {
        binding.imageViewDownload.setOnClickListener(onClickListener);
        binding.imageViewUpload.setOnClickListener(onClickListener);
        binding.imageViewPaper.setOnClickListener(onClickListener);
        binding.imageViewExit.setOnClickListener(onClickListener);
        binding.imageViewForm.setOnClickListener(onClickListener);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if (doubleBackToExitPressedOnce) {
                HttpClientWrapper.call.cancel();
                super.onBackPressed();
            }
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, R.string.to_exit_reback, Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
        }
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

    @SuppressLint("StaticFieldLeak")
    class AddRoutOverlay extends AsyncTask<GeoPoint, Integer, Integer> {
        CustomProgressBar progressBar;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar = new CustomProgressBar();
            progressBar.show(context, context.getString(R.string.waiting_for_routing));
        }


        @Override
        protected Integer doInBackground(GeoPoint... geoPoints) {
            addRouteOverlay(geoPoints[0], geoPoints[1]);
            return null;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            progressBar.getDialog().dismiss();
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
        LocationManager locationManager = (LocationManager)
                context.getSystemService(Context.LOCATION_SERVICE);
        boolean enabled =
                LocationManagerCompat.isLocationEnabled(Objects.requireNonNull(locationManager));
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        if (!enabled) {
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

    void download() {
        SharedPreferenceManager sharedPreferenceManager = new SharedPreferenceManager(
                getApplicationContext(), SharedReferenceNames.ACCOUNT.getValue());
        String token = sharedPreferenceManager.getStringData(SharedReferenceKeys.TOKEN.getValue());
        Retrofit retrofit = NetworkHelper.getInstance(true, token);
        final IAbfaService getKardex = retrofit.create(IAbfaService.class);
        Call<Input> call = getKardex.getMyWorks();
        Download download = new Download();
        HttpClientWrapperOld.callHttpAsync(call, download, context, ProgressType.SHOW.getValue());
    }

    void send() {
        MyDatabase dataBase = Room.databaseBuilder(context, MyDatabase.class, MyApplication.getDBNAME())
                .allowMainThreadQueries().build();
        DaoCalculationUserInput daoCalculationUserInput = dataBase.daoCalculationUserInput();
        calculationUserInputList = daoCalculationUserInput.getCalculationUserInput();
        if (calculationUserInputList.size() > 0) {
            ArrayList<CalculationUserInputSend> calculationUserInputSends = new ArrayList<>();
            for (int i = 0; i < calculationUserInputList.size(); i++) {
                CalculationUserInputSend calculationUserInputSend =
                        new CalculationUserInputSend(calculationUserInputList.get(i));
                calculationUserInputSends.add(calculationUserInputSend);
            }
            SharedPreferenceManager sharedPreferenceManager = new SharedPreferenceManager(
                    getApplicationContext(), SharedReferenceNames.ACCOUNT.getValue());
            String token = sharedPreferenceManager.getStringData(SharedReferenceKeys.TOKEN.getValue());
            Retrofit retrofit = NetworkHelper.getInstance(true, token);
            final IAbfaService abfaService = retrofit.create(IAbfaService.class);
            SendCalculation sendCalculation = new SendCalculation();
            Call<SimpleMessage> call = abfaService.setExaminationInfo(calculationUserInputSends);
            HttpClientWrapperOld.callHttpAsync(call, sendCalculation, context, ProgressType.SHOW.getValue());

        } else
            Toast.makeText(getApplicationContext(), R.string.empty_masir, Toast.LENGTH_LONG).show();
    }

    class Download implements ICallback<Input> {
        @Override
        public void execute(Input input) {
            List<ExaminerDuties> examinerDutiesList = input.getExaminerDuties();
            for (int i = 0; i < examinerDutiesList.size(); i++) {
                Gson gson = new Gson();
                examinerDutiesList.get(i).setRequestDictionaryString(
                        gson.toJson(examinerDutiesList.get(i).getRequestDictionary()));
            }
            MyDatabase dataBase = Room.databaseBuilder(context, MyDatabase.class, MyApplication.getDBNAME())
                    .allowMainThreadQueries().build();

            DaoExaminerDuties daoExaminerDuties = dataBase.daoExaminerDuties();
            List<ExaminerDuties> examinerDutiesListTemp = daoExaminerDuties.getExaminerDuties();
            for (int i = 0; i < examinerDutiesList.size(); i++) {
                examinerDutiesList.get(i).setTrackNumber(
                        examinerDutiesList.get(i).getTrackNumber().replace(".0", ""));
                examinerDutiesList.get(i).setRadif(
                        examinerDutiesList.get(i).getRadif().replace(".0", ""));
                ExaminerDuties examinerDuties = examinerDutiesList.get(i);
                for (int j = 0; j < examinerDutiesListTemp.size(); j++) {
                    ExaminerDuties examinerDutiesTemp = examinerDutiesListTemp.get(j);
                    if (examinerDuties.getTrackNumber().equals(examinerDutiesTemp.getTrackNumber())) {
                        examinerDutiesList.remove(i);
                        j = examinerDutiesListTemp.size();
                        i--;
                    }
                }
            }
            daoExaminerDuties.insertAll(examinerDutiesList);

            DaoNoeVagozariDictionary daoNoeVagozariDictionary = dataBase.daoNoeVagozariDictionary();
            daoNoeVagozariDictionary.insertAll(input.getNoeVagozariDictionary());

            DaoQotrEnsheabDictionary daoQotrEnsheabDictionary = dataBase.daoQotrEnsheabDictionary();
            daoQotrEnsheabDictionary.insertAll(input.getQotrEnsheabDictionary());

            DaoServiceDictionary daoServiceDictionary = dataBase.daoServiceDictionary();
            daoServiceDictionary.insertAll(input.getServiceDictionary());

            DaoTaxfifDictionary daoTaxfifDictionary = dataBase.daoTaxfifDictionary();
            daoTaxfifDictionary.insertAll(input.getTaxfifDictionary());

            Log.e("size", String.valueOf(input.getKarbariDictionary().size()));
            DaoKarbariDictionary daoKarbariDictionary = dataBase.daoKarbariDictionary();
            daoKarbariDictionary.insertAll(input.getKarbariDictionary());

            new CustomDialog(DialogType.Green, context, "تعداد ".concat(String.valueOf(
                    input.getExaminerDuties().size())).concat(" مسیر بارگیری شد."),
                    getString(R.string.dear_user), getString(R.string.receive), getString(R.string.accepted));
        }
    }

    class SendCalculation implements ICallback<SimpleMessage> {
        @Override
        public void execute(SimpleMessage simpleMessage) {
            MyDatabase dataBase = Room.databaseBuilder(context, MyDatabase.class, MyApplication.getDBNAME())
                    .allowMainThreadQueries().build();
            DaoCalculationUserInput daoCalculationUserInput = dataBase.daoCalculationUserInput();
            for (CalculationUserInput calculationUserInput : calculationUserInputList) {
                trackNumber = calculationUserInput.trackNumber;
                daoCalculationUserInput.updateCalculationUserInput(true, trackNumber);
            }
        }
    }

    class GetXY implements ICallback<Place> {
        @Override
        public void execute(Place place) {
            if (place.getX() != 0 && place.getY() != 0) {
                String utm = "39 S ".concat(String.valueOf(place.getX())).concat(" ")
                        .concat(String.valueOf(place.getY()));
                double[] latLong = conversion.utm2LatLon(utm);
                GeoPoint geoPointTemp = new GeoPoint(latLong[0], latLong[1]);
                boolean isUnique = true;
                for (GeoPoint geoPoint : placesPoints) {
                    if (geoPoint.equals(geoPointTemp)) {
                        isUnique = false;
                        break;
                    }
                }
                if (isUnique) {
                    isShown.add(false);
                    indexArray.add(counter);
                    examinerDutiesReady.add(examinerDuties.get(counter));
                    addPlace(new GeoPoint(latLong[0], latLong[1]));
                }
            }
            counter = counter + 1;
            if (counter < examinerDuties.size()) {
                if (examinerDuties.get(counter).getBillId() != null
                        && examinerDuties.get(counter).getBillId().length() > 0)
                    getXY(examinerDuties.get(counter).getBillId());
                else getXY(examinerDuties.get(counter).getNeighbourBillId());
            }
            if (counter == examinerDuties.size())
                setActionBarTitle("خانه");
        }
    }

    class GetXYIncomplete implements ICallbackIncomplete<Place> {
        @Override
        public void executeIncomplete(Response<Place> response) {
            counter = counter + 1;
            if (counter < examinerDuties.size()) {
                if (examinerDuties.get(counter).getBillId() != null
                        && examinerDuties.get(counter).getBillId().length() > 0)
                    getXY(examinerDuties.get(counter).getBillId());
                else getXY(examinerDuties.get(counter).getNeighbourBillId());
            }
            if (counter == examinerDuties.size())
                setActionBarTitle("خانه");
            CustomErrorHandlingNew customErrorHandlingNew = new CustomErrorHandlingNew(context);
            String error = customErrorHandlingNew.getErrorMessageDefault(response);
            Log.e("GetXYIncomplete", error);
        }
    }

    class GetError implements ICallbackError {
        @Override
        public void executeError(Throwable t) {
//            counter = counter + 1;
//            if (counter < examinerDuties.size()) {
//                if (examinerDuties.get(counter).getBillId() != null
//                        && examinerDuties.get(counter).getBillId().length() > 0)
//                    getXY(examinerDuties.get(counter).getBillId());
//                else getXY(examinerDuties.get(counter).getNeighbourBillId());
//            }
//            CustomErrorHandlingNew customErrorHandlingNew = new CustomErrorHandlingNew(context);
//            String error = customErrorHandlingNew.getErrorMessageTotal(t);
//            Log.e("GetXYError", error);
        }
    }
}
