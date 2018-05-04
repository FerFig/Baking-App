package com.ferfig.bakingapp.ui;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ferfig.bakingapp.R;
import com.ferfig.bakingapp.api.HttpRecipsClient;
import com.ferfig.bakingapp.model.dao.RecipDao;
import com.ferfig.bakingapp.model.database.BakingAppDB;
import com.ferfig.bakingapp.model.entity.Ingredient;
import com.ferfig.bakingapp.model.entity.Recip;
import com.ferfig.bakingapp.model.entity.Step;
import com.ferfig.bakingapp.ui.adapter.MainActivityRecipesAdapter;
import com.ferfig.bakingapp.utils.Utils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private static final Integer INGREDIENTS_STEP_ID = 927;
    private static final String OS_LINE_SEPARATOR = System.getProperty("line.separator");
    private static final String EMPTY_STRING = "";

    static List<Recip> mRecipList;

    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.rvMainRecyclerView)
    RecyclerView rvMainRecyclerView;

    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.pbProgress)
    ProgressBar pbProgressBar;

    @SuppressWarnings("WeakerAccess")
    @BindView(R.id.tvErrorMessage)
    TextView tvErrorMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        //Always prepare the recycler view LayoutManager
        rvMainRecyclerView.setLayoutManager(setupMainLayoutManager());

        getRecipsData(savedInstanceState);
    }

    private LinearLayoutManager setupMainLayoutManager(){
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,
                OrientationHelper.VERTICAL,false);
