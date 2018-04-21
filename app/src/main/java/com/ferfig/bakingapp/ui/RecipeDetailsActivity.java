package com.ferfig.bakingapp.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.ferfig.bakingapp.R;
import com.ferfig.bakingapp.model.Recip;
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
