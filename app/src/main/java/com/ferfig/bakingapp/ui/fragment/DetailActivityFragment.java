package com.ferfig.bakingapp.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ferfig.bakingapp.R;
import com.ferfig.bakingapp.model.entity.Recip;
import com.ferfig.bakingapp.model.entity.Step;
import com.ferfig.bakingapp.ui.StepDetailsActivity;
import com.ferfig.bakingapp.ui.adapter.RecipeDetailStepAdapter;
import com.ferfig.bakingapp.utils.Utils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailActivityFragment extends Fragment {

    private static Recip mRecipeDetails;
    private Context mContext;

    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.rvStepsRecyclerView)
    RecyclerView rvStepsRecyclerView;

    public DetailActivityFragment() {
        //Mandatory constructor for fragment instantiation
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_steps, container, false);

        ButterKnife.bind(this, rootView);

        mContext = getContext();

        //Always prepare the recycler view LayoutManager
        rvStepsRecyclerView.setLayoutManager(setupStepsLayoutManager());

        if (savedInstanceState == null) {
            Activity activity = getActivity();
            if (activity != null) {
                Intent receivedIntent = activity.getIntent();
                if (receivedIntent != null && receivedIntent.hasExtra(Utils.RECIPE_DATA_OBJECT)) {
                    mRecipeDetails = receivedIntent.getParcelableExtra(Utils.RECIPE_DATA_OBJECT);
                }
            }
        }else{
            mRecipeDetails = savedInstanceState.getParcelable(Utils.RECIPE_DATA_OBJECT);
        }

        if (mRecipeDetails != null) {
            setStepsAdapter(mRecipeDetails.getSteps());
        }
        return  rootView;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelable(Utils.RECIPE_DATA_OBJECT, mRecipeDetails);

        super.onSaveInstanceState(outState);
    }

    private void setStepsAdapter(List<Step> stepsList) {
        RecipeDetailStepAdapter rvAdapter = new RecipeDetailStepAdapter(mContext,
                stepsList,
                new RecipeDetailStepAdapter.OnItemClickListener() {

                    @Override
                    public void onItemClick(Step stepData) {
                        //notify the parent activity of the clicked item
                        mCallback.onStepSelected(stepData);
                    }
                });

        rvStepsRecyclerView.setAdapter(rvAdapter);
    }

    private LinearLayoutManager setupStepsLayoutManager(){
        return new LinearLayoutManager(mContext,
                OrientationHelper.VERTICAL,false);
    }

    // To communicate between fragments through the host activity ...
    OnStepClickedListener mCallback;
    public interface OnStepClickedListener{
        void onStepSelected(Step step);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        //make sure the host activity implement the callback to communicate between fragments :)
        try {
            mCallback = (OnStepClickedListener) context;
        }catch (ClassCastException e){
            throw new ClassCastException(context.toString() + "must implement OnStepClickedListener!");
        }
    }
}