//        //Add a divider
//        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
//                rvMainRecyclerView.getContext(),
//                linearLayoutManager.getOrientation());
//        rvMainRecyclerView.addItemDecoration(dividerItemDecoration);
        return linearLayoutManager;
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mRecipList != null) {
            outState.putParcelableArrayList(Utils.ALL_RECIPS_SAVED_INSTANCE,
                    new ArrayList<Parcelable>(mRecipList));
        }
        if (rvMainRecyclerView!=null && rvMainRecyclerView.getLayoutManager() != null) {
            outState.putParcelable(Utils.RECLYCLER_SAVED_INSTANCE,
                    rvMainRecyclerView.getLayoutManager().onSaveInstanceState());
        }
        super.onSaveInstanceState(outState);
    }

    private void getRecipsData(Bundle savedInstanceState){
        if (savedInstanceState != null){
            //restore recipes list
            if (savedInstanceState.containsKey(Utils.ALL_RECIPS_SAVED_INSTANCE)) {
                mRecipList = savedInstanceState.getParcelableArrayList(Utils.ALL_RECIPS_SAVED_INSTANCE);
            }
            //restore recycler view state
            if (savedInstanceState.containsKey(Utils.RECLYCLER_SAVED_INSTANCE)) {
                Parcelable savedRecyclerState = savedInstanceState.getParcelable(Utils.RECLYCLER_SAVED_INSTANCE);
                rvMainRecyclerView.getLayoutManager().onRestoreInstanceState(savedRecyclerState);
            }
            //set the adapter
            setMainRecipAdapter(mRecipList);
            //set the gui visible objects
            pbProgressBar.setVisibility(View.GONE);
            rvMainRecyclerView.setVisibility(View.VISIBLE);
            tvErrorMessage.setVisibility(View.GONE);
        }
        else{
            if (Utils.isInternetConectionAvailable(this)){
                Retrofit.Builder retrofitBuilder = new Retrofit.Builder()
                        .baseUrl(Utils.RECIPS_URL_DATA)
                        .addConverterFactory(GsonConverterFactory.create());
                Retrofit retrofit = retrofitBuilder.build();

                HttpRecipsClient client = retrofit.create(HttpRecipsClient.class);
                Call<List<Recip>> recipsCall =  client.getRecips();
                recipsCall.enqueue(new Callback<List<Recip>>() {
                    @Override
                    public void onResponse(@NonNull Call<List<Recip>> call, @NonNull Response<List<Recip>> response) {
                        if (response.code() == 200) {
                            mRecipList = response.body();

                            setMainRecipAdapter(mRecipList);

                            saveToDatabase(new WeakReference<>(getApplicationContext()));

                            pbProgressBar.setVisibility(View.GONE);
                            rvMainRecyclerView.setVisibility(View.VISIBLE);
                            tvErrorMessage.setVisibility(View.GONE);
                        }
                        else
                        {
                            pbProgressBar.setVisibility(View.GONE);
                            rvMainRecyclerView.setVisibility(View.GONE);
                            tvErrorMessage.setVisibility(View.VISIBLE);
                            tvErrorMessage.setText(R.string.error_failed_response);
                        }
                    }

                    @Override
                    public void onFailure(@NonNull Call<List<Recip>> call, @NonNull Throwable t) {
                        pbProgressBar.setVisibility(View.GONE);
                        rvMainRecyclerView.setVisibility(View.GONE);
                        tvErrorMessage.setVisibility(View.VISIBLE);
                        tvErrorMessage.setText(R.string.error_failed_network_request);
                    }
                });

            }else{
                //No network connectivity :(
                pbProgressBar.setVisibility(View.GONE);
                rvMainRecyclerView.setVisibility(View.GONE);
                tvErrorMessage.setVisibility(View.VISIBLE);
                tvErrorMessage.setText(R.string.error_no_network);
            }

        }
    }

    private static void saveToDatabase(final WeakReference<Context> weakContext) {
        new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] objects) {
                if (objects.length == 1) {
                    List<Recip> mRecipes = (List<Recip>) objects[0];
                    BakingAppDB bakingAppDB = BakingAppDB.getInstance(weakContext.get());
                    RecipDao recipDao = bakingAppDB.recipDao();
                    recipDao.insertAll(mRecipes);
                    bakingAppDB.close();
                }
                return null;
            }
        }.execute(mRecipList);
    }

    private void setMainRecipAdapter(List<Recip> recipList) {
        MainActivityRecipesAdapter rvAdapter = new MainActivityRecipesAdapter(this,
                recipList,
                new MainActivityRecipesAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(Recip recipData) {

                        //Add ingredients as first step :)
                        addIngredientsStep(recipData);

                        Intent intent = new Intent(getApplicationContext(), RecipeDetailsActivity.class);
                        intent.putExtra(Utils.RECIPE_DATA_OBJECT, recipData);
                        startActivity(intent);
                    }
                });

        rvMainRecyclerView.setAdapter(rvAdapter);
    }

    /** Add Ingredients as first step (if not already added) */
    private void addIngredientsStep(Recip recipData) {
        if ( !recipData.getSteps().get(0).getId().equals(INGREDIENTS_STEP_ID) ) {
            Step ingredientsStep = new Step();
            ingredientsStep.setId(INGREDIENTS_STEP_ID);
            ingredientsStep.setShortDescription(getString(R.string.IngredientsLabel));
            ingredientsStep.setThumbnailURL(EMPTY_STRING);
            ingredientsStep.setVideoURL(EMPTY_STRING);
            StringBuilder allIngredients = new StringBuilder();
            for (Ingredient ingredient : recipData.getIngredients()) {
                allIngredients.append(Utils.formatQuantity(ingredient.getQuantity()));
                allIngredients.append(ingredient.getMeasure());
                allIngredients.append(getString(R.string.Quantity_Ingredient_Separator));
                allIngredients.append(ingredient.getIngredient());
                allIngredients.append(OS_LINE_SEPARATOR);
            }
            ingredientsStep.setDescription(allIngredients.toString());

            List<Step> allSteps = new ArrayList<>();
            allSteps.add(ingredientsStep);
            allSteps.addAll(recipData.getSteps());
            recipData.setSteps(allSteps);
        }
    }

}
