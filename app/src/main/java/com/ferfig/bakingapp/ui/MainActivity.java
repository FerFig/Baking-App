package com.ferfig.bakingapp.ui;

import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ferfig.bakingapp.R;
import com.ferfig.bakingapp.api.HttpRecipsClient;
import com.ferfig.bakingapp.model.Recip;
import com.ferfig.bakingapp.utils.Utils;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

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

    private void getRecipsData(Bundle savedInstanceState){
        if (savedInstanceState != null){
            List<Recip> recipList = savedInstanceState.getParcelableArrayList(Utils.ALL_RECIPS_SAVED_INSTANCE);

            Parcelable savedRecyclerState = savedInstanceState.getParcelable(Utils.RECLYCLER_SAVED_INSTANCE);
            rvMainRecyclerView.getLayoutManager().onRestoreInstanceState(savedRecyclerState);

            setMainRecipAdapter(recipList);
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
                            List<Recip> recipList = response.body();

                            //TODO save in database
                            //make the room action, as this is already in asyncall there is no need to force it
//                        RecipDao recipDao = BakingDatabase
//                                .getInstance(getContext())
//                                .getRecipsDao();
//
//                        recipDao.insert(mRecips);
                            setMainRecipAdapter(recipList);

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
        //TODO
        //rvMainRecyclerView.setAdapter(new RecyclerView.Adapter());
    }
}
