package com.ferfig.bakingapp.ui.widget;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ferfig.bakingapp.R;
import com.ferfig.bakingapp.model.dao.RecipDao;
import com.ferfig.bakingapp.model.database.BakingAppDB;
import com.ferfig.bakingapp.model.entity.Recip;
import com.ferfig.bakingapp.ui.adapter.MainActivityRecipesAdapter;
import com.ferfig.bakingapp.utils.Utils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * The configuration screen for the {@link BakingAppWidget BakingAppWidget} AppWidget.
 */
public class BakingAppWidgetConfigActivity extends Activity implements RecipesDbAsyncResponse {

    private static final String PREFS_NAME = "com.ferfig.bakingapp.BakingAppWidget.pref";
    private static final String PREF_RECIPE_NAME_PREFIX_KEY = "baking_widget_recipe_";
    private static final String PREF_INGREDIENTS_PREFIX_KEY = "baking_widget_ingredients_";

    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

    @BindView( R.id.tvSelectionTitle)
    TextView tvSelectionTitle;

    @BindView(R.id.rvRecipesSelection)
    RecyclerView rvRecipesSelection;

    @BindView(R.id.pbProgress)
    ProgressBar pbProgress;

    public BakingAppWidgetConfigActivity() {
        super();
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED);

        setContentView(R.layout.baking_app_widget_config);

        ButterKnife.bind(this);

        //Always prepare the recycler view LayoutManager
        rvRecipesSelection.setLayoutManager(new LinearLayoutManager(this,
                OrientationHelper.VERTICAL,false));

        //Load recipes from local DB
        new LoadRecipesFromLocalDB(this).execute(getApplicationContext());

        // Find the widget id from the intent.
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
        }
    }

    @Override
    public void processFinish(List<Recip> recipsList) {
        if (recipsList != null) {
            setRecipAdapter(recipsList);
            pbProgress.setVisibility(View.GONE);
            tvSelectionTitle.setVisibility(View.VISIBLE);
            rvRecipesSelection.setVisibility(View.VISIBLE);
        } else {
            Toast.makeText(getApplicationContext(), getString(R.string.widget_loading_error), Toast.LENGTH_LONG).show();
            finish();
        }
    }

    private static class LoadRecipesFromLocalDB extends AsyncTask<Context, Void, List<Recip>> {
        RecipesDbAsyncResponse asyncCallback;

        LoadRecipesFromLocalDB(RecipesDbAsyncResponse asyncCallback) {
            this.asyncCallback = asyncCallback;
        }

        @Override
        protected List<Recip> doInBackground(Context... params) {
            BakingAppDB bakingAppDB = BakingAppDB.getInstance(params[0]);
            RecipDao recipDao = bakingAppDB.recipDao();
            List<Recip> mRecipFromDB = recipDao.getAllRecips();
            bakingAppDB.close();
            return mRecipFromDB;
        }

        @Override
        protected void onPostExecute(List<Recip> recips) {
            asyncCallback.processFinish(recips);
        }
    }

    private void setRecipAdapter(List<Recip> recipList) {
        MainActivityRecipesAdapter rvAdapter = new MainActivityRecipesAdapter(this,
                recipList,
                new MainActivityRecipesAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(Recip recipData) {

                        //Select the desired recipe
                        selectRecipe(recipData.getName(),
                                Utils.formatIngredients(getApplicationContext(), recipData.getIngredients())
                        );
                    }
                });

        rvRecipesSelection.setAdapter(rvAdapter);
    }

    void selectRecipe(String name, String ingredients){
        final Context context = BakingAppWidgetConfigActivity.this;

        saveRecipePref(context, mAppWidgetId, name, ingredients);

        // It is the responsibility of the configuration activity to update the app widget
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        BakingAppWidget.updateAppWidget(context, appWidgetManager, mAppWidgetId);

        // Make sure we pass back the original appWidgetId
        Intent resultValue = new Intent();
        resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
        setResult(RESULT_OK, resultValue);
        finish();
    }

    // Write the selected recipe and ingredients to the SharedPreferences object for this widget
    static void saveRecipePref(Context context, int appWidgetId, String recipe_name, String ingredients) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putString(PREF_RECIPE_NAME_PREFIX_KEY + appWidgetId, recipe_name);
        prefs.putString(PREF_INGREDIENTS_PREFIX_KEY + appWidgetId, ingredients);
        prefs.apply();
    }

    // Read the prefix from the SharedPreferences object for this widget.
    // If there is no preference saved, get the default from a resource
    static String getRecipeName(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        String titleValue = prefs.getString(PREF_RECIPE_NAME_PREFIX_KEY + appWidgetId, null);
        if (titleValue != null) {
            return titleValue;
        } else {
            return context.getString(R.string.app_name);
        }
    }

    static CharSequence getIngredients(Context context, int appWidgetId) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        String ingredients = prefs.getString(PREF_INGREDIENTS_PREFIX_KEY + appWidgetId, null);
        if (ingredients != null) {
            return ingredients;
        } else {
            return context.getString(R.string.app_name);
        }
    }

    static void deleteRecipePref(Context context, int appWidgetId) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.remove(PREF_RECIPE_NAME_PREFIX_KEY + appWidgetId);
        prefs.remove(PREF_INGREDIENTS_PREFIX_KEY + appWidgetId);
        prefs.apply();
    }
}