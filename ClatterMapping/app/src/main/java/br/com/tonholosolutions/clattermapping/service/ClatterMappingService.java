package br.com.tonholosolutions.clattermapping.service;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.location.Location;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.Date;

import br.com.tonholosolutions.clattermapping.model.Mapping;

/**
 * Created on 12/11/2015.
 *
 * @author rafaeltonholo
 */
public class ClatterMappingService extends Service {

    private static final String TAG = ClatterMappingService.class.getSimpleName();
    private static final long DELAY_TIME = 60000;
    private static final long CAPTURE_TIME = 5000;
    public static final String KEY_PREFERENCE = "CLATTER_MAPPING_SERVICE_STARTED";
    private LocationService.LocalBinder mBinder;
    private ServiceConnection mConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBinder = (LocationService.LocalBinder) service;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, "Serviço iniciado!!");
        Intent locationIntent = new Intent(getBaseContext(), LocationService.class);
        bindService(locationIntent, mConnection, BIND_AUTO_CREATE);
        startService(locationIntent);

        runService();
        return Service.START_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void runService() {
        final Handler handler = new Handler();
        Runnable runnable = new Runnable() {
            Runnable thiz = this;

            @Override
            public void run() {
                SoundListener.getInstance().startRecording();
                Runnable runnable = new Runnable() {
                    @Override
                    public void run() {
                        if (mBinder != null) {
                            SoundListener.getInstance().stopRecording();
                            Location lastLocation = mBinder.getService().getLastLocation();
                            if (lastLocation != null) {
                                Mapping mapping = new Mapping();
                                mapping.setDataRegistro(new Date());
                                mapping.setDecibel(SoundListener.getInstance().getAvgSpl());
                                mapping.setLatitude(lastLocation.getLatitude());
                                mapping.setLongitude(lastLocation.getLongitude());
                                mapping.save();
                            }
                        }
                    }
                };

                handler.postDelayed(runnable, CAPTURE_TIME);
                handler.postDelayed(thiz, DELAY_TIME);
            }
        };

        handler.post(runnable);
    }
}
