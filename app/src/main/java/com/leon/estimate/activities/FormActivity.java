package com.leon.estimate.activities;

import static android.graphics.Color.BLUE;
import static android.graphics.Color.RED;
import static android.graphics.Color.YELLOW;
import static com.leon.estimate.Utils.Constants.REQUEST_LOCATION_CODE;
import static com.leon.estimate.Utils.Constants.arzeshdaraei;
import static com.leon.estimate.Utils.Constants.calculationUserInput;
import static com.leon.estimate.Utils.Constants.calculationUserInputTemp;
import static com.leon.estimate.Utils.Constants.examinerDuties;
import static com.leon.estimate.Utils.Constants.others;
import static com.leon.estimate.Utils.Constants.secondForm;
import static com.leon.estimate.Utils.Constants.valueInteger;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Debug;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.location.LocationManagerCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.room.Room;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.leon.estimate.Enums.BundleEnum;
import com.leon.estimate.Enums.CompanyNames;
import com.leon.estimate.Enums.ProgressType;
import com.leon.estimate.Infrastructure.IAbfaService;
import com.leon.estimate.Infrastructure.ICallback;
import com.leon.estimate.Infrastructure.ICallbackError;
import com.leon.estimate.Infrastructure.ICallbackIncomplete;
import com.leon.estimate.MyApplication;
import com.leon.estimate.R;
import com.leon.estimate.Tables.Arzeshdaraei;
import com.leon.estimate.Tables.Block;
import com.leon.estimate.Tables.CalculationUserInput;
import com.leon.estimate.Tables.DaoBlock;
import com.leon.estimate.Tables.DaoCalculationUserInput;
import com.leon.estimate.Tables.DaoExaminerDuties;
import com.leon.estimate.Tables.DaoFormula;
import com.leon.estimate.Tables.DaoTejariha;
import com.leon.estimate.Tables.DaoZarib;
import com.leon.estimate.Tables.Formula;
import com.leon.estimate.Tables.GISInfo;
import com.leon.estimate.Tables.GISToken;
import com.leon.estimate.Tables.MyDatabase;
import com.leon.estimate.Tables.Place;
import com.leon.estimate.Tables.RequestDictionary;
import com.leon.estimate.Tables.SecondForm;
import com.leon.estimate.Tables.Tejariha;
import com.leon.estimate.Tables.Zarib;
import com.leon.estimate.Utils.Constants;
import com.leon.estimate.Utils.CoordinateConversion;
import com.leon.estimate.Utils.DifferentCompanyManager;
import com.leon.estimate.Utils.GIS.ConvertArcToGeo;
import com.leon.estimate.Utils.GIS.CustomArcGISJSON;
import com.leon.estimate.Utils.GIS.CustomGeoJSON;
import com.leon.estimate.Utils.GIS.MyKmlStyle;
import com.leon.estimate.Utils.GPSTracker;
import com.leon.estimate.Utils.HttpClientWrapper;
import com.leon.estimate.Utils.NetworkHelper;
import com.leon.estimate.databinding.FormActivityBinding;
import com.leon.estimate.fragments.EnterBillIdFragment;
import com.leon.estimate.fragments.FormFragment;
import com.leon.estimate.fragments.PersonalFragment;
import com.leon.estimate.fragments.SecondFormFragment;
import com.leon.estimate.fragments.ServicesFragment;

import org.jetbrains.annotations.NotNull;
import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.kml.KmlDocument;
import org.osmdroid.events.MapEventsReceiver;
import org.osmdroid.tileprovider.tilesource.OnlineTileSourceBase;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.util.MapTileIndex;
import org.osmdroid.views.CustomZoomButtonsController;
import org.osmdroid.views.overlay.FolderOverlay;
import org.osmdroid.views.overlay.MapEventsOverlay;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

