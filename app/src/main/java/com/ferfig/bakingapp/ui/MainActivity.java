package com.ferfig.bakingapp.ui;

import android.content.Intent;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ferfig.bakingapp.R;
import com.ferfig.bakingapp.api.HttpRecipsClient;
import com.ferfig.bakingapp.model.Recip;
import com.ferfig.bakingapp.ui.adapter.MainActivityRecipesAdapter;
import com.ferfig.bakingapp.utils.Utils;

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
        //Add a divider
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                rvMainRecyclerView.getContext(),
                linearLayoutManager.getOrientation());
        rvMainRecyclerView.addItemDecoration(dividerItemDecoration);
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
                    public void onResponse(Call<List<Recip>> call, @NonNull Response<List<Recip>> response) {
                        if (response.code() == 200) {
                            mRecipList = response.body();

//TODO save in database here?!
                          //make the room action in asynctask or rxjava
//                        RecipDao recipDao = BakingDatabase
//                                .getInstance(getContext())
//                                .getRecipsDao();
//
//                        recipDao.insert(mRecips);
                            setMainRecipAdapter(mRecipList);

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
                    public void onFailure(Call<List<Recip>> call, Throwable t) {
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

    private void setMainRecipAdapter(List<Recip> recipList) {
        MainActivityRecipesAdapter rvAdapter = new MainActivityRecipesAdapter(this,
                recipList,
                new MainActivityRecipesAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(Recip recipData) {
                        Intent intent = new Intent(getApplicationContext(), RecipeDetailsActivity.class);
                        intent.putExtra(Utils.RECIPE_DATA_OBJECT, recipData);
                        startActivity(intent);
                    }
                });

        rvMainRecyclerView.setAdapter(rvAdapter);
    }
}
