package com.ferfig.bakingapp.ui.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;

import com.ferfig.bakingapp.model.entity.Step;

public class UiUtils {
    public static VideoPartFragment createVideoFragment(@NonNull Step step) {
        VideoPartFragment videoPartFragment = new VideoPartFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(com.ferfig.bakingapp.utils.Utils.CURRENT_STEP_OBJECT, step);
        videoPartFragment.setArguments(bundle);
        return videoPartFragment;
    }

    public static InstructionsFragment createInstructionsFragment(@NonNull Step step, int position) {
        InstructionsFragment instructionsFragment = new InstructionsFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable(com.ferfig.bakingapp.utils.Utils.CURRENT_STEP_OBJECT, step);
        bundle.putInt(com.ferfig.bakingapp.utils.Utils.SELECT_CURRENT_STEP, position);
        instructionsFragment.setArguments(bundle);
        return instructionsFragment;
    }

}
