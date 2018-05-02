package com.ferfig.bakingapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.ferfig.bakingapp.R;
import com.ferfig.bakingapp.model.entity.Recip;
import com.ferfig.bakingapp.model.entity.Step;
import com.ferfig.bakingapp.ui.fragment.VideoPartFragment;
import com.ferfig.bakingapp.utils.Utils;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StepDetailsActivity extends AppCompatActivity {

    private static Recip mRecipeDetails;
    private static Step mCurrentStep;

    @BindView(R.id.tvStepName)
    TextView tvStepName;

    @BindView(R.id.tvStepDescription)
    TextView tvStepDescription;

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
                mRecipeDetails = receivedIntent.getParcelableExtra(Utils.RECIPE_DATA_OBJECT);
                mCurrentStep = receivedIntent.getParcelableExtra(Utils.CURRENT_STEP_OBJECT);
            }else{//is not supposed too...
                Toast.makeText(getApplicationContext(), R.string.ImplementationErrorMessage, Toast.LENGTH_SHORT).show();
                finish();
            }
        }else{
            mRecipeDetails = savedInstanceState.getParcelable(Utils.RECIPE_DATA_OBJECT);
            mCurrentStep = savedInstanceState.getParcelable(Utils.CURRENT_STEP_OBJECT);
        }

        if (mRecipeDetails != null) this.setTitle(mRecipeDetails.getName());
        if (mCurrentStep!= null) tvStepName.setText(mCurrentStep.getShortDescription());

        stepsNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        showCurrentStepUI(true);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(Utils.RECIPE_DATA_OBJECT, mRecipeDetails);
        outState.putParcelable(Utils.CURRENT_STEP_OBJECT,  mCurrentStep);
        super.onSaveInstanceState(outState);
    }

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.prev_step:
                    for (int i = 0; i < mRecipeDetails.getSteps().size(); i++) {
                        if (mRecipeDetails.getSteps().get(i).getId().equals(mCurrentStep.getId())) {
                            mCurrentStep = mRecipeDetails.getSteps().get(i-1);
                            break;
                        }
                    }
                    break;
                case R.id.next_step:
                    for (int i = 0; i < mRecipeDetails.getSteps().size(); i++) {
                        if (mRecipeDetails.getSteps().get(i).getId().equals(mCurrentStep.getId())) {
                            mCurrentStep = mRecipeDetails.getSteps().get(i+1);
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
        tvStepName.setText(mCurrentStep.getShortDescription());

        MenuItem bnPrevButton = stepsNavigationView.getMenu().getItem(0);
        bnPrevButton.setEnabled( !mCurrentStep.getId().equals(mRecipeDetails.getSteps()
                .get(0).getId()) /* Is not first step */);
        MenuItem bnNextButton = stepsNavigationView.getMenu().getItem(1);
        bnNextButton.setEnabled ( !mCurrentStep.getId().equals(mRecipeDetails.getSteps()
                .get(mRecipeDetails.getSteps().size()-1).getId()) /* Is not last step */ );

        // Replace fragments to display new step information
        VideoPartFragment videoPartFragment = createVideoFragment();

        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fromCreate) {
            fragmentManager.beginTransaction()
                    .add(R.id.recipe_step_video_layout, videoPartFragment)
                    .commit();
        }else{
            fragmentManager.beginTransaction()
                    .replace(R.id.recipe_step_video_layout, videoPartFragment)
                    .commit();
        }

        tvStepDescription.setText(mCurrentStep.getDescription());
    }

    private VideoPartFragment createVideoFragment() {
        VideoPartFragment videoPartFragment = new VideoPartFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(Utils.RECIPE_DATA_OBJECT, mRecipeDetails);
        bundle.putParcelable(Utils.CURRENT_STEP_OBJECT, mCurrentStep);
        videoPartFragment.setArguments(bundle);
        return videoPartFragment;
    }

    public void showInFullscreen() {
        this.getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }
}
