package com.leon.estimate.activities;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.room.Room;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.leon.estimate.Enums.BundleEnum;
import com.leon.estimate.Enums.ProgressType;
import com.leon.estimate.Infrastructure.IAbfaService;
import com.leon.estimate.Infrastructure.ICallback;
import com.leon.estimate.Infrastructure.ICallbackError;
import com.leon.estimate.Infrastructure.ICallbackIncomplete;
import com.leon.estimate.MyApplication;
import com.leon.estimate.R;
import com.leon.estimate.Tables.CalculationUserInput;
import com.leon.estimate.Tables.DaoCalculationUserInput;
import com.leon.estimate.Tables.DaoExaminerDuties;
import com.leon.estimate.Tables.ExaminerDuties;
import com.leon.estimate.Tables.GISInfo;
import com.leon.estimate.Tables.GISToken;
import com.leon.estimate.Tables.MyDatabase;
import com.leon.estimate.Tables.Place;
import com.leon.estimate.Tables.RequestDictionary;
import com.leon.estimate.Tables.SecondForm;
import com.leon.estimate.Utils.CoordinateConversion;
import com.leon.estimate.Utils.GIS.ConvertArcToGeo;
import com.leon.estimate.Utils.GIS.CustomArcGISJSON;
import com.leon.estimate.Utils.GIS.CustomGeoJSON;
import com.leon.estimate.Utils.GIS.MyKmlStyle;
import com.leon.estimate.Utils.HttpClientWrapper;
import com.leon.estimate.Utils.NetworkHelper;
import com.leon.estimate.databinding.FormActivityBinding;
import com.leon.estimate.fragments.FormFragment;
import com.leon.estimate.fragments.PersonalFragment;
import com.leon.estimate.fragments.SecondFormFragment;
import com.leon.estimate.fragments.ServicesFragment;

import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.kml.KmlDocument;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.overlay.FolderOverlay;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

