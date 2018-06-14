package com.innotek.ifieldborad;

import android.app.Activity;
import android.app.Application;

import com.innotek.ifieldborad.utils.CrashHandler;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/6/21.
 */

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        CrashHandler handler = CrashHandler.getInstance();
        handler.init(this);
    }
}
