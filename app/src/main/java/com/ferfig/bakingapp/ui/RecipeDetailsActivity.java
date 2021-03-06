package com.ferfig.bakingapp.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.ferfig.bakingapp.R;
import com.ferfig.bakingapp.model.entity.Recip;
import com.ferfig.bakingapp.model.entity.Step;
import com.ferfig.bakingapp.ui.fragment.DetailActivityFragment;
import com.ferfig.bakingapp.ui.fragment.InstructionsFragment;
import com.ferfig.bakingapp.ui.fragment.UiUtils;
import com.ferfig.bakingapp.ui.fragment.VideoPartFragment;
import com.ferfig.bakingapp.utils.Utils;

public class RecipeDetailsActivity extends AppCompatActivity implements DetailActivityFragment.OnStepClickedListener{

    private static Recip mRecipeDetails;
    private static int mSelectedStep;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_details);

        if (savedInstanceState == null) {
            Intent receivedIntent = getIntent();
            if (receivedIntent != null && receivedIntent.hasExtra(com.ferfig.bakingapp.utils.Utils.RECIPE_DATA_OBJECT)) {
                mRecipeDetails = receivedIntent.getParcelableExtra(com.ferfig.bakingapp.utils.Utils.RECIPE_DATA_OBJECT);
                mSelectedStep = receivedIntent.getIntExtra(Utils.SELECT_CURRENT_STEP, 0);

            }else{//is not supposed too...
                Toast.makeText(getApplicationContext(), R.string.ImplementationErrorMessage, Toast.LENGTH_SHORT).show();
                finish();
            }
        }else{
            mRecipeDetails = savedInstanceState.getParcelable(Utils.RECIPE_DATA_OBJECT);
            mSelectedStep = savedInstanceState.getInt(Utils.SELECT_CURRENT_STEP);
        }

        if (mRecipeDetails != null) this.setTitle(mRecipeDetails.getName());

        if (Utils.isTwoPaneLayout(this)){
            Step step = mRecipeDetails.getSteps().get(mSelectedStep);

            setupFragments(step, true);
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(Utils.RECIPE_DATA_OBJECT, mRecipeDetails);
        outState.putInt(Utils.SELECT_CURRENT_STEP, mSelectedStep);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onStepSelected(Step step, int position) {
        if (Utils.isTwoPaneLayout(this)) {
            mSelectedStep = position;

            setupFragments(step, false);

        }else{
            Intent intent = new Intent(this, StepDetailsActivity.class);
            intent.putExtra(Utils.RECIPE_DATA_OBJECT, mRecipeDetails);
            intent.putExtra(Utils.CURRENT_STEP_OBJECT, step);
            startActivity(intent);
        }
    }

    private void setupFragments(Step step, boolean fromCreate) {
        FragmentManager fragmentManager = getSupportFragmentManager();

        // Replace fragments to display in tablet instead of in new activity

        boolean isNewVideoFragment = false;
        VideoPartFragment videoPartFragment = (VideoPartFragment)fragmentManager.
                findFragmentByTag(Utils.getFragmentTag(step, Utils.FragmentType.VIDEO));
        if (videoPartFragment == null) {
            videoPartFragment = UiUtils.createVideoFragment(step);
            isNewVideoFragment = true;
        }
        if (fromCreate && isNewVideoFragment){
            fragmentManager.beginTransaction()
                    .add(R.id.recipe_step_video_layout, videoPartFragment,
                            Utils.getFragmentTag(step, Utils.FragmentType.VIDEO))
                    .commit();
        }else{
            fragmentManager.beginTransaction()
                    .replace(R.id.recipe_step_video_layout, videoPartFragment,
                            Utils.getFragmentTag(step, Utils.FragmentType.VIDEO))
                    .commit();
        }

        boolean isNewInstructions = false;
        InstructionsFragment instructionsFragment = (InstructionsFragment)fragmentManager.
                findFragmentByTag(Utils.getFragmentTag(step, Utils.FragmentType.INSTRUCTIONS));
        if (instructionsFragment == null) {
            instructionsFragment = UiUtils.createInstructionsFragment(step, mSelectedStep);
            isNewInstructions = true;
        }
        if (fromCreate && isNewInstructions){
            fragmentManager.beginTransaction()
                    .add(R.id.recipe_step_instructions_layout, instructionsFragment,
                            Utils.getFragmentTag(step, Utils.FragmentType.INSTRUCTIONS))
                    .commit();
        }else{
            fragmentManager.beginTransaction()
                    .replace(R.id.recipe_step_instructions_layout, instructionsFragment,
                            Utils.getFragmentTag(step, Utils.FragmentType.INSTRUCTIONS))
                    .commit();
        }
    }
}
