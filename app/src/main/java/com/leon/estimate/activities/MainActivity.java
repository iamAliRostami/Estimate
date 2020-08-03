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
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
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
import com.leon.estimate.Enums.DialogType;
import com.leon.estimate.Enums.ProgressType;
import com.leon.estimate.Enums.SharedReferenceKeys;
import com.leon.estimate.Enums.SharedReferenceNames;
import com.leon.estimate.Infrastructure.IAbfaService;
import com.leon.estimate.Infrastructure.ICallback;
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
import com.leon.estimate.Utils.CustomDialog;
import com.leon.estimate.Utils.HttpClientWrapperOld;
import com.leon.estimate.Utils.NetworkHelper;
import com.leon.estimate.Utils.SharedPreferenceManager;
import com.leon.estimate.Utils.SimpleMessage;
import com.leon.estimate.databinding.MainActivityBinding;

import org.jetbrains.annotations.NotNull;
import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.tileprovider.tilesource.TileSourcePolicy;
import org.osmdroid.tileprovider.tilesource.XYTileSource;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
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
import retrofit2.Retrofit;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, LocationListener {
    public double latitude;
    public double longitude;
    LocationManager locationManager;
    MainActivityBinding binding;
    int REQUEST_LOCATION_CODE = 1236;
    String trackNumber;
    DrawerLayout drawer;
    Context context;
    private MapView mapView = null;
    List<CalculationUserInput> calculationUserInputList;
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

    OnlineTileSourceBase CUSTOM = new XYTileSource("MapQuest",
            0, 19, 256, ".png",
            new String[]{"http://172.18.12.242/osm_tiles/"},
            "© OpenStreetMap contributors",
            new TileSourcePolicy(2,
                    TileSourcePolicy.FLAG_NO_BULK
                            | TileSourcePolicy.FLAG_NO_PREVENTIVE
                            | TileSourcePolicy.FLAG_USER_AGENT_MEANINGFUL
                            | TileSourcePolicy.FLAG_USER_AGENT_NORMALIZED
            ));

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
        List<ExaminerDuties> examinerDuties = input.getExaminerDuties();
        for (int i = 0; i < examinerDuties.size(); i++) {
            Gson gson1 = new Gson();
            examinerDuties.get(i).setRequestDictionaryString(
                    gson1.toJson(examinerDuties.get(i).getRequestDictionary()));
        }
        MyDatabase dataBase = Room.databaseBuilder(context, MyDatabase.class, "MyDatabase")
                .allowMainThreadQueries().build();
        DaoExaminerDuties daoExaminerDuties = dataBase.daoExaminerDuties();
        List<ExaminerDuties> examinerDutiesTemp = daoExaminerDuties.getExaminerDuties();
        for (int i = 0; i < examinerDuties.size(); i++) {
            ExaminerDuties examinerDuties1 = examinerDuties.get(i);
            for (int j = 0; j < examinerDutiesTemp.size(); j++) {
                ExaminerDuties examinerDuties2 = examinerDutiesTemp.get(j);
                if (examinerDuties1.getTrackNumber().equals(examinerDuties2.getTrackNumber())) {
                    examinerDuties.remove(i);
                    j = examinerDutiesTemp.size();
                    i--;
                }
            }
        }
        daoExaminerDuties.insertAll(examinerDuties);
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        context = this;
        binding = MainActivityBinding.inflate(getLayoutInflater());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                    checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
            ) {
                askPermission();
            } else {
                Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context));
                setContentView(binding.getRoot());
                initialize();
            }
        }
