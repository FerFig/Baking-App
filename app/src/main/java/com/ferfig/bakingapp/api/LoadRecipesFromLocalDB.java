package com.ferfig.bakingapp.api;

import android.content.Context;
import android.os.AsyncTask;

import com.ferfig.bakingapp.model.dao.RecipDao;
import com.ferfig.bakingapp.model.database.BakingAppDB;
import com.ferfig.bakingapp.model.entity.Recip;

import java.util.List;

public class LoadRecipesFromLocalDB extends AsyncTask<Context, Void, List<Recip>> {
    private BakingDbAsyncResponse asyncCallback;

    public LoadRecipesFromLocalDB(BakingDbAsyncResponse asyncCallback) {
        this.asyncCallback = asyncCallback;
    }

    @Override
    protected List<Recip> doInBackground(Context... params) {
        try {
            BakingAppDB bakingAppDB = BakingAppDB.getInstance(params[0]);
            RecipDao recipDao = bakingAppDB.recipDao();
            List<Recip> mRecipFromDB = recipDao.getAllRecips();
            bakingAppDB.close();
            return mRecipFromDB;
        }catch(Exception e){
            return null;
        }
    }

    @Override
    protected void onPostExecute(List<Recip> recips) {
        if ( recips == null ) {
            asyncCallback.recipesLoadFailed();
        }else {
            asyncCallback.recipesLoadSuccess(recips);
        }
    }
}

