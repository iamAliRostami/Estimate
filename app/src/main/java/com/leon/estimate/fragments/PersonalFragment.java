package com.leon.estimate.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.fragment.app.Fragment;

import com.google.gson.Gson;
import com.leon.estimate.Enums.BundleEnum;
import com.leon.estimate.R;
import com.leon.estimate.Tables.CalculationUserInput;
import com.leon.estimate.Tables.ExaminerDuties;
import com.leon.estimate.activities.FormActivity1;
import com.leon.estimate.databinding.PersonalFragmentBinding;

import org.jetbrains.annotations.NotNull;
import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.ArrayList;
import java.util.Objects;


public class PersonalFragment extends Fragment {
    private static final String ARG_PARAM2 = "param2";
    PersonalFragmentBinding binding;
    private double latitude, longitude;
    private LocationManager locationManager;
    private int polygonIndex, placeIndex;
    private MyLocationNewOverlay locationOverlay;
    private ArrayList<GeoPoint> polygonPoint = new ArrayList<>();
    //    private View findViewById;
    private MapView mapView = null;
    private Context context;
    //    private ExaminerDuties examinerDuties;
    private CalculationUserInput calculationUserInput;

    public PersonalFragment() {
    }

    public static PersonalFragment newInstance(ExaminerDuties examinerDuties, String param2) {
        PersonalFragment fragment = new PersonalFragment();
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
        ((FormActivity1) Objects.requireNonNull(getActivity())).setActionBarTitle(
                context.getString(R.string.app_name).concat(" / ").concat(context.getString(R.string.moshakhasat_malek)));
        Configuration.getInstance().load(context, PreferenceManager.getDefaultSharedPreferences(context));
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//        findViewById = inflater.inflate(R.layout.map_fragment, container, false);
        binding = PersonalFragmentBinding.inflate(inflater, container, false);
        initialize();
        return binding.getRoot();
    }

    private void initialize() {
//        binding.buttonNext.setOnClickListener(v -> {
////            mapView.setDrawingCacheEnabled(true);
////            Bitmap bitmap = mapView.getDrawingCache(true);
////            ((FormActivity) getActivity()).nextPage(bitmap);
//            if (prepareForm()) {
//                calculationUserInput = new CalculationUserInput();
//                calculationUserInput.nationalId = binding.editTextNationNumber.getText().toString();
//                calculationUserInput.firstName = binding.editTextName.getText().toString();
//                calculationUserInput.sureName = binding.editTextFamily.getText().toString();
//                calculationUserInput.fatherName = binding.editTextFatherName.getText().toString();
//                calculationUserInput.postalCode = binding.editTextPostalCode.getText().toString();
//                calculationUserInput.radif = binding.editTextRadif.getText().toString();
//                calculationUserInput.phoneNumber = binding.editTextPhone.getText().toString();
//                calculationUserInput.mobile = binding.editTextMobile.getText().toString();
//                calculationUserInput.address = binding.editTextAddress.getText().toString();
//                calculationUserInput.description = binding.editTextDescription.getText().toString();
//                ((FormActivity) Objects.requireNonNull(getActivity())).nextPage(
//                        convertMapToBitmap(), calculationUserInput);
//            }
//        });
        initializeField();
    }

    private Bitmap convertMapToBitmap() {
        mapView.setDrawingCacheEnabled(true);
        return mapView.getDrawingCache(true);
    }

    private boolean prepareForm() {
        return checkIsNoEmpty(binding.editTextShenasname)
                && checkIsNoEmpty(binding.editTextName)
                && checkIsNoEmpty(binding.editTextFamily)
                && checkIsNoEmpty(binding.editTextFatherName)
                && checkIsNoEmpty(binding.editTextEshterak)
                && checkIsNoEmpty(binding.editTextRadif)
                && checkIsNoEmpty(binding.editTextAddress)
                && checkOtherIsNoEmpty()
                ;
    }

    private boolean checkIsNoEmpty(EditText editText) {
        View focusView;
        if (editText.getText().toString().length() < 1) {
            focusView = editText;
            focusView.requestFocus();
            return false;
        }
        return true;
    }

    private boolean checkOtherIsNoEmpty() {
        View focusView;
        if (binding.editTextNationNumber.getText().toString().length() < 10) {
            focusView = binding.editTextNationNumber;
            focusView.requestFocus();
            return false;
        } else if (binding.editTextPostalCode.getText().toString().length() < 10) {
            focusView = binding.editTextPostalCode;
            focusView.requestFocus();
            return false;
        } else if (binding.editTextPhone.getText().toString().length() < 8) {
            focusView = binding.editTextPhone;
            focusView.requestFocus();
            return false;
        } else if (binding.editTextMobile.getText().toString().length() < 9) {
            focusView = binding.editTextMobile;
            focusView.requestFocus();
            return false;
        }
        return true;
    }

    private void initializeField() {
        binding.editTextAddress.setText(FormActivity1.examinerDuties.getAddress());
        binding.editTextName.setText(FormActivity1.examinerDuties.getFirstName());
        binding.editTextFamily.setText(FormActivity1.examinerDuties.getSureName());
        binding.editTextNationNumber.setText(FormActivity1.examinerDuties.getNationalId());
        binding.editTextFatherName.setText(FormActivity1.examinerDuties.getFatherName());
        binding.editTextDescription.setText(FormActivity1.examinerDuties.getDescription());
        binding.editTextPhone.setText(FormActivity1.examinerDuties.getPhoneNumber());
        binding.editTextMobile.setText(FormActivity1.examinerDuties.getMobile());
        binding.editTextEshterak.setText(FormActivity1.examinerDuties.getEshterak().trim());
        binding.editTextPostalCode.setText(FormActivity1.examinerDuties.getPostalCode());
        binding.editTextRadif.setText(FormActivity1.examinerDuties.getRadif());
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
