package com.ferfig.bakingapp.ui.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.media.session.MediaButtonReceiver;
import android.support.v4.media.session.MediaSessionCompat;
import android.support.v4.media.session.PlaybackStateCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.ferfig.bakingapp.R;
import com.ferfig.bakingapp.model.entity.Step;
import com.ferfig.bakingapp.utils.Utils;
import com.google.android.exoplayer2.ExoPlaybackException;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.PlaybackParameters;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.Timeline;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.BandwidthMeter;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class VideoPartFragment extends Fragment implements Player.EventListener {

    Context mContext;
    SimpleExoPlayer mExoPlayer;

    @BindView(R.id.exoplayer_view)
    PlayerView mExoPlayerView;

    Unbinder mBkUnbinder;

    static Step sCurrentStep;
    static Long sCurrentPlayerPosition;
    static boolean sPlayWhenReady = true;

    static MediaSessionCompat sMediaSession;
    static PlaybackStateCompat.Builder sPlaybackStateBuilder;

    public VideoPartFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_video_player, container, false);

        mBkUnbinder = ButterKnife.bind(this, rootView);

        mContext = getContext();

        if (savedInstanceState == null) {
            sCurrentPlayerPosition = 0L;
            sPlayWhenReady = true;
            Bundle stepData = getArguments();
            if (stepData != null && stepData.containsKey(Utils.CURRENT_STEP_OBJECT)) {
                sCurrentStep = stepData.getParcelable(Utils.CURRENT_STEP_OBJECT);
            }
        }else{
            sCurrentPlayerPosition = savedInstanceState.getLong(Utils.CURRENT_VIDEO_POSITION);
            sPlayWhenReady = savedInstanceState.getBoolean(Utils.PLAY_WHEN_READY);
            sCurrentStep = savedInstanceState.getParcelable(Utils.CURRENT_STEP_OBJECT);
        }

        mExoPlayerView.setDefaultArtwork(BitmapFactory.decodeResource(mContext.getResources(),
                R.drawable.ic_no_movie_24dp));

        return rootView;
    }

    private void initializeExoPlayer() {
        Log.i(Utils.APP_TAG, "initializeExoPlayer: executed");
        if (Util.SDK_INT < 16) return; //exoplayer needs sdk 16 or upper but I need the sdk 15 to test (at lease non video content) in my old tabled device :)

        if (sCurrentStep != null) {
            if (sCurrentStep.getVideoURL().isEmpty()) {
                if ( Utils.isDeviceInLandscape(mContext) && !Utils.isTwoPaneLayout(mContext) ) {
                    Toast.makeText(mContext, R.string.step_with_no_video, Toast.LENGTH_SHORT).show();
                    getActivity().finish();
                }else {
                    mExoPlayerView.setDefaultArtwork(BitmapFactory.decodeResource(mContext.getResources(),
                            R.drawable.ic_no_movie_24dp));
                }
            } else {
                Uri videoUri = Uri.parse(sCurrentStep.getVideoURL());

                if (sCurrentStep.getThumbnailURL().isEmpty()) {
                    mExoPlayerView.setDefaultArtwork(BitmapFactory.decodeResource(mContext.getResources(),
                            R.drawable.ic_no_movie_24dp));
                } else {
                    Uri thumbUri = Uri.parse(sCurrentStep.getThumbnailURL());
                    Picasso.get().load(thumbUri).into(new Target() {
                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                            mExoPlayerView.setDefaultArtwork(bitmap);
                        }

                        @Override
                        public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                            mExoPlayerView.setDefaultArtwork(BitmapFactory.decodeResource(mContext.getResources(),
                                    R.drawable.ic_no_movie_24dp));
                        }

                        @Override
                        public void onPrepareLoad(Drawable placeHolderDrawable) {

                        }
                    });
                    mExoPlayerView.setDefaultArtwork(BitmapFactory.decodeResource(mContext.getResources(),
                            R.drawable.ic_no_movie_24dp));

                }

                initializeMediaSession();

                if (mExoPlayer == null) {
                    // New ExoPlayer Instance
                    BandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
                    TrackSelection.Factory videoTrackSelectionFactory =
                            new AdaptiveTrackSelection.Factory(bandwidthMeter);
                    TrackSelector trackSelector =
                            new DefaultTrackSelector(videoTrackSelectionFactory);

                    mExoPlayer = ExoPlayerFactory.newSimpleInstance(mContext, trackSelector);
                }

                // Load the default controller
                mExoPlayerView.setUseController(true);
                mExoPlayerView.requestFocus();
                mExoPlayerView.setPlayer(mExoPlayer);

                // Set the ExoPlayer.EventListener to this
                mExoPlayer.addListener(this);

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

                mExoPlayer.prepare(videoSource);
                mExoPlayer.seekTo(sCurrentPlayerPosition);
                mExoPlayer.setPlayWhenReady(sPlayWhenReady);
            }
        }
    }
    private void initializeMediaSession() {

        Activity activity = getActivity();
        if ( activity != null ) {
            // Create a MediaSessionCompat.
            if (sMediaSession == null) {
                sMediaSession = new MediaSessionCompat(activity, Utils.APP_TAG);

                // Enable callbacks from MediaButtons and TransportControls.
                sMediaSession.setFlags(
                        MediaSessionCompat.FLAG_HANDLES_MEDIA_BUTTONS |
                                MediaSessionCompat.FLAG_HANDLES_TRANSPORT_CONTROLS);

                // Do not let MediaButtons restart the player when the app is not visible.
                sMediaSession.setMediaButtonReceiver(null);

                // Set an initial PlaybackState with ACTION_PLAY, so media buttons can start the player.
                sPlaybackStateBuilder = new PlaybackStateCompat.Builder()
                        .setActions(
                                PlaybackStateCompat.ACTION_PLAY |
                                        PlaybackStateCompat.ACTION_PAUSE |
                                        PlaybackStateCompat.ACTION_SKIP_TO_PREVIOUS |
                                        PlaybackStateCompat.ACTION_PLAY_PAUSE |
                                        PlaybackStateCompat.ACTION_FAST_FORWARD |
                                        PlaybackStateCompat.ACTION_REWIND);

                sMediaSession.setPlaybackState(sPlaybackStateBuilder.build());

                // mediaPlayerSessionCallback has methods that handle callbacks from a media controller.
                sMediaSession.setCallback(new mediaPlayerSessionCallback());
            }

            // Start the Media Session since the activity is active.
            sMediaSession.setActive(true);
        }
    }

    private void releaseExoPlayer(){
        Log.i(Utils.APP_TAG, "releaseExoPlayer: executed");
        if (mExoPlayer != null) {
            // Store the position case we come back to this video after screen configurations changes...
            sCurrentPlayerPosition = mExoPlayer.getCurrentPosition();
            sPlayWhenReady = mExoPlayer.getPlayWhenReady();

            mExoPlayer.stop();
            mExoPlayer.release();
            mExoPlayer = null;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.i(Utils.APP_TAG, "onStart: executed");
        if (Util.SDK_INT > 23) {
            initializeExoPlayer();
        }
    }

    @Override
    public void onPause(){
        super.onPause();
        Log.i(Utils.APP_TAG, "onPause: executed");
        if (Util.SDK_INT <= 23) {
            releaseExoPlayer();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(Utils.APP_TAG, "onResume: executed");
        toggleFullScreenIfNeeded();
        if (Util.SDK_INT <= 23 /*|| mExoPlayer == null*/) {
            initializeExoPlayer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i(Utils.APP_TAG, "onStop: executed");
        if (Util.SDK_INT > 23) {
            releaseExoPlayer();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.i(Utils.APP_TAG, "onDestroyView: executed");
        releaseExoPlayer();
        if (mBkUnbinder != null) {
            mBkUnbinder.unbind();
        }
        if (sMediaSession != null) {
            sMediaSession.setActive(false);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putParcelable(Utils.CURRENT_STEP_OBJECT, sCurrentStep);
        outState.putBoolean(Utils.PLAY_WHEN_READY, sPlayWhenReady);
        outState.putLong(Utils.CURRENT_VIDEO_POSITION, sCurrentPlayerPosition);
        super.onSaveInstanceState(outState);
    }

    private void toggleFullScreenIfNeeded() {
        if (Utils.isDeviceInLandscape(mContext) && !Utils.isTwoPaneLayout(mContext)) {
            Activity activity = getActivity();
            Window window;
            if (activity != null) {
                window = activity.getWindow();
                if (window != null) {
                    View decorView;
                    decorView = window.getDecorView();
                    if (decorView != null) {
                        int newUiOptions = decorView.getSystemUiVisibility();
//                        boolean flagAlreadySet =
//                                ((newUiOptions | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY) == newUiOptions);
                        // Navigation bar hiding:  Backwards compatible to ICS.
                        if (Build.VERSION.SDK_INT >= 14) {
                            newUiOptions |= View.SYSTEM_UI_FLAG_HIDE_NAVIGATION;
                        }
                        // Status bar hiding: Backwards compatible to Jellybean
                        if (Build.VERSION.SDK_INT >= 16) {
                            newUiOptions |= View.SYSTEM_UI_FLAG_FULLSCREEN;
                        }
//                        // Immersive mode: Backward compatible to KitKat. not needed for our purpose...
//                        if (Build.VERSION.SDK_INT >= 18) {
//                            newUiOptions ^= View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
//                        }

                        // Set the content to appear under the system bars so that the
                        // content doesn't resize when the system bars hide and show.
                        if (Build.VERSION.SDK_INT >= 16) {
                            newUiOptions |= View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
                            newUiOptions |= View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION;
                            newUiOptions |= View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN;
                        }

                        decorView.setSystemUiVisibility(newUiOptions);
                        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                                WindowManager.LayoutParams.FLAG_FULLSCREEN);
                    }
                }
            }
        }
    }

    /* BEGIN ExoPlayer Event Listeners */
    @Override
    public void onTimelineChanged(Timeline timeline, Object manifest, int reason) {

    }

    @Override
    public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {

    }

    @Override
    public void onLoadingChanged(boolean isLoading) {

    }

    @Override
    public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
        if((playbackState == Player.STATE_READY) && playWhenReady){
            sPlaybackStateBuilder.setState(PlaybackStateCompat.STATE_PLAYING,
                    mExoPlayer.getCurrentPosition(), 1f);
        } else if((playbackState == Player.STATE_READY)){
            sPlaybackStateBuilder.setState(PlaybackStateCompat.STATE_PAUSED,
                    mExoPlayer.getCurrentPosition(), 1f);
        }
        sMediaSession.setPlaybackState(sPlaybackStateBuilder.build());
    }

    @Override
    public void onRepeatModeChanged(int repeatMode) {

    }

    @Override
    public void onShuffleModeEnabledChanged(boolean shuffleModeEnabled) {

    }

    @Override
    public void onPlayerError(ExoPlaybackException error) {
        Log.d(Utils.APP_TAG, "Exo Player Error: " + error.getMessage());
    }

    @Override
    public void onPositionDiscontinuity(int reason) {

    }

    @Override
    public void onPlaybackParametersChanged(PlaybackParameters playbackParameters) {

    }

    @Override
    public void onSeekProcessed() {

    }
    /* FINISH ExoPlayer Event Listeners */

    /* Media Session Callbacks, where all external clients control the player. */
    private class mediaPlayerSessionCallback extends MediaSessionCompat.Callback {
        @Override
        public void onPlay() {
            mExoPlayer.setPlayWhenReady(true);
            sPlayWhenReady = true;
        }

        @Override
        public void onPause() {
            mExoPlayer.setPlayWhenReady(false);
            sPlayWhenReady = false;
        }

        @Override
        public void onSkipToPrevious() {
            mExoPlayer.seekTo(0);
        }

        @Override
        public void onFastForward() {
            mExoPlayer.seekTo(Utils.MEDIA_FF_SEEK_TIME_MS);
        }

        @Override
        public void onRewind() {
            mExoPlayer.seekTo(Utils.MEDIA_RW_SEEK_TIME_MS);
        }
    }

    public static class MediaReceiver extends BroadcastReceiver {

        public MediaReceiver(){
        }

        @Override
        public void onReceive(Context context, Intent intent) {
            MediaButtonReceiver.handleIntent(sMediaSession, intent);
        }
    }
}
