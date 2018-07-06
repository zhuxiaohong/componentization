package com.gaea.componentization.ui;

import android.os.Bundle;
import android.view.View;

import com.alibaba.android.arouter.launcher.ARouter;
import com.gaea.componentization.R;
import com.gaea.componentization.presenter.TestPresenter;
import com.gaea.module_base.base.BaseFragmentActivity;
import com.gaea.module_base.base.BasePresenter;
import com.gaea.module_base.base.IBaseView;
import com.trello.rxlifecycle2.android.ActivityEvent;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;

import io.reactivex.schedulers.Schedulers;

public class MainActivity extends BaseFragmentActivity implements IBaseView {
    private TestPresenter presenter = new TestPresenter(this, this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.hello).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ARouter.getInstance().build("/test/activity").navigation();
            }
        });
    }

    @Override
    protected void initView() {
        presenter.startCallingTiming();


    }

    @Override
    protected void initData() {

    }
}
