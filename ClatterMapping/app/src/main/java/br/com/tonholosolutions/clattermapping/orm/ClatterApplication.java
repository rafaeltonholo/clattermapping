package br.com.tonholosolutions.clattermapping.orm;

import android.app.Application;

import com.raizlabs.android.dbflow.config.FlowManager;

/**
 * Created on 12/11/2015.
 * @author rafaeltonholo
 */
public class ClatterApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FlowManager.init(this);
    }
}
