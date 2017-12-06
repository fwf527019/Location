package com.htlocation;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.htlocation.base.ActivityBase;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2017/11/27.
 */

public class ExplainActivity extends ActivityBase {
    @BindView(R.id.back)
    ImageView back;
    @BindView(R.id.title)
    TextView title;
    @BindView(R.id.right_tv)
    TextView rightTv;
    @BindView(R.id.content)
    TextView content;

    @Override
    protected int getContentViewResId() {
        return R.layout.activity_explain;
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
        content.setText(
                "      尊敬的用户:为了确保您的位置信息能够准确的上传到服务器，请开启定位服务后不要清理该应用进程（安全软件的一键清理功能，系统的进程清理，系统的应用结束功能等）。您可以按HOME键，手机返回键，应用返回键（←）使应用在后台运行，锁屏不会让进程退出，请放心使用！若该应用在使用过程中被系统退出或认为退出，请再次开启服务即可正常使用。"
                        + "\n" +"\n"+ "    首页的关联码是该手机与车辆关联的唯一标识，请使用本系统前，正确的将它填入后台管理系统，确保手机与车辆一一对应的关系。"  + "\n" +"\n"+ "    特别提醒:该服务在正常运行时，状态栏会显示应用图标和位置信息，若没有显示则说明定位服务已经关闭此时需要您手动打开。"
                        + "\n" +"\n"+"    注意：该应用必需开启位置权限，手机的状态权限，首次开启时会向您申请，若开启失败，您可以在手机的权限设置中信任此应用。该应用进入后需要30秒才能显示位置信息，刚进入时显示暂无位置信息是正常现象。"
        );

    }

    @OnClick(R.id.back)
    public void onViewClicked() {
        finish();
    }


}
