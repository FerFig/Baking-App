package com.ferfig.bakingapp.ui.fragment;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;

import com.ferfig.bakingapp.R;
import com.ferfig.bakingapp.model.entity.Recip;
import com.ferfig.bakingapp.model.entity.Step;
import com.ferfig.bakingapp.utils.Utils;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class VideoPartFragment extends Fragment {

    Context mContext;
    SimpleExoPlayer mExoPlyer;

    @BindView(R.id.exoplayer_view)
    SimpleExoPlayerView mExoPlyerView;

    Unbinder mBkUnbinder;

    public VideoPartFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_video_player, container, false);
        mBkUnbinder = ButterKnife.bind(this, rootView);
        mContext = getContext();
        Bundle stepData = getArguments();
        if (stepData != null && stepData.containsKey(Utils.CURRENT_STEP_OBJECT)){
            Step currentStep = stepData.getParcelable(Utils.CURRENT_STEP_OBJECT);
            if (currentStep!=null) {
                initializeExoPlayer(currentStep);
            }
        }
        return rootView;
    }

    private void initializeExoPlayer(Step step) {
        if (step.getVideoURL().isEmpty()) {
            //TODO: deal with no video
        } else {
            Uri videoUri = Uri.parse(step.getVideoURL());

            if (step.getThumbnailURL().isEmpty()) {
                //TODO: deal with no thumbnail
            }else{
                Uri Thumb = Uri.parse(step.getThumbnailURL());
                //TODO: load in async task/background ?!
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(mContext.getContentResolver(), Thumb);
                    mExoPlyerView.setDefaultArtwork(bitmap);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (mExoPlyer == null) {
                //Handler mainHandler = new Handler();

                // New ExoPlayer Instance
                BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
                TrackSelection.Factory videoTrackSelectionFactory =
                        new AdaptiveTrackSelection.Factory(bandwidthMeter);
                TrackSelector trackSelector =
                        new DefaultTrackSelector(videoTrackSelectionFactory);

                mExoPlyer = ExoPlayerFactory.newSimpleInstance(mContext, trackSelector);
            }

            // Load the default controller
            mExoPlyerView.setUseController(true);
            mExoPlyerView.requestFocus();
            mExoPlyerView.setPlayer(mExoPlyer);

            // Measures bandwidth during playback. Can be null if not required.
            DefaultBandwidthMeter defaultBandwidthMeter = new DefaultBandwidthMeter();

            // Produces DataSource instances through which media data is loaded.
            DataSource.Factory dataSourceFactory = new DefaultDataSourceFactory(
                    mContext,
                    Util.getUserAgent(mContext, Utils.APP_TAG),
                    defaultBandwidthMeter);

            // This is the MediaSource representing the media to be played.
            MediaSource videoSource = new ExtractorMediaSource.Factory(dataSourceFactory)
                    .createMediaSource(videoUri);

            mExoPlyer.prepare(videoSource);
            mExoPlyer.setPlayWhenReady(true);
        }
    }

    private void releaseExoPlayer(){
        if (mExoPlyer != null) {
            mExoPlyer.stop();
            mExoPlyer.release();
            mExoPlyer = null;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBkUnbinder.unbind();
        releaseExoPlayer();
    }
}
