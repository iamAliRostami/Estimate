package com.leon.estimate.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Debug;
import android.os.Environment;
import android.os.Handler;
import android.os.StrictMode;
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
import androidx.core.location.LocationManagerCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.room.Room;

import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
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
import com.leon.estimate.Tables.DaoImages;
import com.leon.estimate.Tables.DaoKarbariDictionary;
import com.leon.estimate.Tables.DaoNoeVagozariDictionary;
import com.leon.estimate.Tables.DaoQotrEnsheabDictionary;
import com.leon.estimate.Tables.DaoResultDictionary;
import com.leon.estimate.Tables.DaoServiceDictionary;
import com.leon.estimate.Tables.DaoTaxfifDictionary;
import com.leon.estimate.Tables.ExaminerDuties;
import com.leon.estimate.Tables.Images;
import com.leon.estimate.Tables.Input;
import com.leon.estimate.Tables.Login;
import com.leon.estimate.Tables.MyDatabase;
import com.leon.estimate.Tables.Place;
import com.leon.estimate.Tables.UploadImage;
import com.leon.estimate.Utils.CoordinateConversion;
import com.leon.estimate.Utils.CustomDialog;
import com.leon.estimate.Utils.CustomErrorHandlingNew;
import com.leon.estimate.Utils.CustomFile;
import com.leon.estimate.Utils.CustomProgressBar;
import com.leon.estimate.Utils.GPSTracker;
import com.leon.estimate.Utils.HttpClientWrapper;
import com.leon.estimate.Utils.NetworkHelper;
import com.leon.estimate.Utils.SharedPreferenceManager;
import com.leon.estimate.Utils.SimpleMessage;
import com.leon.estimate.databinding.MainActivityBinding;