//        Room.databaseBuilder(context, MyDatabase.class, "MyDatabase")
//                .fallbackToDestructiveMigration()
//                .addMigrations(MyDatabase.MIGRATION_10_11).build();
//        readData();
    }

    @SuppressLint("MissingPermission")
    void initializeMap() {
        if (!GpsEnabled()) {
            initialize();
        } else {
            mapView = findViewById(R.id.mapView);
//            mapView.setTileSource(CUSTOM);
//            mapView.setTileSource(new OnlineTileSourceBase("USGS Topo", 0, 18, 256, "",
//                    new String[] { "http://basemap.nationalmap.gov/ArcGIS/rest/services/USGSTopo/MapServer/tile/" }) {
//                @Override
//                public String getTileURLString(long pMapTileIndex) {
//                    return getBaseUrl()
//                            + MapTileIndex.getZoom(pMapTileIndex)
//                            + "/" + MapTileIndex.getY(pMapTileIndex)
//                            + "/" + MapTileIndex.getX(pMapTileIndex)
//                            + mImageFilenameEnding;
//                }
//            });
            mapView.setTileSource(TileSourceFactory.MAPNIK);

//            mapView.setTileSource(new XYTileSource("MAPNIK",
//                    0, 18, 256, ".jpg", new String[]{
//                    "http://a.tile.openstreetmap.org/",
//                    "http://b.tile.openstreetmap.org/",
//                    "http://c.tile.openstreetmap.org/"}));
//            mapView.setBuiltInZoomControls(true);
//            mapView.setMultiTouchControls(true);
//            mapView.setUseDataConnection(false); //optional, but a good way to prevent loading from the network and test your zip loading.


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
//            test();
//            initRouteCoordinates();
            mapView.getOverlays().add(locationOverlay);
        }
    }

    void setImageViewFindByViewId() {
        binding.imageViewDownload.setOnClickListener(onClickListener);
        binding.imageViewUpload.setOnClickListener(onClickListener);
        binding.imageViewPaper.setOnClickListener(onClickListener);
        binding.imageViewExit.setOnClickListener(onClickListener);
        binding.imageViewForm.setOnClickListener(onClickListener);
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
        SharedPreferenceManager sharedPreferenceManager = new SharedPreferenceManager(getApplicationContext(), SharedReferenceNames.ACCOUNT.getValue());
        String token = sharedPreferenceManager.getStringData(SharedReferenceKeys.TOKEN.getValue());
//        Retrofit retrofit = NetworkHelper.getInstance(false, "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJqdGkiOiI4MGI0YjJjNi0zYzQ0LTRlNDMtYWQwMi05ODlhNmFiNTIwNTIiLCJpc3MiOiJodHRwOi8vYXV0aHNlcnZlci8iLCJpYXQiOjE1ODIzNzE1MDEsImh0dHA6Ly9zY2hlbWFzLnhtbHNvYXAub3JnL3dzLzIwMDUvMDUvaWRlbnRpdHkvY2xhaW1zL25hbWVpZGVudGlmaWVyIjoiMmRiNDE3YWYtNmU5My00YmU5LTgyOGEtMDE4ZDE0NjkwZWNmIiwiaHR0cDovL3NjaGVtYXMueG1sc29hcC5vcmcvd3MvMjAwNS8wNS9pZGVudGl0eS9jbGFpbXMvbmFtZSI6ImFwcEV4YW0iLCJkaXNwbGF5TmFtZSI6Itin2b7ZhNuM2qnbjNi02YYg2KfYsdiy24zYp9io24wg2KrYs9iqIiwidXNlcklkIjoiMmRiNDE3YWYtNmU5My00YmU5LTgyOGEtMDE4ZDE0NjkwZWNmIiwidXNlckNvZGUiOiI2NCIsImh0dHA6Ly9zY2hlbWFzLm1pY3Jvc29mdC5jb20vd3MvMjAwOC8wNi9pZGVudGl0eS9jbGFpbXMvc2VyaWFsbnVtYmVyIjoiZDY4NmFmOWY4YzVjNDUzYjk0ZTIwMWIxY2Q0YTRkM2YiLCJodHRwOi8vc2NoZW1hcy5taWNyb3NvZnQuY29tL3dzLzIwMDgvMDYvaWRlbnRpdHkvY2xhaW1zL3VzZXJkYXRhIjoiMmRiNDE3YWYtNmU5My00YmU5LTgyOGEtMDE4ZDE0NjkwZWNmIiwiem9uZUlkIjoiMTMxMzAzIiwiYWN0aW9uIjpbIlByb2ZpbGUuSW5kZXgiLCJFeGFtaW5hdGlvbk1hbmFnZXIuR2V0TXlXb3JrcyJdLCJodHRwOi8vc2NoZW1hcy5taWNyb3NvZnQuY29tL3dzLzIwMDgvMDYvaWRlbnRpdHkvY2xhaW1zL3JvbGUiOiJFeGFtaW5lciIsInJvbGVJZCI6IjQiLCJuYmYiOjE1ODIzNzE1MDEsImV4cCI6MTU4MjQxODMwMSwiYXVkIjoiNDE0ZTE5MjdhMzg4NGY2OGFiYzc5ZjcyODM4MzdmZDEifQ.iCLVExnN_UCqEgMvzGWB1Lw3UI4T-5ey3Z8aNQj_I1Y");
        Retrofit retrofit = NetworkHelper.getInstance(false, token);
        final IAbfaService getKardex = retrofit.create(IAbfaService.class);
        Call<Input> call = getKardex.getMyWorks();
        Download download = new Download();
        HttpClientWrapperOld.callHttpAsync(call, download, context, ProgressType.SHOW.getValue());
    }

    void send() {
        MyDatabase dataBase = Room.databaseBuilder(context, MyDatabase.class, "MyDatabase")
                .allowMainThreadQueries().build();
        DaoCalculationUserInput daoCalculationUserInput = dataBase.daoCalculationUserInput();
        calculationUserInputList = daoCalculationUserInput.getCalculationUserInput();
        if (calculationUserInputList.size() > 0) {
            ArrayList<CalculationUserInputSend> calculationUserInputSends = new ArrayList<>();
            for (int i = 0; i < calculationUserInputList.size(); i++) {
                CalculationUserInputSend calculationUserInputSend = new CalculationUserInputSend(calculationUserInputList.get(i));
                calculationUserInputSends.add(calculationUserInputSend);
            }
            SharedPreferenceManager sharedPreferenceManager = new SharedPreferenceManager(getApplicationContext(), SharedReferenceNames.ACCOUNT.getValue());
            String token = sharedPreferenceManager.getStringData(SharedReferenceKeys.TOKEN.getValue());
            Retrofit retrofit = NetworkHelper.getInstance(false, token);
            final IAbfaService abfaService = retrofit.create(IAbfaService.class);
            SendCalculation sendCalculation = new SendCalculation();
            Call<SimpleMessage> call = abfaService.setExaminationInfo(calculationUserInputSends);
            HttpClientWrapperOld.callHttpAsync(call, sendCalculation, context, ProgressType.SHOW.getValue());

        } else
            Toast.makeText(getApplicationContext(), "مسیری برای تخلیه وجود ندارد", Toast.LENGTH_LONG).show();
    }

    class Download implements ICallback<Input> {
        @Override
        public void execute(Input input) {
            List<ExaminerDuties> examinerDuties = input.getExaminerDuties();
            for (int i = 0; i < examinerDuties.size(); i++) {
                Gson gson = new Gson();
                examinerDuties.get(i).setRequestDictionaryString(
                        gson.toJson(examinerDuties.get(i).getRequestDictionary()));
            }
            MyDatabase dataBase = Room.databaseBuilder(context, MyDatabase.class, "MyDatabase")
                    .allowMainThreadQueries().build();

            DaoExaminerDuties daoExaminerDuties = dataBase.daoExaminerDuties();
            List<ExaminerDuties> examinerDutiesTemp = daoExaminerDuties.getExaminerDuties();
            for (int i = 0; i < examinerDuties.size(); i++) {
                ExaminerDuties examinerDuties1 = examinerDuties.get(i);
                for (int j = 0; j < examinerDutiesTemp.size(); j++) {
                    ExaminerDuties examinerDuties2 = examinerDutiesTemp.get(j);
                    if (examinerDuties1.getTrackNumber().equals(examinerDuties2.getTrackNumber())) {
                        examinerDuties.remove(i);
                        j = examinerDutiesTemp.size();
                        i--;
                    }
                }
            }
            daoExaminerDuties.insertAll(examinerDuties);

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

            new CustomDialog(DialogType.Green, context, "تعداد ".concat(String.valueOf(input.getExaminerDuties().size())).concat(" مسیر بارگیری شد."),
                    getString(R.string.dear_user), getString(R.string.receive), getString(R.string.accepted));
        }
    }

    class SendCalculation implements ICallback<SimpleMessage> {
        @Override
        public void execute(SimpleMessage simpleMessage) {
            MyDatabase dataBase = Room.databaseBuilder(context, MyDatabase.class, "MyDatabase")
                    .allowMainThreadQueries().build();
            DaoCalculationUserInput daoCalculationUserInput = dataBase.daoCalculationUserInput();
            for (CalculationUserInput calculationUserInput : calculationUserInputList) {
                trackNumber = calculationUserInput.trackNumber;
                daoCalculationUserInput.updateCalculationUserInput(true, trackNumber);
            }
        }
    }
}
