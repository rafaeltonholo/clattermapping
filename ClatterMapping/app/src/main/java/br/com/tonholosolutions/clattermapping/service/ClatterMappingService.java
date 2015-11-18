package br.com.tonholosolutions.clattermapping.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created on 12/11/2015.
 *
 * @author rafaeltonholo
 */
public class ClatterMappingService extends Service {
    private static final String TAG = ClatterMappingService.class.getSimpleName();
    private static final long DELAY_TIME = 10000;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Serviço iniciado!!");
        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            Runnable thiz = this;
            @Override
            public void run() {
                SoundListener.getInstance().startRecording();
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        SoundListener.getInstance().stopRecording();
                    }
                };

                handler.postDelayed(runnable, DELAY_TIME / 2);
                handler.postDelayed(thiz, DELAY_TIME);
            }
        };

        handler.post(runnable);

        return Service.START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
