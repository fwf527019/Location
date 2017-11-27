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
                "      尊敬的用户:为了确保您的位置信息能够准确的上传到服务器，请开启定位服务后不要清理该应用进程。您可以按HOME键使应用在后台运行，锁屏不会让进程退出，请放心使用！"
                        + "\n" +"\n"+ "    首页的关联码是该手机与车辆关联的唯一标识，请使用本系统前，正确的将它填入后台管理系统，确保手机与车辆一一对应的关系。"
        );

    }

    @OnClick(R.id.back)
    public void onViewClicked() {
        finish();
    }


}
