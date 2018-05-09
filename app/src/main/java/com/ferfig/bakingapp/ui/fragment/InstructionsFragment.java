package com.ferfig.bakingapp.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.ferfig.bakingapp.R;
import com.ferfig.bakingapp.model.entity.Step;
import com.ferfig.bakingapp.utils.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class InstructionsFragment extends Fragment {
    static Step sCurrentStep;
    Context mContext;

    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.tvStepName)
    TextView tvStepName;

    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.tvStepDescription)
    TextView tvStepDescription;

    public InstructionsFragment() {
        //Mandatory constructor for fragment instantiation
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_step_instructions, container, false);

        ButterKnife.bind(this, rootView);

        mContext = getContext();
        if (savedInstanceState == null) {
            Bundle stepData = getArguments();
            if (stepData != null && stepData.containsKey(Utils.CURRENT_STEP_OBJECT)) {
                sCurrentStep = stepData.getParcelable(Utils.CURRENT_STEP_OBJECT);
            }
        }else{
            sCurrentStep = savedInstanceState.getParcelable(Utils.CURRENT_STEP_OBJECT);
        }

        tvStepName.setText(sCurrentStep.getShortDescription());
        tvStepDescription.setText(sCurrentStep.getDescription());
        return rootView;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelable(Utils.CURRENT_STEP_OBJECT, sCurrentStep);
        super.onSaveInstanceState(outState);
    }
}
