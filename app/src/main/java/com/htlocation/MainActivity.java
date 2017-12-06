package com.htlocation;

import android.Manifest;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.htlocation.Service.LocationService;
import com.htlocation.Service.OnePixelReceiver;
import com.htlocation.base.ActivityBase;
import com.taobao.sophix.SophixManager;
import com.yanzhenjie.permission.AndPermission;
import com.yanzhenjie.permission.PermissionListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends ActivityBase {

    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.right_tv)
    TextView rightTv;
    @BindView(R.id.btn)
    Button btn;
    @BindView(R.id.activity_main)
    LinearLayout activityMain;
    @BindView(R.id.gps_code)
    TextView gpsCode;
    @BindView(R.id.address)
    TextView address;
    @BindView(R.id.btn_tv)
    TextView btnTv;
    private MsgReceiver msgReceiver;

    private OnePixelReceiver mOnepxReceiver;


    @Override
    protected int getContentViewResId() {
        return R.layout.activity_main;
    }

    @Override
    protected void initViews() {

    }

    @Override
    protected void initDatas() {


    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
        //注册监听屏幕的广播

        mOnepxReceiver = new OnePixelReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.intent.action.SCREEN_OFF");
        intentFilter.addAction("android.intent.action.SCREEN_ON");
        intentFilter.addAction("android.intent.action.USER_PRESENT");
        registerReceiver(mOnepxReceiver, intentFilter);


        //动态注册地址消息广播接收器
        msgReceiver = new MsgReceiver();
        IntentFilter intentFilter1 = new IntentFilter();
        intentFilter1.addAction("com.htlocation.RECEIVER");
        registerReceiver(msgReceiver, intentFilter1);


        if (isServiceRunning(MainActivity.this)) {
            btnTv.setText("点击关闭服务↑");
            btn.setBackgroundResource(R.drawable.ring1);
        }
        initImg();
        //获取手机IMEI码
        AndPermission.with(MainActivity.this)
                .permission(Manifest.permission.READ_PHONE_STATE,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE
                        ,Manifest.permission.ACCESS_WIFI_STATE
                )
                .requestCode(100)
                .callback(new PermissionListener() {
                    @Override
                    public void onSucceed(int requestCode, @NonNull List<String> grantPermissions) {
                        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(MainActivity.this.TELEPHONY_SERVICE);
                        String imei = telephonyManager.getDeviceId();
                        Log.d("MainActivity", imei);
                        gpsCode.setText(imei);
                        App.IMEI = imei;
                        SophixManager.getInstance().queryAndLoadNewPatch();
                    }

                    @Override
                    public void onFailed(int requestCode, @NonNull List<String> deniedPermissions) {
                        Toast.makeText(MainActivity.this, "手机读写权限获取失败", Toast.LENGTH_SHORT).show();
                    }
                })
                .start();


    }

    @OnClick({R.id.back, R.id.btn, R.id.right_tv})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                Intent home = new Intent(Intent.ACTION_MAIN);
                home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                home.addCategory(Intent.CATEGORY_HOME);
                startActivity(home);
//             startActivity(new Intent(MainActivity.this, ExplainActivity.class));
//                Toast.makeText(this, "呵呵呵大", Toast.LENGTH_SHORT).show();
                break;
            case R.id.right_tv:
                startActivity(new Intent(MainActivity.this, ExplainActivity.class));
                break;
            case R.id.btn:
                AndPermission.with(MainActivity.this)
                        .permission(Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_FINE_LOCATION
                        )
                        .requestCode(100)
                        .callback(new PermissionListener() {
                            @Override
                            public void onSucceed(int requestCode, @NonNull List<String> grantPermissions) {
                                if (btnTv.getText().toString().contains("点击开启服务")) {
                                    Intent intent = new Intent(MainActivity.this, LocationService.class);
                                    btnTv.setText("点击关闭服务↑");
                                    btn.setBackgroundResource(R.drawable.meitu_3);
                                    startService(intent);
                                } else {

                                    AlertDialog.Builder alertDialog = new AlertDialog.Builder(MainActivity.this);
                                    alertDialog.setIcon(R.mipmap.ic_launcher);
                                    alertDialog.setTitle("警告!");
                                    alertDialog.setMessage("您确认关闭服务吗？");
                                    alertDialog.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            btnTv.setText("点击开启服务↑");
                                            btn.setBackgroundResource(R.drawable.meitu_4);
                                            Intent intent = new Intent(MainActivity.this, LocationService.class);
                                            stopService(intent);
                                            dialog.dismiss();
                                        }
                                    });
                                    alertDialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            dialog.dismiss();
                                        }
                                    });
                                    alertDialog.show();

                                }
                            }

                            @Override
                            public void onFailed(int requestCode, @NonNull List<String> deniedPermissions) {
                                Toast.makeText(MainActivity.this, "位置权限获取失败", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .start();


                break;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (isServiceRunning(MainActivity.this)) {
            btnTv.setText("点击关闭服务↑");
            btn.setBackgroundResource(R.drawable.meitu_3);

        }
    }

    /**
     * 判断服务是否运行状态
     *
     * @param context
     * @return
     */
    public static boolean isServiceRunning(Context context) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if ("com.htlocation.Service.LocationService".equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }


    private void initImg() {

        DraweeController mDraweeController = Fresco.newDraweeControllerBuilder()
                .setAutoPlayAnimations(true)
                //加载drawable里的一张gif图
                .setUri(Uri.parse("res://" + getPackageName() + "/" + R.drawable.runing))//设置uri
                .build();
        //设置Controller
        //   img.setController(mDraweeController);

    }


    /**
     * 广播接收器
     *
     * @author len
     */
    public class MsgReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            //拿到数据，更新UI
            String taddress = intent.getStringExtra("address");
            address.setText(taddress);

        }
    }


    //点击返回键返回桌面而不是退出程序
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Intent home = new Intent(Intent.ACTION_MAIN);
            home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            home.addCategory(Intent.CATEGORY_HOME);
            startActivity(home);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }



}