public class FormActivity extends AppCompatActivity implements LocationListener {
    public static String karbari, noeVagozari, shenasname;
    public static List<RequestDictionary> requestDictionaries;
    public static ExaminerDuties examinerDuties;
    public static CalculationUserInput calculationUserInput, calculationUserInputTemp;
    public static SecondForm secondForm;
    Context context;
    String trackNumber, json;
    @SuppressLint("StaticFieldLeak")
    public static FormActivity activity;
    MyDatabase dataBase;
    DaoExaminerDuties daoExaminerDuties;
    FormActivityBinding binding;
    byte[] bitmap;
    String token, billId;
    CoordinateConversion conversion;
    double[] latLong;
    int counter;
    private LocationManager locationManager;
    private double latitude, longitude;
    int pageNumber = 1;
    private ArrayList<GeoPoint> polygonPoint = new ArrayList<>();
    private int polygonIndex, place1Index, place2Index, place3Index;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        binding = FormActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        activity = this;
        context = this;
//        Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context));
        if (getIntent().getExtras() != null) {
            trackNumber = getIntent().getExtras().getString(BundleEnum.TRACK_NUMBER.getValue());
            new SerializeJson().execute(getIntent());
        }
        initialize();
    }

    @SuppressLint("ClickableViewAccessibility")
    void initialize() {
        calculationUserInput = new CalculationUserInput();
        calculationUserInputTemp = new CalculationUserInput();
        new GetDBData().execute();
        setOnButtonClickListener();
        binding.imageViewRefresh.setOnClickListener(view -> {
            binding.mapView.getOverlays().clear();
            place1Index = 0;
            place2Index = 0;
            polygonIndex = 0;
            polygonPoint.removeAll(polygonPoint);
            initializeMap();
        });
    }

    void setOnButtonClickListener() {
        binding.buttonNext.setOnClickListener(view -> {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            FragmentManager fragmentManager = getSupportFragmentManager();
            switch (pageNumber) {
                case 1:
                    PersonalFragment personalFragment = (PersonalFragment)
                            fragmentManager.findFragmentById(R.id.fragment);
                    if (personalFragment != null) {
                        calculationUserInputTemp = personalFragment.setOnButtonNextClickListener();
                        if (calculationUserInputTemp != null) {
                            prepareFromPersonal();
                            fragmentTransaction = getSupportFragmentManager().beginTransaction();
                            fragmentTransaction.replace(R.id.fragment, new ServicesFragment());
                            fragmentTransaction.commit();
                            pageNumber = pageNumber + 1;
                        }
                    }
                    break;
                case 2:
                    calculationUserInputTemp = ServicesFragment.prepareServices();
                    if (calculationUserInputTemp != null) {
                        calculationUserInput.selectedServicesObject =
                                calculationUserInputTemp.selectedServicesObject;
                        calculationUserInput.selectedServicesString =
                                calculationUserInputTemp.selectedServicesString;
                        fragmentTransaction.replace(R.id.fragment, new FormFragment());
                        fragmentTransaction.commit();
                        pageNumber = pageNumber + 1;
                    } else
                        Toast.makeText(context, R.string.select_service, Toast.LENGTH_LONG).show();
                    break;
                case 3:
                    FormFragment formFragment = (FormFragment) fragmentManager.findFragmentById(R.id.fragment);
                    if (formFragment != null)
                        calculationUserInputTemp = formFragment.setOnButtonNextClickListener();
                    if (calculationUserInputTemp != null) {
                        prepareFromForm();
                        fragmentTransaction = getSupportFragmentManager().beginTransaction();
                        fragmentTransaction.replace(R.id.fragment, new SecondFormFragment());
                        fragmentTransaction.commit();
                        pageNumber = pageNumber + 1;
                    }
                    break;
                case 4:
                    SecondFormFragment secondFormFragment = (SecondFormFragment) fragmentManager.findFragmentById(R.id.fragment);
                    if (secondFormFragment != null) {
                        secondForm = secondFormFragment.setOnButtonNextClickListener();
                    }
                    if (secondForm != null) {
                        binding.buttonNext.setText(R.string.save_info);
                        pageNumber = pageNumber + 1;
                        binding.fragment.setVisibility(View.GONE);
                        binding.relativeLayoutMap.setVisibility(View.VISIBLE);
                        setActionBarTitle(
                                context.getString(R.string.app_name).concat(" / ").concat(context.getString(R.string.location)));
//                        fragmentTransaction = getSupportFragmentManager().beginTransaction();
//                        fragmentTransaction.replace(R.id.fragment, new MapFragment());
//                        fragmentTransaction.commit();
                    }
                    break;
                case 5:
                    Intent intent = new Intent(getApplicationContext(), DocumentFormActivity.class);
                    bitmap = convertBitmapToByte(convertMapToBitmap());
                    intent.putExtra(BundleEnum.IMAGE_BITMAP.getValue(), bitmap);

                    intent.putExtra(BundleEnum.TRACK_NUMBER.getValue(), trackNumber);
                    intent.putExtra(BundleEnum.BILL_ID.getValue(), examinerDuties.getBillId());
                    intent.putExtra(BundleEnum.NEW_ENSHEAB.getValue(), examinerDuties.isNewEnsheab());
                    prepareToSend();
                    startActivity(intent);
//                    MapFragment mapFragment = (MapFragment) fragmentManager.findFragmentById(R.id.fragment);
//                    Intent intent = new Intent(getApplicationContext(), DocumentFormActivity.class);
//                    if (mapFragment != null) {
//                        bitmap = convertBitmapToByte(mapFragment.convertMapToBitmap());
//                        intent.putExtra(BundleEnum.IMAGE_BITMAP.getValue(), bitmap);
//                    }
//                    intent.putExtra(BundleEnum.TRACK_NUMBER.getValue(), trackNumber);
//                    intent.putExtra(BundleEnum.BILL_ID.getValue(), examinerDuties.getBillId());
//                    intent.putExtra(BundleEnum.NEW_ENSHEAB.getValue(), examinerDuties.isNewEnsheab());
//                    prepareToSend();
//                    startActivity(intent);
//                    finish();
                    break;
            }
        });
        binding.buttonPrevious.setOnClickListener(view -> {
            FragmentTransaction fragmentTransaction;
            switch (pageNumber) {
                case 1:
                    finish();
                    break;
                case 2:
                    fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.fragment, new PersonalFragment());
                    fragmentTransaction.commit();
                    break;
                case 3:
                    fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.fragment, new ServicesFragment());
                    fragmentTransaction.commit();
                    break;
                case 4:
                    fragmentTransaction = getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.fragment, new FormFragment());
                    fragmentTransaction.commit();
                    break;
                case 5:
                    binding.buttonNext.setText(R.string.next);
                    binding.fragment.setVisibility(View.VISIBLE);
                    binding.relativeLayoutMap.setVisibility(View.GONE);
//                    fragmentTransaction = getSupportFragmentManager().beginTransaction();
//                    fragmentTransaction.replace(R.id.fragment, new SecondFormFragment());
//                    fragmentTransaction.commit();
                    break;
            }
            pageNumber = pageNumber - 1;
        });
    }

    void prepareFromForm() {
        calculationUserInput.sifoon100 = calculationUserInputTemp.sifoon100;
        calculationUserInput.sifoon125 = calculationUserInputTemp.sifoon125;
        calculationUserInput.sifoon150 = calculationUserInputTemp.sifoon150;
        calculationUserInput.sifoon200 = calculationUserInputTemp.sifoon200;
        calculationUserInput.arse = calculationUserInputTemp.arse;
        calculationUserInput.aianKol = calculationUserInputTemp.aianKol;
        calculationUserInput.aianMaskooni = calculationUserInputTemp.aianMaskooni;
        calculationUserInput.aianTejari = calculationUserInputTemp.aianTejari;
        calculationUserInput.tedadMaskooni = calculationUserInputTemp.tedadMaskooni;
        calculationUserInput.tedadTejari = calculationUserInputTemp.tedadTejari;
        calculationUserInput.tedadSaier = calculationUserInputTemp.tedadSaier;
        calculationUserInput.arzeshMelk = calculationUserInputTemp.arzeshMelk;
        calculationUserInput.tedadTaxfif = calculationUserInputTemp.tedadTaxfif;
        calculationUserInput.zarfiatQarardadi = calculationUserInputTemp.zarfiatQarardadi;
        calculationUserInput.parNumber = calculationUserInputTemp.parNumber;
        calculationUserInput.karbariId = calculationUserInputTemp.karbariId;
        calculationUserInput.noeVagozariId = calculationUserInputTemp.noeVagozariId;
        calculationUserInput.qotrEnsheabId = calculationUserInputTemp.qotrEnsheabId;
        calculationUserInput.taxfifId = calculationUserInputTemp.taxfifId;
        calculationUserInput.adamTaxfifAb = calculationUserInputTemp.adamTaxfifAb;
        calculationUserInput.adamTaxfifFazelab = calculationUserInputTemp.adamTaxfifFazelab;
        calculationUserInput.ensheabQeireDaem = calculationUserInputTemp.ensheabQeireDaem;

        examinerDuties.setSifoon100(calculationUserInputTemp.sifoon100);
        examinerDuties.setSifoon125(calculationUserInputTemp.sifoon125);
        examinerDuties.setSifoon150(calculationUserInputTemp.sifoon150);
        examinerDuties.setSifoon200(calculationUserInputTemp.sifoon200);
        examinerDuties.setArse(calculationUserInputTemp.arse);
        examinerDuties.setAianMaskooni(calculationUserInputTemp.aianMaskooni);
        examinerDuties.setAianNonMaskooni(calculationUserInputTemp.aianTejari);
        examinerDuties.setAianKol(calculationUserInputTemp.aianKol);
        examinerDuties.setTedadMaskooni(calculationUserInputTemp.tedadMaskooni);
        examinerDuties.setTedadTejari(calculationUserInputTemp.tedadTejari);
        examinerDuties.setTedadSaier(calculationUserInputTemp.tedadSaier);
        examinerDuties.setTedadTaxfif(calculationUserInputTemp.tedadTaxfif);
        examinerDuties.setZarfiatQarardadi(calculationUserInputTemp.zarfiatQarardadi);
        examinerDuties.setArzeshMelk(calculationUserInputTemp.arzeshMelk);
        examinerDuties.setParNumber(calculationUserInputTemp.parNumber);

        examinerDuties.setKarbariId(calculationUserInputTemp.karbariId);
        examinerDuties.setQotrEnsheabId(calculationUserInputTemp.qotrEnsheabId);
        examinerDuties.setTaxfifId(calculationUserInputTemp.taxfifId);

        examinerDuties.setEnsheabQeirDaem(calculationUserInputTemp.ensheabQeireDaem);
    }

    void prepareFromPersonal() {
        calculationUserInput.nationalId = calculationUserInputTemp.nationalId;
        calculationUserInput.firstName = calculationUserInputTemp.firstName;
        calculationUserInput.sureName = calculationUserInputTemp.sureName;
        calculationUserInput.fatherName = calculationUserInputTemp.fatherName;
        calculationUserInput.postalCode = calculationUserInputTemp.postalCode;
        calculationUserInput.radif = calculationUserInputTemp.radif;
        calculationUserInput.phoneNumber = calculationUserInputTemp.phoneNumber;
        calculationUserInput.mobile = calculationUserInputTemp.mobile;
        calculationUserInput.address = calculationUserInputTemp.address;
        calculationUserInput.description = calculationUserInputTemp.description;

        examinerDuties.setNationalId(calculationUserInputTemp.nationalId);
        examinerDuties.setFirstName(calculationUserInputTemp.firstName);
        examinerDuties.setSureName(calculationUserInputTemp.sureName);
        examinerDuties.setNameAndFamily(calculationUserInputTemp.firstName.concat(" ")
                .concat(calculationUserInputTemp.sureName));
        examinerDuties.setFatherName(calculationUserInputTemp.fatherName);
        examinerDuties.setPostalCode(calculationUserInput.postalCode);
        examinerDuties.setPhoneNumber(calculationUserInput.phoneNumber);
        examinerDuties.setMobile(calculationUserInputTemp.mobile);//TODO 3 mobile
        examinerDuties.setAddress(calculationUserInputTemp.address);
        examinerDuties.setDescription(calculationUserInputTemp.description);
    }

    private byte[] convertBitmapToByte(Bitmap bitmap) {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos);
        return bos.toByteArray();
    }

    void prepareToSend() {
        fillCalculationUserInput();
        updateCalculationUserInput();
        updateExamination();
    }

    void fillCalculationUserInput() {
        calculationUserInput.trackingId = examinerDuties.getTrackingId();
        calculationUserInput.requestType = Integer.parseInt(examinerDuties.getRequestType());
        calculationUserInput.parNumber = examinerDuties.getParNumber();
        calculationUserInput.billId = examinerDuties.getBillId();
        calculationUserInput.neighbourBillId = examinerDuties.getNeighbourBillId();
        calculationUserInput.notificationMobile = examinerDuties.getNotificationMobile();
        calculationUserInput.identityCode = examinerDuties.getIdentityCode();
        calculationUserInput.trackNumber = examinerDuties.getTrackNumber();
        calculationUserInput.setSent(false);
    }

    void updateCalculationUserInput() {
        DaoCalculationUserInput daoCalculationUserInput = dataBase.daoCalculationUserInput();
        daoCalculationUserInput.deleteByTrackNumber(trackNumber);
        daoCalculationUserInput.insertCalculationUserInput(calculationUserInput);
    }

    void updateExamination() {
        DaoExaminerDuties daoExaminerDuties = dataBase.daoExaminerDuties();
        daoExaminerDuties.insert(examinerDuties.updateExaminerDuties(calculationUserInput));
    }

    public void setActionBarTitle(String title) {
        Objects.requireNonNull(getSupportActionBar()).setTitle(title);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @SuppressLint("StaticFieldLeak")
    class SerializeJson extends AsyncTask<Intent, String, String> {
        ProgressDialog dialog;

        @Override
        protected String doInBackground(Intent... intents) {
            json = Objects.requireNonNull(getIntent().getExtras()).getString(BundleEnum.SERVICES.getValue());
            Gson gson = new GsonBuilder().create();
            requestDictionaries = Arrays.asList(gson.fromJson(json, RequestDictionary[].class));
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(context);
            dialog.setMessage(context.getString(R.string.loading_getting_info));
            dialog.setTitle(context.getString(R.string.loading_connecting));
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            dialog.dismiss();
        }

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
            billId = examinerDuties.getBillId();
        else billId = examinerDuties.getNeighbourBillId();
        getXY(billId);
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
        LocationManager mLocationManager = (LocationManager) Objects.requireNonNull(getSystemService(LOCATION_SERVICE));
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
        HttpClientWrapper.callHttpAsync(call, ProgressType.NOT_SHOW.getValue(), context,
                new GetXY(), new GetXYIncomplete(), new GetError());
    }

    void getGISToken() {
        Retrofit retrofit = NetworkHelper.getInstance(true, "");
        IAbfaService iAbfaService = retrofit.create(IAbfaService.class);
        Call<GISToken> call = iAbfaService.getGISToken();
        HttpClientWrapper.callHttpAsync(call, ProgressType.NOT_SHOW.getValue(), context,
                new GetGISToken(), new GetGISTokenIncomplete(), new GetError());
    }

    void getGis(int i) {
        Retrofit retrofit = NetworkHelper.getInstanceMap();
        IAbfaService iAbfaService = retrofit.create(IAbfaService.class);
        Call<String> call;
        if (i == 1) {
            call = iAbfaService.getGisWaterPipe(new GISInfo("jesuschrist", token, billId,
                    latLong[0], latLong[1]));
            HttpClientWrapper.callHttpAsync(call, ProgressType.NOT_SHOW.getValue(), context,
                    new GetGISWaterPipe(), new GetGISIncomplete(), new GetError());
        } else if (i == 2) {
            call = iAbfaService.getGisWaterTransfer(new GISInfo("jesuschrist", token, billId,
                    latLong[0], latLong[1]));
            HttpClientWrapper.callHttpAsync(call, ProgressType.NOT_SHOW.getValue(), context,
                    new GetGISWaterTransfer(), new GetGISIncomplete(), new GetError());
        } else if (i == 3) {
            call = iAbfaService.getGisSanitationTransfer(new GISInfo("jesuschrist", token, billId,
                    latLong[0], latLong[1]));
            HttpClientWrapper.callHttpAsync(call, ProgressType.NOT_SHOW.getValue(), context,
                    new GetGISSanitationTransfer(), new GetGISIncomplete(), new GetError());
        } else {
            call = iAbfaService.getGisParcels(new GISInfo("jesuschrist", token, billId,
                    latLong[0], latLong[1]));
            HttpClientWrapper.callHttpAsync(call, ProgressType.NOT_SHOW.getValue(), context,
                    new GetGISParcels(), new GetGISIncomplete(), new GetError());
        }
    }

    @SuppressLint("StaticFieldLeak")
    class GetDBData extends AsyncTask<Integer, String, String> {
        ProgressDialog dialog;

        @Override
        protected String doInBackground(Integer... integers) {
            dataBase = Room.databaseBuilder(context, MyDatabase.class, MyApplication.getDBNAME())
                    .allowMainThreadQueries().build();
            daoExaminerDuties = dataBase.daoExaminerDuties();
            examinerDuties = daoExaminerDuties.unreadExaminerDutiesByTrackNumber(trackNumber);
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.fragment, new PersonalFragment());
            fragmentTransaction.commit();
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = new ProgressDialog(context);
            dialog.setMessage(context.getString(R.string.loading_getting_info));
            dialog.setTitle(context.getString(R.string.loading_connecting));
            dialog.setCancelable(false);
            dialog.show();
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            initializeMap();
            dialog.dismiss();
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    class GetGISSanitationTransfer implements ICallback<String> {
        @Override
        public void execute(String s) {
            CustomArcGISJSON customArcGISJSON = ConvertArcToGeo.convertStringToCustomArcGISJSON(s);
            CustomGeoJSON customGeoJSON = ConvertArcToGeo.convertPolygon(customArcGISJSON, "Polygon");
            KmlDocument kmlDocument = new KmlDocument();
            kmlDocument.parseGeoJSON(ConvertArcToGeo.convertCustomGeoJSONToString(customGeoJSON));

            MyKmlStyle.color = 4;
            FolderOverlay geoJsonOverlay = (FolderOverlay) kmlDocument.mKmlRoot.buildOverlay(
                    binding.mapView, null, new MyKmlStyle(), kmlDocument);
            binding.mapView.getOverlays().add(geoJsonOverlay);
            binding.mapView.invalidate();
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    class GetGISWaterTransfer implements ICallback<String> {
        @Override
        public void execute(String s) {
            CustomArcGISJSON customArcGISJSON = ConvertArcToGeo.convertStringToCustomArcGISJSON(s);
            CustomGeoJSON customGeoJSON = ConvertArcToGeo.convertPolygon(customArcGISJSON, "Polygon");
            KmlDocument kmlDocument = new KmlDocument();
            kmlDocument.parseGeoJSON(ConvertArcToGeo.convertCustomGeoJSONToString(customGeoJSON));
            MyKmlStyle.color = 3;
            FolderOverlay geoJsonOverlay = (FolderOverlay) kmlDocument.mKmlRoot.buildOverlay(
                    binding.mapView, null, new MyKmlStyle(), kmlDocument);
            binding.mapView.getOverlays().add(geoJsonOverlay);
            binding.mapView.invalidate();
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    class GetGISWaterPipe implements ICallback<String> {
        @Override
        public void execute(String s) {
            CustomArcGISJSON customArcGISJSON = ConvertArcToGeo.convertStringToCustomArcGISJSON(s);
            CustomGeoJSON customGeoJSON = ConvertArcToGeo.convertPolygon(customArcGISJSON, "Polygon");
            KmlDocument kmlDocument = new KmlDocument();
            kmlDocument.parseGeoJSON(ConvertArcToGeo.convertCustomGeoJSONToString(customGeoJSON));
            MyKmlStyle.color = 2;
            FolderOverlay geoJsonOverlay = (FolderOverlay) kmlDocument.mKmlRoot.buildOverlay(
                    binding.mapView, null, new MyKmlStyle(), kmlDocument);
            binding.mapView.getOverlays().add(geoJsonOverlay);
            binding.mapView.invalidate();
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    class GetGISParcels implements ICallback<String> {
        @Override
        public void execute(String s) {
            CustomArcGISJSON customArcGISJSON = ConvertArcToGeo.convertStringToCustomArcGISJSON(s);
            CustomGeoJSON customGeoJSON = ConvertArcToGeo.convertPolygon(customArcGISJSON, "Polygon");
            KmlDocument kmlDocument = new KmlDocument();
            kmlDocument.parseGeoJSON(ConvertArcToGeo.convertCustomGeoJSONToString(customGeoJSON));
            MyKmlStyle.color = 1;
            FolderOverlay geoJsonOverlay = (FolderOverlay) kmlDocument.mKmlRoot.buildOverlay(
                    binding.mapView, null, new MyKmlStyle(), kmlDocument);
            binding.mapView.getOverlays().add(geoJsonOverlay);
            binding.mapView.invalidate();
        }
    }

    class GetGISIncomplete implements ICallbackIncomplete<String> {
        @Override
        public void executeIncomplete(Response<String> response) {
            if (response.errorBody() != null) {
                Log.e("Error GetGISIncomplete", response.errorBody().toString());
            }
        }
    }

    class GetGISToken implements ICallback<GISToken> {
        @Override
        public void execute(GISToken gisToken) {
            token = gisToken.getToken();
            if (latLong != null) {
                getGis(0);
                getGis(1);
                getGis(2);
                getGis(3);
            }
        }
    }

    class GetGISTokenIncomplete implements ICallbackIncomplete<GISToken> {
        @Override
        public void executeIncomplete(Response<GISToken> response) {
            if (response.errorBody() != null) {
                Log.e("GetGISTokenIncomplete", response.errorBody().toString());
            }
        }
    }

    class GetXY implements ICallback<Place> {
        @Override
        public void execute(Place place) {
            if (place.getX() != 0 && place.getY() != 0) {
                String utm = "39 S ".concat(String.valueOf(place.getX())).concat(" ")
                        .concat(String.valueOf(place.getY()));
                latLong = conversion.utm2LatLon(utm);
                addUserPlace(new GeoPoint(latLong[0], latLong[1]));
                getGISToken();
            }
        }
    }

    class GetXYIncomplete implements ICallbackIncomplete<Place> {
        @Override
        public void executeIncomplete(Response<Place> response) {
            if (response.errorBody() != null) {
                Log.e("GetXYIncomplete", response.errorBody().toString());
            }
        }
    }

    class GetError implements ICallbackError {
        @Override
        public void executeError(Throwable t) {
            Log.e("GetError", Objects.requireNonNull(t.getMessage()));
        }
    }
}
