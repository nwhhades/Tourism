package com.whiner.common.base;

import android.content.res.Resources;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.viewbinding.ViewBinding;

import com.blankj.utilcode.util.AdaptScreenUtils;
import com.blankj.utilcode.util.SPUtils;
import com.blankj.utilcode.util.StringUtils;
import com.bumptech.glide.Glide;
import com.hjq.permissions.XXPermissions;
import com.hjq.toast.Toaster;
import com.whiner.common.R;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseActivity<V extends ViewBinding> extends AppCompatActivity {

    private static final String TAG = "BaseActivity";
    protected static final String SP_KEY_ACTIVITY_BG = "SP_KEY_ACTIVITY_BG";
    protected V binding;

    protected abstract V getBinding();

    protected abstract boolean enableBg();

    protected void setBgUrl(String url) {
        SPUtils.getInstance().put(SP_KEY_ACTIVITY_BG, url);
    }

    protected String getBgUrl() {
        return SPUtils.getInstance().getString(SP_KEY_ACTIVITY_BG, "");
    }

    protected ImageView createBg(@NonNull String url) {
        ImageView imageView = new ImageView(this);
        imageView.setId(R.id.iv_main_bg);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        Glide.with(imageView).load(url).placeholder(R.color.window_bg).into(imageView);
        return imageView;
    }

    protected void addBgToActivity() {
        String url = getBgUrl();
        if (StringUtils.isEmpty(url)) {
            Log.d(TAG, "addBgToActivity: url is null !");
            return;
        }
        if (binding.getRoot() instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) binding.getRoot();
            ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            viewGroup.addView(createBg(url), 0, layoutParams);
        }
    }

    protected View loadingView;
    protected View oldFocusView;

    protected abstract boolean enableLoadingView();

    protected View createLoadingView() {
        Log.d(TAG, "createLoadingView: 创建默认LoadingView");
        FrameLayout view = new FrameLayout(this);
        view.setId(R.id.fl_loading_view);
        view.setBackgroundResource(R.color.tr_black_50);
        view.setClickable(true);
        view.setFocusable(true);
        view.setFocusableInTouchMode(true);
        view.setVisibility(View.INVISIBLE);
        view.setNextFocusLeftId(R.id.fl_loading_view);
        view.setNextFocusRightId(R.id.fl_loading_view);
        view.setNextFocusUpId(R.id.fl_loading_view);
        view.setNextFocusDownId(R.id.fl_loading_view);
        view.setOnClickListener(v -> {
            Log.d(TAG, "onClick: 用户点击了LoadingView");
            actionClickLoadingView();
        });
        view.setOnKeyListener((v, keyCode, event) -> {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                switch (keyCode) {
                    case KeyEvent.KEYCODE_BACK:
                    case KeyEvent.KEYCODE_DPAD_LEFT:
                    case KeyEvent.KEYCODE_DPAD_RIGHT:
                    case KeyEvent.KEYCODE_DPAD_UP:
                    case KeyEvent.KEYCODE_DPAD_DOWN:
                        Log.d(TAG, "onClick: 用户按拦截键了LoadingView");
                        actionClickLoadingView();
                        return true;
                    default:
                        break;
                }
            }
            return false;
        });
        //添加子View
        ProgressBar child_view = new ProgressBar(new ContextThemeWrapper(this, R.style.LoadingViewProgressBar), null);
        FrameLayout.LayoutParams child_view_layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        child_view_layoutParams.gravity = Gravity.CENTER;
        view.addView(child_view, child_view_layoutParams);
        return view;
    }

    protected void addLoadingViewToActivity(View view) {
        loadingView = view;
        if (view != null) {
            if (binding.getRoot() instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) binding.getRoot();
                ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
                viewGroup.addView(view, layoutParams);
            }
        }
    }

    protected void delLoadingViewToActivity(View view) {
        if (view != null) {
            if (binding.getRoot() instanceof ViewGroup) {
                ViewGroup viewGroup = (ViewGroup) binding.getRoot();
                viewGroup.removeView(view);
            }
        }
    }

    protected void setLoadingView(View view) {
        delLoadingViewToActivity(loadingView);
        addLoadingViewToActivity(view);
    }

    protected void showLoadingView(String msg) {
        Log.d(TAG, "showLoadingView: show");
        oldFocusView = getCurrentFocus();
        if (loadingView != null) {
            loadingView.setTag(msg);
            loadingView.setVisibility(View.VISIBLE);
            loadingView.bringToFront();
            loadingView.requestFocus();
        }
    }

    protected void hideLoadingView() {
        Log.d(TAG, "hideLoadingView: hide");
        if (loadingView != null) {
            loadingView.setTag(null);
            loadingView.setVisibility(View.INVISIBLE);
        }
        if (oldFocusView != null) {
            oldFocusView.requestFocus();
        }
    }

    protected void actionClickLoadingView() {
        if (loadingView != null) {
            String msg = getString(R.string.base_activity_loading_view_tip);
            Object o = loadingView.getTag();
            if (o instanceof String) {
                msg = o.toString();
            }
            Toaster.show(msg);
        }
    }

    protected void preInit() {
        Log.d(TAG, "preInit: " + this);
    }

    protected abstract void init();

    @Override
    public Resources getResources() {
        return AdaptScreenUtils.adaptWidth(super.getResources(), 3840);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        preInit();
        Log.d(TAG, "onCreate: " + this);
        super.onCreate(savedInstanceState);
        binding = getBinding();
        //判断是否添加背景
        if (enableBg()) {
            Log.d(TAG, "onCreate: 启用全局背景");
            addBgToActivity();
        }
        //判断是否添加LoadingView
        if (enableLoadingView()) {
            Log.d(TAG, "onCreate: 启用LoadingView");
            setLoadingView(createLoadingView());
        }
        setContentView(binding.getRoot());
        initPermission();
        if (waitPermissionInit()) {
            return;
        }
        init();
    }

    @Override
    protected void onStart() {
        Log.d(TAG, "onStart: " + this);
        super.onStart();
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "onResume: " + this);
        super.onResume();
    }

    @Override
    protected void onPause() {
        Log.d(TAG, "onPause: " + this);
        super.onPause();
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "onStop: " + this);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy: " + this);
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        Log.d(TAG, "onBackPressed: " + this);
        super.onBackPressed();
    }

    protected abstract boolean waitPermissionInit();

    protected abstract List<String> requestPermissionList();

    protected void permissionSuccess() {
        Log.d(TAG, "permissionSuccess: 权限正常");
    }

    protected void permissionFail() {
        Log.d(TAG, "permissionFail: 权限异常");
    }

    protected void initPermission() {
        if (requestPermissionList() == null) {
            return;
        }
        //默认需要网络权限
        List<String> list = new ArrayList<>();
        list.add("android.permission.INTERNET");
        list.addAll(requestPermissionList());
        XXPermissions.with(this).permission(list).request((permissions, allGranted) -> {
            Log.d(TAG, "initPermission: " + permissions);
            if (allGranted) {
                permissionSuccess();
                if (waitPermissionInit()) {
                    init();
                }
            } else {
                permissionFail();
            }
        });
    }

}
