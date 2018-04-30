package com.ferfig.bakingapp.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.ferfig.bakingapp.R;
import com.ferfig.bakingapp.model.entity.Recip;
import com.ferfig.bakingapp.utils.Utils;

import butterknife.ButterKnife;

public class RecipeDetailsActivity extends AppCompatActivity {

    private static Recip mRecipeDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe_details);

        ButterKnife.bind(this);

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
}
