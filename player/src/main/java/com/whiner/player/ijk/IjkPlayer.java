package com.whiner.player.ijk;

import android.content.Context;
import android.view.View;

import com.whiner.player.impl.BasePlayerView;

import xyz.doikki.videoplayer.player.BaseVideoView;

public class IjkPlayer extends BasePlayerView implements BaseVideoView.OnStateChangeListener {

    private IjkVideoView mView;

    @Override
    public View getView(Context context) {
        if (mView == null) {
            mView = new IjkVideoView(context);
            mView.setClickable(false);
            mView.setFocusable(false);
            mView.setFocusableInTouchMode(false);
        }
        return mView;
    }

    @Override
    public void addListener() {
        if (mView != null) {
            mView.addOnStateChangeListener(this);
        }
    }

    @Override
    public void delListener() {
        if (mView != null) {
            mView.removeOnStateChangeListener(this);
        }
    }

    @Override
    public void releaseView() {
        release();
        mView = null;
    }

    @Override
    public void replay() {
        if (mView != null) {
            mView.replay(true);
        }
    }

    @Override
    public void start() {
        if (mView != null) {
            mView.setUrl(getUrl());
            mView.start();
        }
    }

    @Override
    public void resume() {
        if (mView != null) {
            mView.resume();
        }
    }

    @Override
    public void pause() {
        if (mView != null) {
            mView.pause();
        }
    }

    @Override
    public void release() {
        if (mView != null) {
            mView.release();
        }
    }

    @Override
    public void seekTo(long ms) {
        if (mView != null && mView.getDuration() > ms) {
            mView.seekTo(ms);
        }
    }

    @Override
    public void setLooping(boolean looping) {
        if (mView != null) {
            mView.setLooping(looping);
        }
    }

    @Override
    public boolean isPlaying() {
        if (mView != null) {
            return mView.isPlaying();
        }
        return false;
    }

    @Override
    public void onMute() {
        if (mView != null) {
            mView.setVolume(0f, 0f);
        }
    }

    @Override
    public void onUnMute() {
        if (mView != null) {
            mView.setVolume(1f, 1f);
        }
    }

    @Override
    public long getCurrentPosition() {
        if (mView != null) {
            return mView.getCurrentPosition();
        }
        return 0;
    }

    @Override
    public long getDuration() {
        if (mView != null) {
            return mView.getDuration();
        }
        return 0;
    }

    @Override
    public void onPlayerStateChanged(int playerState) {

    }

    @Override
    public void onPlayStateChanged(int playState) {
        switch (playState) {
            case BaseVideoView.STATE_ERROR:
                setErrState(new Exception("播放错误"));
                break;
            case BaseVideoView.STATE_PLAYING:
                setPlaySate(PlayState.STATE_PLAYING);
                break;
            case BaseVideoView.STATE_PAUSED:
                setPlaySate(PlayState.STATE_PAUSED);
                break;
            case BaseVideoView.STATE_PLAYBACK_COMPLETED:
                setCompletion();
                break;
            case BaseVideoView.STATE_BUFFERING:
                setPlaySate(PlayState.STATE_BUFFERING);
                break;
            case BaseVideoView.STATE_BUFFERED:
                setPlaySate(PlayState.STATE_BUFFERED);
                break;
            default:
                break;
        }
    }

}
