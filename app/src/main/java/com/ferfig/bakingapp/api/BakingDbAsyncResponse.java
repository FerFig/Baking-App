package com.ferfig.bakingapp.api;

import com.ferfig.bakingapp.model.entity.Recip;

import java.util.List;

public interface BakingDbAsyncResponse {
    void recipesLoadSuccess(List<Recip> recipsList);
    void recipesLoadFailed();
}
