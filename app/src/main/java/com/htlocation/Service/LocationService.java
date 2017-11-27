package com.htlocation.Service;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.htlocation.App;
import com.htlocation.MainActivity;
import com.htlocation.R;
import com.htlocation.UrlUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.MediaType;

/**
 * Created by Administrator on 2017/11/8.
 */

public class LocationService extends Service {

    private Notification.Builder builder;
    private Notification notification;
    private LocationManager locationManager;
    private NotificationManager notificationManager;
    private Location mLocation;
    private boolean isRun;
    private Handler handler = new Handler();
    private long TIME = 10000;
    private String addressStr;
    Runnable runnable = new Runnable() {
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void run() {
            if (isRun) {
                handler.postDelayed(this, TIME);
                sendMassege(mLocation);
            }
        }

    };

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        isRun = true;
        super.onCreate();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        handler.postDelayed(runnable, TIME);
        /***
         * 通知栏的初始化
         */
        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        builder = new Notification.Builder(this);
        Intent nfIntent = new Intent(this, MainActivity.class);
        builder
                .setContentIntent(PendingIntent.getActivity(this, 0, nfIntent, 0))// 设置PendingIntent
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), R.mipmap.ic_launcher))
                .setContentTitle("位置信息") // 设置下拉列表里的标题
                .setSmallIcon(R.mipmap.ic_launcher) // 设置状态栏内的小图标
                .setContentText("获取中...") // 设置上下文内容
                .setWhen(System.currentTimeMillis()); // 设置该通知发生的时间
        notification = builder.build(); // 获取构建好的Notification
        notification.defaults = Notification.DEFAULT_SOUND; //设置为默认的声音
        startForeground(1100, notification);// 开始前台服务
        getLngAndLat(LocationService.this);
        return super.onStartCommand(intent, flags, startId);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopForeground(true);// 停止前台服务--参数：表示是否移除之前的通知
        isRun = false;
    }


    /**
     * 获取经纬度
     *
     * @param context
     * @return
     */
    private String getLngAndLat(Context context) {

        double latitude = 0.0;
        double longitude = 0.0;
        locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {  //从gps获取经纬度
            Location location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location != null) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
                mLocation = location;
            } else {//当GPS信号弱没获取到位置的时候又从网络获取
                return getLngAndLatWithNetwork();
            }
        } else {    //从网络获取经纬度
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.

            }
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 0, locationListener);
            Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (location != null) {
                latitude = location.getLatitude();
                longitude = location.getLongitude();
            }
        }
        return longitude + "," + latitude;
    }

    //从网络获取经纬度
    public String getLngAndLatWithNetwork() {
        double latitude = 0.0;
        double longitude = 0.0;
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

        }
        locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 5000, 0, locationListener);
        Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        if (location != null) {
            latitude = location.getLatitude();
            longitude = location.getLongitude();
            mLocation = location;
        }
        return longitude + "," + latitude;
    }


    LocationListener locationListener = new LocationListener() {

        // Provider的状态在可用、暂时不可用和无服务三个状态直接切换时触发此函数
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        // Provider被enable时触发此函数，比如GPS被打开
        @Override
        public void onProviderEnabled(String provider) {

        }

        // Provider被disable时触发此函数，比如GPS被关闭
        @Override
        public void onProviderDisabled(String provider) {
            Toast.makeText(LocationService.this, "GPS关闭", Toast.LENGTH_SHORT).show();
        }

        //当坐标改变时触发此函数，如果Provider传进相同的坐标，它就不会被触发
        @Override
        public void onLocationChanged(Location location) {
            mLocation = location;
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void showloction(Location location) {

        float lat = (float) location.getLatitude();//未读
        float lot = (float) location.getLongitude();

        Geocoder geocoder = new Geocoder(LocationService.this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(lat,
                    lot, 1);
            StringBuilder sb = new StringBuilder();
            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                for (int i = 0; i < address.getMaxAddressLineIndex(); i++) {
                    sb.append(address.getAddressLine(i)).append(" ");
                }
                /*sb.append(address.getCountryName());
                Log.i("location", "address.getCountryName()==" + address.getCountryName());//国家名*/
              //  sb.append(address.getLocality()).append(" ");
                Log.i("location", "address.getLocality()==" + address.getLocality());//城市名
              //  sb.append(address.getSubLocality());
                Log.i("location", "address.getSubLocality()=2=" + address.getSubLocality());//---区名
                addressStr = sb.toString();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        //发送Action为com.example.communication.RECEIVER的广播
        Intent intent = new Intent("com.htlocation.RECEIVER");
        intent.putExtra("address", addressStr);
        sendBroadcast(intent);

        builder.setContentTitle(addressStr);
        builder
                .setContentText("纬度:" + lat +
                        "  经度:" + lot);
        notificationManager.notify(1100, builder.build());
        Log.d("LocationService", "纬度：" + lat +
                "经度：" + lot);
    }


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private void sendMassege(Location location) {
        if (location != null) {
            showloction(location);
        }

        com.alibaba.fastjson.JSONObject jsobj = new com.alibaba.fastjson.JSONObject();
        jsobj.put("lng", mLocation.getLongitude());
        jsobj.put("lat", mLocation.getLatitude());
        jsobj.put("GPSCode", App.IMEI);
        OkHttpUtils
                .postString()
                .content(jsobj.toJSONString())
                .addHeader("timestamp", UrlUtils.getTime())
                .addHeader("token", UrlUtils.getMd5(UrlUtils.getTime()))
                .url("http://192.168.1.55:8002/LogisticsPlatform/UpLoadTruckPosition")
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Toast.makeText(LocationService.this, "e:" + e, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Toast.makeText(LocationService.this, "response:" + response, Toast.LENGTH_SHORT).show();
                    }
                });


    }


}
