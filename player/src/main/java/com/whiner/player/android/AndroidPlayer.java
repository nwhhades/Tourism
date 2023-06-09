package com.whiner.player.android;

import android.content.Context;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.VideoView;

import com.whiner.player.impl.BasePlayerView;

public class AndroidPlayer extends BasePlayerView {

    private static final String TAG = "AndroidPlayer";
    private boolean mLooping = false;
    private MyVideoView mView;
    private MediaPlayer mMediaPlayer;

    @Override
    public View getView(Context context) {
        if (mView == null) {
            mView = new MyVideoView(context);
            mView.setClickable(false);
            mView.setFocusable(false);
            mView.setFocusableInTouchMode(false);
        }
        return mView;
    }

    @Override
    public void addListener() {
        if (mView != null) {
            mView.setOnErrorListener((mp, what, extra) -> {
                setErrState(new Exception("err :" + extra));
                return true;
            });
            mView.setOnPreparedListener(mp -> mMediaPlayer = mp);
            mView.setOnCompletionListener(mp -> {
                Log.d(TAG, "onCompletion: 播放结束");
                mp.seekTo(0);
                if (mLooping) {
                    mp.start();
                } else {
                    setCompletion();
                }
            });
            mView.setOnInfoListener((mp, what, extra) -> {
                switch (what) {
                    case MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START:
                        setPlaySate(PlayState.STATE_PLAYING);
                        break;
                    case MediaPlayer.MEDIA_INFO_BUFFERING_START:
                        setPlaySate(PlayState.STATE_BUFFERING);
                        break;
                    case MediaPlayer.MEDIA_INFO_BUFFERING_END:
                        setPlaySate(PlayState.STATE_BUFFERED);
                        break;
                    default:
                        break;
                }
                return true;
            });
        }
    }

    @Override
    public void delListener() {
        if (mView != null) {
            mView.setOnErrorListener((mp, what, extra) -> true);
            mView.setOnPreparedListener(null);
            mView.setOnCompletionListener(null);
            mView.setOnInfoListener(null);
        }
    }

    @Override
    public void releaseView() {
        release();
        mMediaPlayer = null;
        mView = null;
    }

    @Override
    public void replay() {
        if (mView != null) {
            mView.resume();
            mView.start();
        }
    }

    @Override
    public void start() {
        if (mView != null) {
            mView.setVideoURI(Uri.parse(getUrl()));
            mView.start();
            setPlaySate(PlayState.STATE_PLAYING);
        }
    }

    @Override
    public void resume() {
        if (mView != null && !mView.isPlaying()) {
            mView.start();
            setPlaySate(PlayState.STATE_PLAYING);
        }
    }

    @Override
    public void pause() {
        if (mView != null) {
            mView.pause();
            setPlaySate(PlayState.STATE_PAUSED);
        }
    }

    @Override
    public void release() {
        if (mView != null) {
            mView.suspend();
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
    public void setLooping(boolean looping) {
        mLooping = looping;
    }

    @Override
    public void onMute() {
        if (mMediaPlayer != null) {
            mMediaPlayer.setVolume(0f, 0f);
        }
    }

    @Override
    public void onUnMute() {
        if (mMediaPlayer != null) {
            mMediaPlayer.setVolume(1f, 1f);
        }
    }

    @Override
    public void seekTo(long ms) {
        if (mView != null && mView.getDuration() > ms) {
            mView.seekTo((int) ms);
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

    private static final class MyVideoView extends VideoView {

        public MyVideoView(Context context) {
            super(context);
        }

        public MyVideoView(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public MyVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
            super(context, attrs, defStyleAttr);
        }

        @Override
        public void setClickable(boolean clickable) {
            super.setClickable(false);
        }

        @Override
        public void setFocusable(boolean focusable) {
            super.setFocusable(false);
        }

        @Override
        public void setFocusableInTouchMode(boolean focusableInTouchMode) {
            super.setFocusableInTouchMode(false);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            //我们重新计算高度
            int width = getDefaultSize(0, widthMeasureSpec);
            int height = getDefaultSize(0, heightMeasureSpec);
            setMeasuredDimension(width, height);
        }

    }

}
