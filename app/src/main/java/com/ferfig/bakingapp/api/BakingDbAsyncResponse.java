package com.ferfig.bakingapp.api;

import com.ferfig.bakingapp.model.entity.Recip;

import java.util.List;
/** Created by FerFig on @17/05/2018 */

public interface BakingDbAsyncResponse {
    void recipesLoadSuccess(List<Recip> recipsList);
    void recipesLoadFailed();
}
