package com.sun.music61.media;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import com.sun.music61.data.model.Track;
import com.sun.music61.screen.service.PlayTrackService;
import com.sun.music61.util.CommonUtils;
import java.io.IOException;

public class MediaPlayerManager implements Control {

    private static final String TAG = MediaPlayer.class.getName();
    private static MediaPlayerManager sInstance;

    private PlayTrackService mService;
    private Track mCurrentTrack;
    private MediaPlayer mMediaPlayer;
    @State
    private int mState;

    private MediaPlayerManager(PlayTrackService service) {
        mService = service;
        mState = State.PAUSE;
        mMediaPlayer = new MediaPlayer();
    }

    public static MediaPlayerManager getInstance(PlayTrackService service) {
        if (sInstance == null)
            sInstance = new MediaPlayerManager(service);
        return sInstance;
    }

    public Track getCurrentTrack() {
        return mCurrentTrack;
    }

    public void setCurrentTrack(Track currentTrack) {
        mCurrentTrack = currentTrack;
    }

    public MediaPlayer getMediaPlayer() {
        return mMediaPlayer;
    }

    public void setMediaPlayer(MediaPlayer mediaPlayer) {
        mMediaPlayer = mediaPlayer;
    }

    @Override
    public void create(Track track) {
        mMediaPlayer.reset();
        try {
            mMediaPlayer.setDataSource(mService, Uri.parse(track.getStreamUrl() + CommonUtils.AUTHORIZED_SERVER));
        } catch (IOException e) {
            // Do nothing
        }
        mMediaPlayer.setOnErrorListener(mService);
        mMediaPlayer.setOnCompletionListener(mService);
        mMediaPlayer.setOnPreparedListener(mService);
        mMediaPlayer.prepareAsync();
    }

    @Override
    public void start() {
        setState(State.PLAY);
        mMediaPlayer.start();
    }

    @Override
    public void change(Track track) {
        setState(State.PAUSE);
        mCurrentTrack = track;
        create(track);
    }

    @Override
    public void pause() {
        setState(State.PAUSE);
        mMediaPlayer.pause();
    }

    @Override
    public void stop() {
        mMediaPlayer.stop();
        setState(State.PAUSE);
    }

    @Override
    public void release() {
        mMediaPlayer.release();
    }

    @Override
    public void reset() {
        mMediaPlayer.reset();
    }

    @Override
    public void seek(int milliseconds) {
        mMediaPlayer.seekTo(milliseconds);
    }

    @Override
    public long getDuration() {
        return mMediaPlayer.getDuration();
    }

    @Override
    public long getCurrentDuration() {
        return mMediaPlayer.getCurrentPosition();
    }

    @Override
    public void setState(@State int state) {
        mState = state;
    }

    @State
    @Override
    public int getState() {
        return mState;
    }
}