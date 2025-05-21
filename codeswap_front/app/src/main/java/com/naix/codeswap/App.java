package com.naix.codeswap;

import android.app.Application;
import com.naix.codeswap.api.ApiClient;

public class App extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        ApiClient.init(this);
    }
}