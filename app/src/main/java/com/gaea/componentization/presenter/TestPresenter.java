package com.gaea.componentization.presenter;

import com.gaea.module_base.base.BasePresenter;
import com.gaea.module_base.base.IBaseView;
import com.gaea.module_base.util.LogUtil;
import com.trello.rxlifecycle2.LifecycleProvider;
import com.trello.rxlifecycle2.android.ActivityEvent;


import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;


/**
 * Created by zhuxiaohong on 2018/7/2
 */
public class TestPresenter extends BasePresenter<IBaseView, LifecycleProvider> {


    public TestPresenter(IBaseView view, LifecycleProvider activity) {
        super(view, activity);
    }

    public void startCallingTiming() {
        Observable.interval(1, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .doOnSubscribe(new Consumer<Disposable>() {
                    @Override
                    public void accept(Disposable disposable) throws Exception {

                    }
                }).subscribeOn(AndroidSchedulers.mainThread()).observeOn(Schedulers.io())
                .doOnNext(new Consumer<Long>() {
                    @Override
                    public void accept(Long aLong) throws Exception {
                        LogUtil.info("doOnNext:" + aLong);
                    }
                }).observeOn(AndroidSchedulers.mainThread())
                .compose(getActivity().bindUntilEvent(ActivityEvent.DESTROY))
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {

                    }

                    @Override
                    public void onNext(Long aLong) {
                        LogUtil.info("onNext:" + aLong);
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }
}