public class FormActivity extends AppCompatActivity {
    @SuppressLint("StaticFieldLeak")
    public static FormActivity activity;
    FormActivityBinding binding;
    Context context;
    MyDatabase dataBase;
    int polygonIndex, place1Index, place2Index, pageNumber = 1;
    double latitude, longitude;
    int[] indexes;
    double[] latLong;
    FolderOverlay[] geoJsonOverlays;
    GPSTracker gpsTracker;
    Marker startMarker;
    CoordinateConversion conversion;
    ArrayList<GeoPoint> polygonPoint = new ArrayList<>();
    String token, billId, trackNumber, json;
    Bitmap bitmap;
    @SuppressLint("NonConstantResourceId")
    CompoundButton.OnCheckedChangeListener onCheckedChangeListener = (buttonView, isChecked) -> {
        int id = buttonView.getId();
        switch (id) {
            case R.id.checkboxParcels:
                if (isChecked) {
                    binding.mapView.getOverlays().add(geoJsonOverlays[0]);
                } else {
                    binding.mapView.getOverlays().remove(geoJsonOverlays[0]);
                }
                break;
            case R.id.checkboxWaterPipe:
                if (isChecked) {
                    binding.mapView.getOverlays().add(geoJsonOverlays[1]);
                } else {
                    binding.mapView.getOverlays().remove(geoJsonOverlays[1]);
                }
                break;
            case R.id.checkboxWaterTransfer:
                if (isChecked) {
                    binding.mapView.getOverlays().add(geoJsonOverlays[2]);
                } else {
                    binding.mapView.getOverlays().remove(geoJsonOverlays[2]);
                }
                break;
            case R.id.checkboxSanitationTransfer:
                if (isChecked) {
                    binding.mapView.getOverlays().add(geoJsonOverlays[3]);
                } else {
                    binding.mapView.getOverlays().remove(geoJsonOverlays[3]);
                }
                break;
        }
        try {
            binding.mapView.invalidate();
            indexes[0] = binding.mapView.getOverlays().indexOf(geoJsonOverlays[0]);
            indexes[1] = binding.mapView.getOverlays().indexOf(geoJsonOverlays[0]);
            indexes[2] = binding.mapView.getOverlays().indexOf(geoJsonOverlays[0]);
            indexes[3] = binding.mapView.getOverlays().indexOf(geoJsonOverlays[0]);
        } catch (Exception e) {
            e.printStackTrace();
        }
    };
    View.OnClickListener onClickListener = new View.OnClickListener() {
        @SuppressLint("NonConstantResourceId")
        @Override
        public void onClick(View v) {
            int id = v.getId();
            switch (id) {
                case R.id.imageViewRefresh1:
                    //TODO
                    binding.mapView.getOverlays().clear();
//                    binding.mapView.getOverlays().add(startMarker);
                    place1Index = 0;
                    place2Index = 0;
                    polygonIndex = 0;
                    polygonPoint.clear();
                    binding.checkboxSanitationTransfer.setChecked(false);
                    binding.checkboxWaterTransfer.setChecked(false);
                    binding.checkboxWaterPipe.setChecked(false);
                    binding.checkboxParcels.setChecked(false);
                    calculationUserInput.x3 = 0;
                    calculationUserInput.y3 = 0;
                    initializeMap(true);
                    break;
                case R.id.imageViewRefresh2:
                    binding.signatureView.clearCanvas();
                    binding.signatureView.setBitmap(convertMapToBitmap());
                    break;
                case R.id.imageViewColorBlue:
                    binding.signatureView.setPenColor(BLUE);
                    break;
                case R.id.imageViewColorRed:
                    binding.signatureView.setPenColor(RED);
                    break;
                case R.id.imageViewColorYellow:
                    binding.signatureView.setPenColor(YELLOW);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
        binding = FormActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        activity = this;
        context = this;
        if (getIntent().getExtras() != null) {
            trackNumber = getIntent().getExtras().getString(BundleEnum.TRACK_NUMBER.getValue());
            new SerializeJson().execute(getIntent());
        }
        initialize();
    }

    @SuppressLint("ClickableViewAccessibility")
    void initialize() {
        valueInteger = new ArrayList<>();
        for (int i = 0; i < 8; i++)
            valueInteger.add(0);
        calculationUserInput = new CalculationUserInput();
        calculationUserInputTemp = new CalculationUserInput();
        others = new ArrayList<>();
        new GetDBData().execute();
        setOnButtonClickListener();
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.signatureView.setPenColor(YELLOW);
        binding.imageViewRefresh1.setOnClickListener(onClickListener);
        binding.imageViewRefresh2.setOnClickListener(onClickListener);
        binding.imageViewColorYellow.setOnClickListener(onClickListener);
        binding.imageViewColorBlue.setOnClickListener(onClickListener);
        binding.imageViewColorRed.setOnClickListener(onClickListener);
        binding.checkboxParcels.setOnCheckedChangeListener(onCheckedChangeListener);
        binding.checkboxWaterPipe.setOnCheckedChangeListener(onCheckedChangeListener);
        binding.checkboxWaterTransfer.setOnCheckedChangeListener(onCheckedChangeListener);
        binding.checkboxSanitationTransfer.setOnCheckedChangeListener(onCheckedChangeListener);
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
                        Toast.makeText(context, R.string.select_service,
                                Toast.LENGTH_LONG).show();
                    break;
                case 3:
                    FormFragment formFragment = (FormFragment)
                            fragmentManager.findFragmentById(R.id.fragment);
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
                    SecondFormFragment secondFormFragment = (SecondFormFragment)
                            fragmentManager.findFragmentById(R.id.fragment);
                    if (secondFormFragment != null) {
                        secondForm = secondFormFragment.setOnButtonNextClickListener();
                    }
                    if (secondForm != null) {
                        examinerDuties.updateExaminerDuties(secondForm);
                        if (examinerDuties.getMapDescription() != null)
                            binding.editTextDescription.setText(examinerDuties.getMapDescription().trim());
                        pageNumber = pageNumber + 1;
                        binding.fragment.setVisibility(View.GONE);
                        binding.relativeLayoutMap.setVisibility(View.VISIBLE);
                        setActionBarTitle(
                                context.getString(R.string.app_name).concat(" / ").concat("صفحه پنجم"));
                        binding.buttonNext.setText(R.string.crooki);
                    }
                    break;
                case 5:
                    bitmap = convertMapToBitmap();
                    examinerDuties.setMapDescription(binding.editTextDescription.getText().toString());
                    pageNumber = pageNumber + 1;
                    binding.relativeLayoutMap.setVisibility(View.GONE);
                    binding.relativeLayoutEditMap.setVisibility(View.VISIBLE);
                    binding.signatureView.setBitmap(bitmap);
                    setActionBarTitle(
                            context.getString(R.string.app_name).concat(" / ").concat("صفحه ششم"));
                    binding.buttonNext.setText(R.string.save_info);
                    break;
                case 6:
                    Constants.bitmapMapImage = binding.signatureView.getSignatureBitmap();
                    Intent intent = new Intent(getApplicationContext(), DocumentFormActivity.class);
                    intent.putExtra(BundleEnum.TRACK_NUMBER.getValue(), trackNumber);
                    if (examinerDuties.getBillId() != null)
                        intent.putExtra(BundleEnum.BILL_ID.getValue(), examinerDuties.getBillId());
                    else
                        intent.putExtra(BundleEnum.BILL_ID.getValue(), examinerDuties.getNeighbourBillId());
                    intent.putExtra(BundleEnum.NEW_ENSHEAB.getValue(), examinerDuties.isNewEnsheab());
                    prepareToSend();
                    startActivity(intent);
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
                    setActionBarTitle(
                            context.getString(R.string.app_name).concat(" / ").concat("صفحه چهارم"));
                    break;
                case 6:
                    //TODO
                    binding.buttonNext.setText(R.string.crooki);
                    binding.relativeLayoutMap.setVisibility(View.VISIBLE);
                    binding.relativeLayoutEditMap.setVisibility(View.GONE);
//                    binding.mapView.getOverlays().add(startMarker);
                    setActionBarTitle(
                            context.getString(R.string.app_name).concat(" / ").concat("صفحه پنجم"));
                    break;
            }
            pageNumber = pageNumber - 1;
        });
    }

    void prepareFromForm() {
        calculationUserInput.prepareCalculationUserInputFromForm();
        examinerDuties.prepareExaminerDutiesFromForm();
    }

    void prepareFromPersonal() {
        calculationUserInput.prepareCalculationFromPersonal();
        examinerDuties.prepareExaminerDutiesFromPersonal();
    }

    void prepareToSend() {
        calculationUserInput.fillCalculationUserInput();
        updateCalculationUserInput();
        updateExamination();
        updateOthers();
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

    void updateOthers() {
        DaoTejariha daoTejariha = dataBase.daoTejariha();
        for (int i = 0; i < others.size(); i++)
            daoTejariha.insertTejariha(others.get(i));
    }

    public void setActionBarTitle(String title) {
        Objects.requireNonNull(getSupportActionBar()).setTitle(title);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void addPlace(GeoPoint p) {
        GeoPoint startPoint = new GeoPoint(p.getLatitude(), p.getLongitude());
        Marker startMarker = new Marker(binding.mapView);
        startMarker.setPosition(startPoint);
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        if (place1Index != 0 && place2Index == 0) {
            startMarker.setIcon(getResources().getDrawable(R.drawable.map_siphon_drop_point));
            try {
                binding.mapView.getOverlays().add(startMarker);
                place2Index = binding.mapView.getOverlays().size() - 2;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (place2Index != 0) {
            try {
                binding.mapView.getOverlays().remove(place1Index);
                binding.mapView.getOverlays().remove(place2Index);
                place1Index = 0;
                place2Index = 0;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (place1Index == 0) {
            calculationUserInput.y3 = p.getLatitude();
            calculationUserInput.x3 = p.getLongitude();
            startMarker.setIcon(getResources().getDrawable(R.drawable.map_water_drop_point));
            try {
                binding.mapView.getOverlays().add(startMarker);
                place1Index = binding.mapView.getOverlays().size() - 1;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public Bitmap convertMapToBitmap() {
        if (examinerDuties.isNewEnsheab())
            binding.mapView.getOverlays().remove(startMarker);
        binding.mapView.destroyDrawingCache();
        binding.mapView.setDrawingCacheEnabled(true);
        return binding.mapView.getDrawingCache(true);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initializeMap(boolean isRefresh) {
        if (!GpsEnabled()) {
            initialize();
            return;
        }
        if (MyApplication.isLocal) {
            final OnlineTileSourceBase custom = new OnlineTileSourceBase("custom",
                    0, 19, 256, ".png", new String[]{
                    DifferentCompanyManager.getLocalBaseUrl(CompanyNames.ESF_MAP)
            }) {
                @Override
                public String getTileURLString(long aTile) {
                    return getBaseUrl() + MapTileIndex.getZoom(aTile) + "/" + MapTileIndex.getX(aTile)
                            + "/" + MapTileIndex.getY(aTile) + mImageFilenameEnding;
                }
            };
            binding.mapView.setTileSource(custom);
        }
        binding.mapView.getZoomController().setVisibility(
                CustomZoomButtonsController.Visibility.SHOW_AND_FADEOUT);
        binding.mapView.setMultiTouchControls(true);
        IMapController mapController = binding.mapView.getController();
        mapController.setZoom(19.5);
        gpsTracker = new GPSTracker(activity);
        latitude = gpsTracker.getLatitude();
        longitude = gpsTracker.getLongitude();
        GeoPoint startPoint = new GeoPoint(latitude, longitude);
        mapController.setCenter(startPoint);
        MyLocationNewOverlay locationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(context), binding.mapView);
        locationOverlay.enableMyLocation();
        try {
            binding.mapView.getOverlays().add(locationOverlay);
        } catch (Exception e) {
            e.printStackTrace();
        }
        conversion = new CoordinateConversion();

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
        if (!isRefresh) {
            if (examinerDuties.getBillId() != null)
                billId = examinerDuties.getBillId();
            else billId = examinerDuties.getNeighbourBillId();
            getXY(billId);
        }
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

    private void addUserPlace(GeoPoint p) {
        GeoPoint startPoint = new GeoPoint(p.getLatitude(), p.getLongitude());
        startMarker = new Marker(binding.mapView);
        startMarker.setPosition(startPoint);
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        try {
            binding.mapView.getOverlayManager().add(startMarker);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createPolygon(GeoPoint geoPoint) {
        Polyline line = new Polyline(binding.mapView);
        line.getOutlinePaint().setColor(Color.YELLOW);
        if (polygonIndex != 0) {
            try {
                binding.mapView.getOverlays().remove(polygonIndex);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            binding.mapView.getOverlays().add(line);
        } catch (Exception e) {
            e.printStackTrace();
        }
        polygonPoint.add(geoPoint);
        polygonPoint.add(polygonPoint.get(0));
        line.setPoints(polygonPoint);
        polygonPoint.remove(polygonPoint.size() - 1);
        polygonIndex = binding.mapView.getOverlays().size() - 1;
    }

    void getXY(String billId) {
        binding.progressBar.setVisibility(View.VISIBLE);
        Retrofit retrofit = NetworkHelper.getInstance("");
        IAbfaService iAbfaService = retrofit.create(IAbfaService.class);
        Call<Place> call = iAbfaService.getXY(billId);
        HttpClientWrapper.callHttpAsync(call, ProgressType.NOT_SHOW.getValue(), context,
                new GetXY(), new GetXYIncomplete(), new GetError());
    }

    void getGISToken() {
        Retrofit retrofit = NetworkHelper.getInstance("");
        IAbfaService iAbfaService = retrofit.create(IAbfaService.class);
        Call<GISToken> call = iAbfaService.getGISToken();
        HttpClientWrapper.callHttpAsync(call, ProgressType.NOT_SHOW.getValue(), context,
                new GetGISToken(), new GetGISTokenIncomplete(), new GetError());
    }

    void getGis(int i) {
        Retrofit retrofit = NetworkHelper.getInstance();
        IAbfaService iAbfaService = retrofit.create(IAbfaService.class);
        Call<String> call;
        binding.progressBar.setVisibility(View.VISIBLE);
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

    @Override
    public boolean onOptionsItemSelected(@NotNull MenuItem item) {
        int id = item.getItemId();
        Intent intent = new Intent(getApplicationContext(), DocumentActivity.class);
        intent.putExtra(BundleEnum.TRACK_NUMBER.getValue(), trackNumber);
        if (examinerDuties.getBillId() != null)
            intent.putExtra(BundleEnum.BILL_ID.getValue(), examinerDuties.getBillId());
        else
            intent.putExtra(BundleEnum.BILL_ID.getValue(), examinerDuties.getNeighbourBillId());
        intent.putExtra(BundleEnum.NEW_ENSHEAB.getValue(), examinerDuties.isNewEnsheab());
        if (id == R.id.menu_document) {
            intent.putExtra(BundleEnum.IS_NEIGHBOUR.getValue(), false);
            startActivity(intent);
        } else if (id == R.id.menu_neighbour_document) {
            intent.putExtra(BundleEnum.IS_NEIGHBOUR.getValue(), true);
            startActivity(intent);
        } else if (id == R.id.menu_other_document) {
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            EnterBillIdFragment enterBillIdFragment = EnterBillIdFragment.newInstance();
            enterBillIdFragment.show(fragmentTransaction, "bill Id");
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.document_menu, menu);
        if (!examinerDuties.isNewEnsheab()) {
            menu.getItem(1).setVisible(false);
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Runtime.getRuntime().totalMemory();
        Runtime.getRuntime().freeMemory();
        Runtime.getRuntime().maxMemory();
        Debug.getNativeHeapAllocatedSize();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Runtime.getRuntime().totalMemory();
        Runtime.getRuntime().freeMemory();
        Runtime.getRuntime().maxMemory();
        Debug.getNativeHeapAllocatedSize();
    }

    @SuppressLint("StaticFieldLeak")
    class SerializeJson extends AsyncTask<Intent, String, String> {
        ProgressDialog dialog;

        public SerializeJson() {
            super();
        }

        @Override
        protected String doInBackground(Intent... intents) {
            json = Objects.requireNonNull(getIntent().getExtras()).getString(BundleEnum.SERVICES.getValue());
            Gson gson = new GsonBuilder().create();
            Constants.requestDictionaries = Arrays.asList(gson.fromJson(json, RequestDictionary[].class));
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

    @SuppressLint("UseCompatLoadingForDrawables")
    class GetGISWaterTransfer implements ICallback<String> {
        @Override
        public void execute(String s) {
            try {
                CustomArcGISJSON customArcGISJSON = ConvertArcToGeo.convertStringToCustomArcGISJSON(s);
                try {
                    CustomGeoJSON customGeoJSON = ConvertArcToGeo.convertPolygon(customArcGISJSON, "Polygon");
                    KmlDocument kmlDocument = new KmlDocument();
                    String json = ConvertArcToGeo.convertCustomGeoJSONToString(customGeoJSON);
                    if (json != null) {
                        try {
                            kmlDocument.parseGeoJSON(json);
                            MyKmlStyle.color = 3;
                            FolderOverlay geoJsonOverlay = (FolderOverlay) kmlDocument.mKmlRoot.buildOverlay(
                                    binding.mapView, null, new MyKmlStyle(), kmlDocument);
                            geoJsonOverlays[2] = geoJsonOverlay;
                            binding.checkboxWaterTransfer.setVisibility(View.VISIBLE);
                            binding.linearLayoutAttribute.setVisibility(View.VISIBLE);
                        } catch (Exception e) {
                            Log.e("Error 3", e.toString());
                        }
                    }
                } catch (Exception e) {
                    Log.e("Error 2", e.toString());
                }
            } catch (Exception e) {
                Log.e("Error 1", e.toString());
            }
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    class GetGISWaterPipe implements ICallback<String> {
        @Override
        public void execute(String s) {
            try {
                CustomArcGISJSON customArcGISJSON = ConvertArcToGeo.convertStringToCustomArcGISJSON(s);
                try {
                    CustomGeoJSON customGeoJSON = ConvertArcToGeo.convertPolygon(customArcGISJSON, "Polygon");
                    KmlDocument kmlDocument = new KmlDocument();
                    String json = ConvertArcToGeo.convertCustomGeoJSONToString(customGeoJSON);
                    if (json != null) {
                        try {
                            kmlDocument.parseGeoJSON(json);
                            MyKmlStyle.color = 2;
                            FolderOverlay geoJsonOverlay = (FolderOverlay) kmlDocument.mKmlRoot.buildOverlay(
                                    binding.mapView, null, new MyKmlStyle(), kmlDocument);

                            geoJsonOverlays[1] = geoJsonOverlay;
                            binding.checkboxWaterPipe.setVisibility(View.VISIBLE);
                            binding.linearLayoutAttribute.setVisibility(View.VISIBLE);
                        } catch (Exception e) {
                            Log.e("Error 3", e.toString());
                        }
                    }
                } catch (Exception e) {
                    Log.e("Error 2", e.toString());
                }
            } catch (Exception e) {
                Log.e("Error 1", e.toString());
            }
//            binding.progressBar.setVisibility(View.GONE);
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    class GetGISSanitationTransfer implements ICallback<String> {
        @Override
        public void execute(String s) {
            try {
                CustomArcGISJSON customArcGISJSON = ConvertArcToGeo.convertStringToCustomArcGISJSON(s);
                try {
                    CustomGeoJSON customGeoJSON = ConvertArcToGeo.convertPolygon(customArcGISJSON, "Polygon");
                    KmlDocument kmlDocument = new KmlDocument();
                    String json = ConvertArcToGeo.convertCustomGeoJSONToString(customGeoJSON);
                    if (json != null) {
                        try {
                            kmlDocument.parseGeoJSON(json);
                            MyKmlStyle.color = 4;
                            FolderOverlay geoJsonOverlay = (FolderOverlay) kmlDocument.mKmlRoot.buildOverlay(
                                    binding.mapView, null, new MyKmlStyle(), kmlDocument);
                            geoJsonOverlays[3] = geoJsonOverlay;
                            binding.checkboxSanitationTransfer.setVisibility(View.VISIBLE);
                            binding.linearLayoutAttribute.setVisibility(View.VISIBLE);
                        } catch (Exception e) {
                            Log.e("Error 3", e.toString());
                        }
                    }
                } catch (Exception e) {
                    Log.e("Error 2", e.toString());
                }
            } catch (Exception e) {
                Log.e("Error 1", e.toString());
            }
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    class GetGISParcels implements ICallback<String> {
        @Override
        public void execute(String s) {
            try {
                CustomArcGISJSON customArcGISJSON = ConvertArcToGeo.convertStringToCustomArcGISJSON(s);
                try {
                    CustomGeoJSON customGeoJSON = ConvertArcToGeo.convertPolygon(customArcGISJSON, "Polygon");
                    KmlDocument kmlDocument = new KmlDocument();
                    String json = ConvertArcToGeo.convertCustomGeoJSONToString(customGeoJSON);
                    if (json != null) {
                        try {
                            kmlDocument.parseGeoJSON(json);
                            MyKmlStyle.color = 1;
                            FolderOverlay geoJsonOverlay = (FolderOverlay) kmlDocument.mKmlRoot.buildOverlay(
                                    binding.mapView, null, new MyKmlStyle(), kmlDocument);
                            geoJsonOverlays[0] = geoJsonOverlay;
                            binding.checkboxParcels.setVisibility(View.VISIBLE);
                            binding.linearLayoutAttribute.setVisibility(View.VISIBLE);
                        } catch (Exception e) {
                            Log.e("Error 3", e.toString());
                        }
                    }
                } catch (Exception e) {
                    Log.e("Error 2", e.toString());
                }
            } catch (Exception e) {
                Log.e("Error 1", e.toString());
            }
            binding.progressBar.setVisibility(View.GONE);
        }
    }

    class GetGISIncomplete implements ICallbackIncomplete<String> {
        @Override
        public void executeIncomplete(Response<String> response) {
            binding.progressBar.setVisibility(View.GONE);
            if (response.errorBody() != null) {
                Log.e("Error GetGISIncomplete", response.errorBody().toString());
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
            } else {
                latLong = new double[2];
                gpsTracker.getLocation();
                latLong[0] = gpsTracker.getLatitude();
                latLong[1] = gpsTracker.getLongitude();
            }
            calculationUserInput.y1 = latLong[0];
            calculationUserInput.x1 = latLong[1];
            addUserPlace(new GeoPoint(latLong[0], latLong[1]));
            getGISToken();
        }
    }

    class GetXYIncomplete implements ICallbackIncomplete<Place> {
        @Override
        public void executeIncomplete(Response<Place> response) {
            binding.progressBar.setVisibility(View.GONE);
            if (response.errorBody() != null) {
                Log.e("GetXYIncomplete", response.errorBody().toString());
            }
        }
    }

    class GetGISTokenIncomplete implements ICallbackIncomplete<GISToken> {
        @Override
        public void executeIncomplete(Response<GISToken> response) {
            binding.progressBar.setVisibility(View.GONE);
            if (response.errorBody() != null) {
                Log.e("GetGISTokenIncomplete", response.errorBody().toString());
            }
        }
    }

    class GetError implements ICallbackError {
        @Override
        public void executeError(Throwable t) {
            binding.progressBar.setVisibility(View.GONE);
            Log.e("GetError", Objects.requireNonNull(t.getMessage()));
        }
    }

    class GetGISToken implements ICallback<GISToken> {
        @Override
        public void execute(GISToken gisToken) {
            token = gisToken.getToken();
            if (latLong != null) {
                geoJsonOverlays = new FolderOverlay[4];
                indexes = new int[4];
                getGis(0);
                getGis(1);
                getGis(2);
                getGis(3);
            } else {
                binding.linearLayoutAttribute.setVisibility(View.GONE);
                binding.progressBar.setVisibility(View.GONE);
            }
        }
    }

    @SuppressLint("StaticFieldLeak")
    class GetDBData extends AsyncTask<Integer, String, String> {
        ProgressDialog dialog;

        public GetDBData() {
            super();
        }

        @Override
        protected String doInBackground(Integer... integers) {
            arzeshdaraei = new Arzeshdaraei();

            dataBase = Room.databaseBuilder(context, MyDatabase.class, MyApplication.getDBNAME())
                    .allowMainThreadQueries().build();
            DaoExaminerDuties daoExaminerDuties = dataBase.daoExaminerDuties();
            examinerDuties = daoExaminerDuties.unreadExaminerDutiesByTrackNumber(trackNumber);
            secondForm = new SecondForm(examinerDuties.getFaseleKhakiA(),
                    examinerDuties.getFaseleKhakiF(), examinerDuties.getFaseleAsphaultA(),
                    examinerDuties.getFaseleAsphaultF(), examinerDuties.getFaseleSangA(),
                    examinerDuties.getFaseleSangF(), examinerDuties.getFaseleOtherA(),
                    examinerDuties.getFaseleOtherF(), examinerDuties.getQotrLooleS(),
                    examinerDuties.getJensLooleS(), examinerDuties.getNoeMasrafI(),
                    examinerDuties.getNoeMasrafS(), examinerDuties.isVaziatNasbPompI(),
                    examinerDuties.getOmqeZirzamin(), examinerDuties.isEtesalZirzamin(),
                    examinerDuties.getOmqeZirzamin(), examinerDuties.isChahAbBaran(),
                    examinerDuties.isEzhaNazarA(), examinerDuties.isEzhaNazarF(),
                    examinerDuties.getQotrLooleI(), examinerDuties.getJensLooleI(),
                    examinerDuties.isLooleA(), examinerDuties.isLooleF(),
                    examinerDuties.getMasrafDescription(), examinerDuties.getChahDescription(),
                    examinerDuties.getEshterak()
            );
            DaoTejariha daoTejariha = dataBase.daoTejariha();
            List<Tejariha> othersTemp = daoTejariha.getTejarihaByTrackNumber(
                    examinerDuties.getTrackNumber());
            others.addAll(othersTemp);
            DaoFormula daoFormula = dataBase.daoFormula();
            DaoBlock daoBlock = dataBase.daoBlock();
            DaoZarib daoZarib = dataBase.daoZarib();
            List<Formula> formulas = daoFormula.getFormulaByZoneId(
                    Integer.parseInt(examinerDuties.getZoneId()));
            List<Block> blocks = daoBlock.getBlockByZoneId(
                    Integer.parseInt(examinerDuties.getZoneId()));
            List<Zarib> zaribs = daoZarib.getZaribByZoneId(
                    Integer.parseInt(examinerDuties.getZoneId()));
            if (formulas != null && formulas.size() > 0 && blocks != null && blocks.size() > 0
                    && zaribs != null && zaribs.size() > 0) {
                arzeshdaraei = new Arzeshdaraei(blocks, formulas, zaribs);
            }
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
            initializeMap(false);
            dialog.dismiss();
        }
    }
}
