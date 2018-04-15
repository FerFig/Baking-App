package com.ferfig.bakingapp.ui;

import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.OrientationHelper;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

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

        getRecipsData(savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        if (mRecipList != null) {
            outState.putParcelableArrayList(Utils.ALL_RECIPS_SAVED_INSTANCE,
                    new ArrayList<Parcelable>(mRecipList));
        }
//        outState.putParcelable(Utils.RECLYCLER_SAVED_INSTANCE,
//                rvMainRecyclerView.getLayoutManager().onSaveInstanceState());
        super.onSaveInstanceState(outState);
    }

    private void getRecipsData(Bundle savedInstanceState){
        if (savedInstanceState != null){
            mRecipList = savedInstanceState.getParcelableArrayList(Utils.ALL_RECIPS_SAVED_INSTANCE);
//            Parcelable savedRecyclerState = savedInstanceState.getParcelable(Utils.RECLYCLER_SAVED_INSTANCE);
//            rvMainRecyclerView.getLayoutManager().onRestoreInstanceState(savedRecyclerState);
            setMainRecipAdapter(mRecipList);

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
                    public void onResponse(Call<List<Recip>> call, Response<List<Recip>> response) {
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
                        //TODO prepare the intent to call detail activity/fragment
                        //And send it to the detail activity
                        Toast.makeText(getApplicationContext(),
                                "clicked: " + recipData.getName(),
                                Toast.LENGTH_SHORT).show();
                    }
                });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this,
                OrientationHelper.VERTICAL,false);

        //Add a divider
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(
                rvMainRecyclerView.getContext(),
                linearLayoutManager.getOrientation());
        rvMainRecyclerView.addItemDecoration(dividerItemDecoration);

        rvMainRecyclerView.setLayoutManager(linearLayoutManager);

        rvMainRecyclerView.setAdapter(rvAdapter);
    }

}
