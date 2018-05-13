package com.ferfig.bakingapp.ui.widget;

import com.ferfig.bakingapp.model.entity.Recip;

import java.util.List;

interface RecipesDbAsyncResponse {
    void processFinish(List<Recip> recipsList);
}
