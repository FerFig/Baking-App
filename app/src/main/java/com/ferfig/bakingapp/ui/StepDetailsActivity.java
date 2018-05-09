package com.ferfig.bakingapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Toast;

import com.ferfig.bakingapp.R;
import com.ferfig.bakingapp.model.entity.Recip;
import com.ferfig.bakingapp.model.entity.Step;
import com.ferfig.bakingapp.ui.fragment.InstructionsFragment;
import com.ferfig.bakingapp.ui.fragment.UiUtils;
import com.ferfig.bakingapp.ui.fragment.VideoPartFragment;
import com.ferfig.bakingapp.utils.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StepDetailsActivity extends AppCompatActivity {

    private static Recip sRecipeDetails;
    private static Step sCurrentStep;

    @Nullable
    @BindView(R.id.navigation)
    BottomNavigationView stepsNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_details);

        ButterKnife.bind(this);

        if (savedInstanceState == null) {
            Intent receivedIntent = getIntent();
            if (receivedIntent != null && receivedIntent.hasExtra(Utils.RECIPE_DATA_OBJECT)
                    && receivedIntent.hasExtra(Utils.CURRENT_STEP_OBJECT)) {
                sRecipeDetails = receivedIntent.getParcelableExtra(Utils.RECIPE_DATA_OBJECT);
                sCurrentStep = receivedIntent.getParcelableExtra(Utils.CURRENT_STEP_OBJECT);
            }else{//is not supposed too...
                Toast.makeText(getApplicationContext(), R.string.ImplementationErrorMessage, Toast.LENGTH_SHORT).show();
                finish();
            }
        }else{
            sRecipeDetails = savedInstanceState.getParcelable(Utils.RECIPE_DATA_OBJECT);
            sCurrentStep = savedInstanceState.getParcelable(Utils.CURRENT_STEP_OBJECT);
        }

        this.setTitle(sRecipeDetails.getName());

        if  ( !Utils.isDeviceInLandscape(this) ) {
            if (stepsNavigationView != null)
                stepsNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        }

        showCurrentStepUI(true);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(Utils.RECIPE_DATA_OBJECT, sRecipeDetails);
        outState.putParcelable(Utils.CURRENT_STEP_OBJECT, sCurrentStep);
        super.onSaveInstanceState(outState);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.prev_step:
                    for (int i = 0; i < sRecipeDetails.getSteps().size(); i++) {
                        if (sRecipeDetails.getSteps().get(i).getId().equals(sCurrentStep.getId())) {
                            sCurrentStep = sRecipeDetails.getSteps().get(i-1);
                            break;
                        }
                    }
                    break;
                case R.id.next_step:
                    for (int i = 0; i < sRecipeDetails.getSteps().size(); i++) {
                        if (sRecipeDetails.getSteps().get(i).getId().equals(sCurrentStep.getId())) {
                            sCurrentStep = sRecipeDetails.getSteps().get(i+1);
                            break;
                        }
                    }
                    break;
            }

            showCurrentStepUI(false);
            return true;
        }
    };

    private void showCurrentStepUI(boolean fromCreate) {
        FragmentManager fragmentManager = getSupportFragmentManager();

        // Add/Replace fragments to display new step information
        VideoPartFragment videoPartFragment = UiUtils.createVideoFragment(sCurrentStep);
        if (fromCreate) {
            fragmentManager.beginTransaction()
                    .add(R.id.recipe_step_video_layout, videoPartFragment)
                    .commit();
        } else {
            fragmentManager.beginTransaction()
                    .replace(R.id.recipe_step_video_layout, videoPartFragment)
                    .commit();
        }

        if ( !Utils.isDeviceInLandscape(this) ) {
            // add instruction step details...
            InstructionsFragment instructionsFragment = UiUtils.createInstructionsFragment(sCurrentStep);
            if (fromCreate) {
                fragmentManager.beginTransaction()
                        .add(R.id.recipe_step_instructions_layout, instructionsFragment)
                        .commit();
            } else {
                fragmentManager.beginTransaction()
                        .replace(R.id.recipe_step_instructions_layout, instructionsFragment)
                        .commit();
            }
            //... and navigation options
            if (stepsNavigationView != null) {
                // display navigation options in portrait mode
                MenuItem bnPrevButton = stepsNavigationView.getMenu().getItem(0);
                bnPrevButton.setEnabled(!sCurrentStep.getId().equals(sRecipeDetails.getSteps()
                        .get(0).getId()) /* Is not first step */);
                MenuItem bnNextButton = stepsNavigationView.getMenu().getItem(1);
                bnNextButton.setEnabled(!sCurrentStep.getId().equals(sRecipeDetails.getSteps()
                        .get(sRecipeDetails.getSteps().size() - 1).getId()) /* Is not last step */);
            }
        }
    }

    public void showInFullscreen() {
        this.getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }
}
