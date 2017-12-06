package com.htlocation;

import android.Manifest;
import android.app.Application;
import android.content.Context;
import android.support.annotation.NonNull;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.blankj.utilcode.util.AppUtils;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.taobao.sophix.PatchStatus;
import com.taobao.sophix.SophixManager;
import com.taobao.sophix.listener.PatchLoadStatusListener;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.https.HttpsUtils;
import com.zhy.http.okhttp.log.LoggerInterceptor;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;

/**
 * Created by Administrator on 2017/11/8.
 */

public class App extends Application {
    public static App instance;
    public static String IMEI = "";

    @Override
    public void onCreate() {
        super.onCreate();
        initHttp();
        initHotfix();
        Fresco.initialize(this);
    }

    /***
     * 初始化网络框架
     */
    private void initHttp() {
        try {
            InputStream in = getAssets().open("qhwendangwang.com.crt");
            //   HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory(null, in, null);
            HttpsUtils.SSLParams sslParams = HttpsUtils.getSslSocketFactory(null, null, null);
            OkHttpClient okHttpClient = new OkHttpClient.Builder()
                    .addInterceptor(new LoggerInterceptor("TAG"))
                    .sslSocketFactory(sslParams.sSLSocketFactory, sslParams.trustManager)
                    .connectTimeout(20000L, TimeUnit.MILLISECONDS)
                    .cache(new Cache(instance.getExternalCacheDir(), 1024 * 1024 * 10))
                    .readTimeout(10000L, TimeUnit.MILLISECONDS)
                    .build();
            OkHttpUtils.initClient(okHttpClient);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

    }

    private void initHotfix() {
        String appVersion;
        try {
            appVersion = this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName;
        } catch (Exception e) {
            appVersion = "1.0.0";
        }


        SophixManager.getInstance().setContext(this)
                .setAppVersion(appVersion)
                .setAesKey(null)
                //.setAesKey("0123456789123456")
                .setEnableDebug(true)
                .setPatchLoadStatusStub(new PatchLoadStatusListener() {
                    @Override
                    public void onLoad(final int mode, final int code, final String info, final int handlePatchVersion) {
                        // 补丁加载回调通知
                        if (code == PatchStatus.CODE_LOAD_SUCCESS) {
                            Log.d("App", "表明补丁加载成功");
                            // 表明补丁加载成功
                            Log.d("App", "code:" + code);
                            Log.d("App", info);
                        } else if (code == PatchStatus.CODE_LOAD_RELAUNCH) {
                            Log.d("App", "需要重启");
                            // 表明新补丁生效需要重启. 开发者可提示用户或者强制重启;
                            // 建议: 用户可以监听进入后台事件, 然后应用自杀
                            Log.d("App", "code:" + code);
                            Log.d("App", info);
                        } else if (code == PatchStatus.CODE_LOAD_FAIL) {
                            // 内部引擎异常, 推荐此时清空本地补丁, 防止失败补丁重复加载
                            Log.d("App", "内部引擎异常, 推荐此时清空本地补丁, 防止失败补丁重复加载");
                            SophixManager.getInstance().cleanPatches();
                            Log.d("App", "code:" + code);
                            Log.d("App", info);
                        } else {
                            // 其它错误信息, 查看PatchStatus类说明
                            Log.d("App", "code:" + code);
                            Log.d("App", info);
                            Log.d("App", "其它错误信息, 查看PatchStatus类说明");
                        }
                    }
                }).initialize();

    }

}
