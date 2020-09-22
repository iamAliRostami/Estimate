package com.leon.estimate.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import com.leon.estimate.Enums.BundleEnum;
import com.leon.estimate.Enums.ProgressType;
import com.leon.estimate.Enums.SharedReferenceKeys;
import com.leon.estimate.Enums.SharedReferenceNames;
import com.leon.estimate.Infrastructure.IAbfaService;
import com.leon.estimate.Infrastructure.ICallback;
import com.leon.estimate.Infrastructure.ICallbackError;
import com.leon.estimate.Infrastructure.ICallbackIncomplete;
import com.leon.estimate.R;
import com.leon.estimate.Tables.Arzeshdaraei;
import com.leon.estimate.Utils.HttpClientWrapper;
import com.leon.estimate.Utils.NetworkHelper;
import com.leon.estimate.Utils.SharedPreferenceManager;
import com.leon.estimate.databinding.ValueFragmentBinding;

import org.jetbrains.annotations.NotNull;

import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

import static com.leon.estimate.activities.FormActivity.activity;


public class ValueFragment extends DialogFragment {
    ValueFragmentBinding binding;
    Context context;
    TextView textview;
    SharedPreferenceManager sharedPreferenceManager;
    private String zoneId;

    public ValueFragment() {
    }

    public static ValueFragment newInstance(String param1) {
        ValueFragment fragment = new ValueFragment();
        Bundle args = new Bundle();
        args.putString(BundleEnum.ZONE_ID.getValue(), param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            zoneId = getArguments().getString(BundleEnum.ZONE_ID.getValue());
        }
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = ValueFragmentBinding.inflate(inflater, container, false);
        initialize();
        return binding.getRoot();
    }

    void initialize() {
        context = getActivity();
        sharedPreferenceManager = new SharedPreferenceManager(context, SharedReferenceNames.ACCOUNT.getValue());
        getArzeshdaraei();
        textview = (TextView) activity.findViewById(R.id.textViewArzeshMelk);
    }

    void getArzeshdaraei() {
        Retrofit retrofit = NetworkHelper.getInstance(true, sharedPreferenceManager.getStringData(SharedReferenceKeys.TOKEN.getValue()));
        final IAbfaService arzeshdaraei = retrofit.create(IAbfaService.class);
        Call<Arzeshdaraei> call = arzeshdaraei.getArzeshDaraii(Integer.parseInt(zoneId));
        HttpClientWrapper.callHttpAsync(call, ProgressType.SHOW_CANCELABLE.getValue(), context,
                new GetArzeshdaraei(), new GetArzeshdaraeiIncomplete(), new GetError());

    }

    @Override
    public void onResume() {
        WindowManager.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        getDialog().getWindow().setAttributes(params);
        super.onResume();
    }

    class GetArzeshdaraei implements ICallback<Arzeshdaraei> {
        @Override
        public void execute(Arzeshdaraei arzeshdaraei) {

        }
    }

    class GetArzeshdaraeiIncomplete implements ICallbackIncomplete<Arzeshdaraei> {
        @Override
        public void executeIncomplete(Response<Arzeshdaraei> response) {

        }
    }

    class GetError implements ICallbackError {
        @Override
        public void executeError(Throwable t) {

        }
    }
}