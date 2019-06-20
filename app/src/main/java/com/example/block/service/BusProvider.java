package com.example.block.service;

import android.os.Handler;
import android.os.Looper;

import com.squareup.otto.Bus;

import java.util.ArrayList;

public final class BusProvider extends Bus {

    // 같은 object의 중복 register를 방지하기 위해 registeredObjects(list) 생성
    private ArrayList registeredObjects = new ArrayList();
    private static final BusProvider BUS = new BusProvider();

    private final Handler mHandler = new Handler(Looper.getMainLooper());

    @Override
    public void post(final Object event) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            super.post(event);
        } else {
            mHandler.post(() -> BusProvider.super.post(event));
        }
    }


    public static BusProvider getInstance(){
        return BUS;
    }
    private BusProvider(){
        //no instance.
    }

    //object register
    @Override
    public void register(Object object) {
        if(!registeredObjects.contains(object)){
            registeredObjects.add(object);
            super.register(object);
        }
    }

    //object unregister
    @Override
    public void unregister(Object object) {
        if(registeredObjects.contains(object)){
            registeredObjects.remove(object);
            super.unregister(object);
        }
    }

    //전체 object unregister
    public void allUnregister(){
        for(int i=0; i<registeredObjects.size(); i++){
            Object object = registeredObjects.get(i);
            unregister(object);
        }
        registeredObjects.clear();
        registeredObjects = new ArrayList();
    }
}