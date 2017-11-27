package com.htlocation;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.https.HttpsUtils;
import com.zhy.http.okhttp.log.LoggerInterceptor;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.OkHttpClient;

/**
 * Created by Administrator on 2017/11/8.
 */

public class App extends Application {
    public static App instance;
    public static String IMEI="";
    @Override
    public void onCreate() {
        super.onCreate();
        initHttp();
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
}
