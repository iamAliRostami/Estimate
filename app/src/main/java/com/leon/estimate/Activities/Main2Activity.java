package com.leon.estimate.Activities;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.room.Room;

import com.google.android.material.navigation.NavigationView;
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
import com.mapbox.android.core.permissions.PermissionsListener;
import com.mapbox.android.core.permissions.PermissionsManager;
import com.mapbox.android.gestures.MoveGestureDetector;
import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.Mapbox;
import com.mapbox.mapboxsdk.location.LocationComponent;
import com.mapbox.mapboxsdk.location.LocationComponentActivationOptions;
import com.mapbox.mapboxsdk.location.modes.CameraMode;
import com.mapbox.mapboxsdk.location.modes.RenderMode;
import com.mapbox.mapboxsdk.maps.MapView;
import com.mapbox.mapboxsdk.maps.MapboxMap;
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback;
import com.mapbox.mapboxsdk.maps.Style;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;

public class Main2Activity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, OnMapReadyCallback, PermissionsListener, MapboxMap.OnMoveListener {

    ImageView imageViewExit, imageViewDownload, imageViewUpload, imageViewPaper, imageViewForm;
    String accessToken = "pk.eyJ1IjoiaWFtYWxpcm9zdGFtaSIsImEiOiJjanhjbmptcmowMjZnM3BvdnY0YWx4ampxIn0.iv9I6s34q_-k9GqCiz2seg";
    String trackNumber;
    private PermissionsManager permissionsManager;
    private MapboxMap mapboxMap;
    private MapView mapView;
    private List<Point> routeCoordinates;
    DrawerLayout drawer;
    Context context;
    View.OnClickListener onClickListener = view -> {
        Intent intent = null;
        switch (view.getId()) {
            case R.id.imageViewForm:
                intent = new Intent(getApplicationContext(), ListActivity.class);
                startActivity(intent);
                break;
            case R.id.imageViewDownload:
                download();
//                intent = new Intent(getApplicationContext(), DownloadActivity.class);
//                startActivity(intent);
                break;
            case R.id.imageViewUpload:
                send();
//                intent = new Intent(getApplicationContext(), UploadActivity.class);
//                startActivity(intent);
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);

        Mapbox.getInstance(this, accessToken);
        setContentView(R.layout.main2_activity);
        mapView = findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        context = this;
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        drawer = findViewById(R.id.drawer_layout);
        drawer.openDrawer(GravityCompat.START);
        setImageViewFindByViewId();
//        Room.databaseBuilder(context.getApplicationContext(), MyDatabase.class, "MyDatabase")
//                .fallbackToDestructiveMigration()
//                .addMigrations(MyDatabase.MIGRATION_10_11).build();
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

    @Override
    public void onMapReady(@NonNull final MapboxMap mapboxMap) {
        Main2Activity.this.mapboxMap = mapboxMap;
        mapboxMap.setStyle(new Style.Builder().fromUrl("mapbox://styles/mapbox/cjerxnqt3cgvp2rmyuxbeqme7"),
                style -> {
                    enableLocationComponent(style);
//                    for (int i = 1; i < 4; i++) {
//                        initRouteCoordinates(i);
//                        // Create the LineString from the list of coordinates and then make a GeoJSON
//                        // FeatureCollection so we can add the line to our map as a layer.
//                        style.addSource(new GeoJsonSource("line-source".concat(String.valueOf(i)),
//                                FeatureCollection.fromFeatures(new Feature[]{Feature.fromGeometry(
//                                        LineString.fromLngLats(routeCoordinates)
//                                )})));
//                        // The layer properties for our line. This is where we make the line dotted, set the color, etc.
//                        style.addLayer(new LineLayer("linelayer".concat(String.valueOf(i))
//                                , "line-source".concat(String.valueOf(i))).withProperties(
//                                PropertyFactory.lineDasharray(new Float[]{0.01f, 2f}),
//                                PropertyFactory.lineCap(Property.LINE_CAP_ROUND),
//                                PropertyFactory.lineJoin(Property.LINE_JOIN_ROUND),
//                                PropertyFactory.lineWidth(5f),
//                                PropertyFactory.lineColor(Color.parseColor("#e55e5e"))
//                        ));
//                    }
                });
    }

    @SuppressWarnings({"MissingPermission"})
    private void enableLocationComponent(@NonNull Style loadedMapStyle) {
        // Check if permissions are enabled and if not request
        if (PermissionsManager.areLocationPermissionsGranted(this)) {
            // Get an instance of the component
            LocationComponent locationComponent = mapboxMap.getLocationComponent();
            // Activate with options
            locationComponent.activateLocationComponent(
                    LocationComponentActivationOptions.builder(this, loadedMapStyle).build());
            // Enable to make component visible
            locationComponent.setLocationComponentEnabled(true);
            // Set the component's camera mode
            locationComponent.setCameraMode(CameraMode.TRACKING);
            // Set the component's render mode
            locationComponent.setRenderMode(RenderMode.COMPASS);
        } else {
            permissionsManager = new PermissionsManager(this);
            permissionsManager.requestLocationPermissions(this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        permissionsManager.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onExplanationNeeded(List<String> permissionsToExplain) {
        Toast.makeText(this, R.string.user_location_permission_explanation, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onPermissionResult(boolean granted) {
        if (granted) {
            mapboxMap.getStyle(new Style.OnStyleLoaded() {
                @Override
                public void onStyleLoaded(@NonNull Style style) {
                    enableLocationComponent(style);
                }
            });
        } else {
            Toast.makeText(this, R.string.user_location_permission_not_granted, Toast.LENGTH_LONG).show();
            finish();
        }
    }

    @Override
    @SuppressWarnings({"MissingPermission"})
    public void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapView.onStop();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
    }


    @Override
    public void onMoveEnd(@NotNull MoveGestureDetector detector) {
    }

    @Override
    public void onMoveBegin(@NotNull MoveGestureDetector detector) {
// Left empty on purpose
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mapView.onSaveInstanceState(outState);
    }

    private void initRouteCoordinates(int i) {
// Create a list to store our line coordinates.
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

    @Override
    public void onMove(@NotNull MoveGestureDetector detector) {
// Left empty on purpose
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

    void send() {
        MyDatabase dataBase = Room.databaseBuilder(context, MyDatabase.class, "MyDatabase")
                .allowMainThreadQueries().build();
        DaoCalculationUserInput daoCalculationUserInput = dataBase.daoCalculationUserInput();
        List<CalculationUserInput> calculationUserInputList = daoCalculationUserInput.getCalculationUserInput();
        Log.e("size", String.valueOf(calculationUserInputList.size()));
    }

    void download() {
        Retrofit retrofit = NetworkHelper.getInstance(true, "header");
        final IAbfaService getKardex = retrofit.create(IAbfaService.class);
        Call<List<Calculation>> call = getKardex.getMyWorks();
        Download download = new Download();
        HttpClientWrapper.callHttpAsync(call, download, context, ProgressType.SHOW.getValue());
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