import org.jetbrains.annotations.NotNull;
import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.infowindow.InfoWindow;
import org.osmdroid.views.overlay.infowindow.MarkerInfoWindow;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.leon.estimate.Utils.Constants.REQUEST_LOCATION_CODE;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    MainActivityBinding binding;
    String trackNumber;
    DrawerLayout drawer;
    Polyline roadOverlay;
    MapView mapView;
    Context context;
    Activity activity;
    CoordinateConversion conversion;
    GPSTracker gpsTracker;
    int wayIndex = 0, counter = 0, imageId, imageCounter = 0;
    double latitude, longitude;
    boolean doubleBackToExitPressedOnce = false;
    Toolbar toolbar;
    MyDatabase dataBase;
    SharedPreferenceManager sharedPreferenceManager;
    ArrayList<Integer> indexArray;
    ArrayList<Boolean> isShown;
    ArrayList<GeoPoint> wayPoints, placesPoints;
    List<Images> images;
    List<ExaminerDuties> examinerDuties, examinerDutiesReady;
    List<CalculationUserInput> calculationUserInputList;
    @SuppressLint("NonConstantResourceId")
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
            case R.id.imageViewRequest:
                intent = new Intent(getApplicationContext(), MotherChildActivity.class);
                startActivity(intent);
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
        activity = this;
        binding = MainActivityBinding.inflate(getLayoutInflater());
        checkPermission();
        Room.databaseBuilder(context, MyDatabase.class,
                MyApplication.getDBNAME()).fallbackToDestructiveMigration()
                .addMigrations(MyDatabase.MIGRATION_41_42).build();
    }

    void checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED ||
                    checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED
            ) {
                askPermission();
            } else {
                setContentView(binding.getRoot());
                initialize();
            }
        }
    }

    void initialize() {
        toolbar = findViewById(R.id.toolbar);
        drawer = findViewById(R.id.drawer_layout);
        setActionBarTitle(getString(R.string.home));
        drawer.openDrawer(GravityCompat.START);
        sharedPreferenceManager = new SharedPreferenceManager(context,
                SharedReferenceNames.ACCOUNT.getValue());
        setImageViewClickListener();
        dataBase = Room.databaseBuilder(context, MyDatabase.class, MyApplication.getDBNAME())
                .allowMainThreadQueries().build();
        initializeMap();
    }

    void setImageViewClickListener() {
        binding.imageViewDownload.setOnClickListener(onClickListener);
        binding.imageViewUpload.setOnClickListener(onClickListener);
        binding.imageViewPaper.setOnClickListener(onClickListener);
        binding.imageViewExit.setOnClickListener(onClickListener);
        binding.imageViewForm.setOnClickListener(onClickListener);
        binding.imageViewRequest.setOnClickListener(onClickListener);
    }

    void initializeMap() {
        if (!GpsEnabled()) {
            initialize();
        } else {
            mapView = findViewById(R.id.mapView);

            mapView.setBuiltInZoomControls(true);
            mapView.getZoomController().setVisibility(
                    CustomZoomButtonsController.Visibility.SHOW_AND_FADEOUT);
            mapView.setMultiTouchControls(true);
            IMapController mapController = mapView.getController();
            mapController.setZoom(19.5);
            gpsTracker = new GPSTracker(activity);
            latitude = gpsTracker.getLatitude();
            longitude = gpsTracker.getLongitude();
            GeoPoint startPoint = new GeoPoint(latitude, longitude);
            mapController.setCenter(startPoint);
            MyLocationNewOverlay locationOverlay =
                    new MyLocationNewOverlay(new GpsMyLocationProvider(context), mapView);
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
        DaoExaminerDuties daoExaminerDuties = dataBase.daoExaminerDuties();
        examinerDuties = daoExaminerDuties.ExaminerDuties();
        examinerDutiesReady = new ArrayList<>();
        if (examinerDuties != null && examinerDuties.size() > 0) {
            setActionBarTitle(getString(R.string.locating));
            if (examinerDuties.get(0).getBillId() != null)
                getXY(examinerDuties.get(0).getBillId());
            else getXY(examinerDuties.get(0).getNeighbourBillId());
        }
    }

    void getXY(String billId) {
        Retrofit retrofit = NetworkHelper.getInstance("");
        IAbfaService iAbfaService = retrofit.create(IAbfaService.class);
        Call<Place> call = iAbfaService.getXY(billId);
        HttpClientWrapper.callHttpAsync(call, ProgressType.NOT_SHOW.getValue(), context,
                new GetXY(), new GetXYIncomplete(), new GetError());
    }

    private void addPlace(GeoPoint p) {
        placesPoints.add(p);
        GeoPoint startPoint = new GeoPoint(p.getLatitude(), p.getLongitude());
        Marker startMarker = new Marker(mapView);
        startMarker.setPosition(startPoint);
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        MarkerInfoWindow infoWindow = new MarkerInfoWindow(
                R.layout.custom_info_window, mapView);
        startMarker.setInfoWindow(infoWindow);
        startMarker.setOnMarkerClickListener((marker, mapView) -> {
            int overlayIndex = mapView.getOverlayManager().indexOf(startMarker) / 2;
            ExaminerDuties examinerDuties = examinerDutiesReady.get(overlayIndex);
            InfoWindow.closeAllInfoWindowsOn(mapView);
            infoWindow.close();
            if (isShown.get(overlayIndex)) {
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
                gpsTracker.getLocation();
                new AddRoutOverlay().execute(new GeoPoint(gpsTracker.getLatitude(), gpsTracker.getLongitude()), startPoint);
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
            return false;
        });
        mapView.getOverlayManager().add(startMarker);
        mapView.getOverlays().add(new MapEventsOverlay(new MapEventsReceiver() {
            @Override
            public boolean singleTapConfirmedHelper(GeoPoint p) {
                return false;
            }

            @Override
            public boolean longPressHelper(GeoPoint p) {
                return false;
            }
        }));
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
        mapView.getOverlays().add(roadOverlay);
        mapView.invalidate();
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
        Retrofit retrofit = NetworkHelper.getInstance(token);
        final IAbfaService getKardex = retrofit.create(IAbfaService.class);
        Call<Input> call = getKardex.getMyWorks();
        HttpClientWrapper.callHttpAsync(call, ProgressType.SHOW.getValue(), context,
                new Download(), new DownloadIncomplete(), new GetError());
    }

    void send() {
        DaoCalculationUserInput daoCalculationUserInput = dataBase.daoCalculationUserInput();
        calculationUserInputList = daoCalculationUserInput.getCalculationUserInput();
        DaoExaminerDuties daoExaminerDuties = dataBase.daoExaminerDuties();
        if (calculationUserInputList.size() > 0) {
            ArrayList<CalculationUserInputSend> calculationUserInputSends = new ArrayList<>();
            for (int i = 0; i < calculationUserInputList.size(); i++) {
                ExaminerDuties examinerDuties = daoExaminerDuties.examinerDutiesByTrackNumber(
                        calculationUserInputList.get(i).trackNumber);
                CalculationUserInputSend calculationUserInputSend =
                        new CalculationUserInputSend(calculationUserInputList.get(i), examinerDuties);
                calculationUserInputSends.add(calculationUserInputSend);
            }
            SharedPreferenceManager sharedPreferenceManager = new SharedPreferenceManager(
                    getApplicationContext(), SharedReferenceNames.ACCOUNT.getValue());
            String token = sharedPreferenceManager.getStringData(SharedReferenceKeys.TOKEN.getValue());
            Retrofit retrofit = NetworkHelper.getInstance(token);
            final IAbfaService abfaService = retrofit.create(IAbfaService.class);
            Call<SimpleMessage> call = abfaService.setExaminationInfo(calculationUserInputSends);
            HttpClientWrapper.callHttpAsync(call, ProgressType.SHOW.getValue(), context, new SendCalculation(),
                    new SendCalculationIncomplete(), new GetError());
        } else
            Toast.makeText(getApplicationContext(), R.string.empty_masir, Toast.LENGTH_LONG).show();
        DaoImages daoImages = dataBase.daoImages();
        images = daoImages.getImages();
        if (images.size() > 0) {
            attemptLogin();
        }
    }

    void attemptLogin() {
        Retrofit retrofit = NetworkHelper.getInstance("");
        final IAbfaService abfaService = retrofit.create(IAbfaService.class);
        Call<Login> call = abfaService.login2(sharedPreferenceManager.getStringData(
                SharedReferenceKeys.USERNAME_TEMP.getValue()),
                sharedPreferenceManager.getStringData(SharedReferenceKeys.PASSWORD_TEMP.getValue()));
        HttpClientWrapper.callHttpAsync(call, ProgressType.SHOW.getValue(),
                this, new LoginDocument(), new LoginDocumentIncomplete(), new GetError());
    }

    void uploadImage(Images images) {
        Retrofit retrofit = NetworkHelper.getInstance("");
        final IAbfaService getImage = retrofit.create(IAbfaService.class);
        images = loadImage(images);
        if (images != null) {
            MultipartBody.Part body = CustomFile.bitmapToFile(images.getBitmap(), context,
                    images.getAddress());
            Call<UploadImage> call;
            if (images.getTrackingNumber().length() > 0)
                call = getImage.uploadDocNew(sharedPreferenceManager.getStringData(
                        SharedReferenceKeys.TOKEN_FOR_FILE.getValue()), body,
                        Integer.parseInt(images.getDocId()), images.getTrackingNumber());
            else
                call = getImage.uploadDoc(sharedPreferenceManager.getStringData(
                        SharedReferenceKeys.TOKEN_FOR_FILE.getValue()), body,
                        Integer.parseInt(images.getDocId()), images.getBillId());
            imageId = images.getImageId();
            HttpClientWrapper.callHttpAsync(call, ProgressType.SHOW.getValue(), this,
                    new UploadImageDoc(), new UploadImageDocIncomplete(), new GetError());
        }
    }

    Images loadImage(Images images) {
        try {
            File f = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES), context.getString(R.string.camera_folder));
            f = new File(f, images.getAddress());
            Bitmap b = BitmapFactory.decodeStream(new FileInputStream(f));
            images.setBitmap(b);
            return images;
        } catch (
                FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    class LoginDocument implements ICallback<Login> {
        @Override
        public void execute(Login loginFeedBack) {
            if (loginFeedBack.isSuccess()) {
                sharedPreferenceManager.putData(SharedReferenceKeys.TOKEN_FOR_FILE.getValue(),
                        loginFeedBack.getData().getToken());
                uploadImage(images.get(0));
            }
        }
    }

    static class LoginDocumentIncomplete implements ICallbackIncomplete<Login> {

        @Override
        public void executeIncomplete(Response<Login> response) {
            Log.e("Login incomplete", response.toString());
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
        toolbar.setNavigationOnClickListener(view1 -> drawer.openDrawer(Gravity.START));
    }

    static class UploadImageDocIncomplete implements ICallbackIncomplete<UploadImage> {
        @Override
        public void executeIncomplete(Response<UploadImage> response) {
            Log.e("UploadImageDoc error", response.toString());
        }
    }

    static class SendCalculationIncomplete implements ICallbackIncomplete<SimpleMessage> {
        @Override
        public void executeIncomplete(Response<SimpleMessage> response) {
            Log.e("SendCalculation Error", response.toString());
        }
    }

    class SendCalculation implements ICallback<SimpleMessage> {
        @Override
        public void execute(SimpleMessage simpleMessage) {
            DaoCalculationUserInput daoCalculationUserInput = dataBase.daoCalculationUserInput();
            for (CalculationUserInput calculationUserInput : calculationUserInputList) {
                trackNumber = calculationUserInput.trackNumber;
                daoCalculationUserInput.updateCalculationUserInput(true, trackNumber);
            }
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
                setActionBarTitle(getString(R.string.home));
            CustomErrorHandlingNew customErrorHandlingNew = new CustomErrorHandlingNew(context);
            String error = customErrorHandlingNew.getErrorMessageDefault(response);
            Log.e("GetXYIncomplete", error);
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
                if (isUnique && counter < examinerDuties.size()) {
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
                setActionBarTitle(getString(R.string.home));
        }
    }

    class GetError implements ICallbackError {
        @Override
        public void executeError(Throwable t) {
            Log.e("Error", Objects.requireNonNull(t.getMessage()));
            setActionBarTitle(getString(R.string.home));
        }
    }


    class UploadImageDoc implements ICallback<UploadImage> {
        @Override
        public void execute(UploadImage responseBody) {
            if (responseBody.isSuccess()) {
                DaoImages daoImages = dataBase.daoImages();
                daoImages.deleteByID(imageId);
                imageCounter = imageCounter + 1;
                if (imageCounter < images.size()) {
                    uploadImage(images.get(imageCounter));
                }
            } else
                new CustomDialog(DialogType.Yellow, MainActivity.this,
                        MainActivity.this.getString(R.string.error_upload).concat("\n")
                                .concat(responseBody.getError()),
                        MainActivity.this.getString(R.string.dear_user),
                        MainActivity.this.getString(R.string.upload_image),
                        MainActivity.this.getString(R.string.accepted));
        }
    }

    List<ExaminerDuties> prepareExaminerDuties(List<ExaminerDuties> examinerDutiesList) {
        for (int i = 0; i < examinerDutiesList.size(); i++) {
            Gson gson = new Gson();
            examinerDutiesList.get(i).setRequestDictionaryString(
                    gson.toJson(examinerDutiesList.get(i).getRequestDictionary()));
            if (examinerDutiesList.get(i).getZoneId() == null ||
                    examinerDutiesList.get(i).getZoneId().equals("0")) {
                examinerDutiesList.remove(i);
                i--;
            }
        }
        return examinerDutiesList;
    }

    int removeExaminerDuties(List<ExaminerDuties> examinerDutiesList) {
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
                if (examinerDuties.getTrackNumber().equals(examinerDutiesTemp.getTrackNumber())
                        || examinerDuties.getZoneId() == null
                        || examinerDuties.getZoneId().equals("0")) {
                    examinerDutiesList.remove(i);
                    j = examinerDutiesListTemp.size();
                    i--;
                }
            }
        }
        daoExaminerDuties.insertAll(examinerDutiesList);
        return examinerDutiesList.size();
    }

    class Download implements ICallback<Input> {
        @Override
        public void execute(Input input) {
            if (input != null) {
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
                DaoResultDictionary daoResultDictionary = dataBase.daoResultDictionary();
                daoResultDictionary.insertAll(input.getResultDictionary());
                new CustomDialog(DialogType.Green, context, "تعداد ".concat(String.valueOf(
                        removeExaminerDuties(prepareExaminerDuties(input.getExaminerDuties()))))
                        .concat(" مسیر بارگیری شد."),
                        getString(R.string.dear_user), getString(R.string.receive), getString(R.string.accepted));
            } else {
                Toast.makeText(getApplicationContext(), R.string.empty_download, Toast.LENGTH_LONG).show();
            }
        }
    }

    class DownloadIncomplete implements ICallbackIncomplete<Input> {
        @Override
        public void executeIncomplete(Response<Input> response) {
            CustomErrorHandlingNew customErrorHandlingNew = new CustomErrorHandlingNew(context);
            String error = customErrorHandlingNew.getErrorMessageDefault(response);
            new CustomDialog(DialogType.Yellow, context, error,
                    getString(R.string.dear_user),
                    getString(R.string.download),
                    getString(R.string.accepted));
            Log.e("Download Incomplete", response.toString());
        }
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
    protected void onResume() {
        super.onResume();
        binding.imageViewDownload.setImageResource(R.drawable.image_download);
        binding.imageViewExit.setImageResource(R.drawable.image_exit);
        binding.imageViewUpload.setImageResource(R.drawable.image_upload);
        binding.imageViewPaper.setImageResource(R.drawable.image_paper);
        binding.imageViewRequest.setImageResource(R.drawable.image_request);
        binding.imageViewForm.setImageResource(R.drawable.image_form);
        mapView.onResume();
        if (counter < examinerDuties.size()) {
            setActionBarTitle("در حال جانمایی میسرها...");
            if (examinerDuties.get(counter).getBillId() != null
                    && examinerDuties.get(counter).getBillId().length() > 0)
                getXY(examinerDuties.get(counter).getBillId());
            else getXY(examinerDuties.get(counter).getNeighbourBillId());
        }
        if (counter == examinerDuties.size())
            setActionBarTitle(getString(R.string.home));
    }


    @Override
    protected void onStop() {
        super.onStop();
        binding.imageViewDownload.setImageDrawable(null);
        binding.imageViewUpload.setImageDrawable(null);
        binding.imageViewExit.setImageDrawable(null);
        binding.imageViewForm.setImageDrawable(null);
        binding.imageViewPaper.setImageDrawable(null);
        binding.imageViewRequest.setImageDrawable(null);
        HttpClientWrapper.call.cancel();
        Runtime.getRuntime().totalMemory();
        Runtime.getRuntime().freeMemory();
        Runtime.getRuntime().maxMemory();
        Debug.getNativeHeapAllocatedSize();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
        HttpClientWrapper.call.cancel();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        HttpClientWrapper.call.cancel();
        Runtime.getRuntime().totalMemory();
        Runtime.getRuntime().freeMemory();
        Runtime.getRuntime().maxMemory();
        Debug.getNativeHeapAllocatedSize();
    }
}
