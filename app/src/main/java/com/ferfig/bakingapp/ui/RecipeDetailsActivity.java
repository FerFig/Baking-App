package com.ferfig.bakingapp.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.ferfig.bakingapp.R;
import com.ferfig.bakingapp.model.entity.Recip;
import com.ferfig.bakingapp.model.entity.Step;
import com.ferfig.bakingapp.ui.fragment.DetailActivityFragment;
import com.ferfig.bakingapp.utils.Utils;


public class RecipeDetailsActivity extends AppCompatActivity implements DetailActivityFragment.OnStepClickedListener{

    private static Recip mRecipeDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_details);

        if (savedInstanceState == null) {
            Intent receivedIntent = getIntent();
            if (receivedIntent != null && receivedIntent.hasExtra(Utils.RECIPE_DATA_OBJECT)) {
                mRecipeDetails = receivedIntent.getParcelableExtra(Utils.RECIPE_DATA_OBJECT);
//TODO retrieve recip from DB
//                new AsyncTask() {
//                    @Override
//                    protected Boolean doInBackground(Object[] objects) {
//                        Boolean b = false;
//                        if (objects.length == 1) {
//                            Integer recipId = (Integer) objects[0];
//                            BakingAppDB bakingAppDB = BakingAppDB.getInstance(getApplicationContext());
//                            RecipDao recipDao = bakingAppDB.recipDao();
//                            Recip mRecipFromDB;
//                            mRecipFromDB = recipDao.getRecipById(recipId);
//                            if (mRecipeDetails.getId() == mRecipFromDB.getId()){
//                                b = true;
//                            }else {
//                                b = false;
//                            }
//                        }
//                        return b;
//                    }
//                }.execute(mRecipeDetails.getId());

            }else{//is not supposed too...
                Toast.makeText(getApplicationContext(), R.string.ImplementationErrorMessage, Toast.LENGTH_SHORT).show();
                finish();
            }
        }else{
            mRecipeDetails = savedInstanceState.getParcelable(Utils.RECIPE_DATA_OBJECT);
        }

        if (mRecipeDetails != null) this.setTitle(mRecipeDetails.getName());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(Utils.RECIPE_DATA_OBJECT, mRecipeDetails);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onStepSelected(Step step) {
        Toast.makeText(this, "Step clicked: " + step.getShortDescription(), Toast.LENGTH_SHORT).show();
    }
}
