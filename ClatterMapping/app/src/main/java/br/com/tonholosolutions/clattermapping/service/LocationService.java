package br.com.tonholosolutions.clattermapping.service;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

public class LocationService extends Service implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private static final String TAG = LocationService.class.getSimpleName();

    // Configuração dos intervalos de atualização do local recebidos do google api
    private final static int UPDATE_INTERVAL = 1000; // 10 sec
    private final static int FATEST_INTERVAL = 5000; // 5 sec
    private final static int DISPLACEMENT = 10; // 10 meters

    private final IBinder mBinder = new LocalBinder();
    private GoogleApiClient mGoogleApiClient;
    private Location mLocation;
    private LocationRequest mLocationRequest;

    public LocationService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        Log.d(TAG, "Location Service Started");
        if(checkPlayServices()) {
            buildGoogleApiClient(); // Criando o client para o GoogleApi

            // Resuming the periodic location updates
            if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
                startLocationUpdates();
            } else if (mGoogleApiClient != null) {
                mGoogleApiClient.connect();
            }
        } else {
            Log.e(TAG, "onStartCommand: Criando Serviço de Localização: Google play services não encontrado.");
        }

        return START_STICKY;
    }

    /*@Override
    public void onCreate() {
        super.onCreate();
        if (checkPlayServices()) {
            buildGoogleApiClient(); // Criando o client para o GoogleApi
        } else {
            Log.e(TAG, "onCreate: Criando Serviço de Localização: Google play services não encontrado.");
        }
    }*/

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onConnected(Bundle bundle) {
        createLocationRequest();
        startLocationUpdates();
        displayLocation();
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "onConnectionSuspended, code: " + i);
        if (mGoogleApiClient != null) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    public void onDestroy() {
        stopLocationUpdates();
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }

        super.onDestroy();
    }

    @Override
    public void onLocationChanged(Location location) {
        mLocation = location;
        displayLocation();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.e(TAG, "Connection failed: ConnectionResult.getErrorCode(" + connectionResult.getErrorCode() + ")");
    }

    /**
     * Metodo para verficiar se o google play services está no dispositivo
     */
    private boolean checkPlayServices() {
        Log.d(TAG, "checkPlayServices");
        boolean resultCode = true;

        if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(this) != ConnectionResult.SUCCESS) {
            Log.e(TAG, "Serviço de localização: checkPlayServices (This device is not supported).");
            resultCode = false;
        }

        return resultCode;
    }

    protected synchronized void buildGoogleApiClient() {
        Log.d(TAG, "buildGoogleApiClient executed");

        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }

    private void createLocationRequest() {
        Log.d(TAG, "createLocationRequest");
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL); //Intervalo de tempo desejado para receber atualização de local
        mLocationRequest.setFastestInterval(FATEST_INTERVAL); //Menor intervalo de tempo que o serviço está apto a receber a atualização de local
        mLocationRequest.setMaxWaitTime(2 * UPDATE_INTERVAL); //Intervalo máximo de tempo para receber atualização de local
        mLocationRequest.setSmallestDisplacement(DISPLACEMENT); //Variação de distância para se receber atualização de local
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY); //Retorna a localidade com a melhor acuracia possível
    }

    /**
     * Iniciando a atualização do local
     */
    protected void startLocationUpdates() {
        Log.d(TAG, "startLocationUpdates");
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    /**
     * Interropendo a atualização do local
     */
    protected void stopLocationUpdates() {
        Log.d(TAG, "stopLocationUpdates");
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    /**
     * Exibe no debug a localização
     */
    private void displayLocation() {

        if (mGoogleApiClient != null) {
            mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        }

        if (mLocation != null) {
            Log.d(TAG, "Latitude, Longitude (" + mLocation.getLatitude() + "," + mLocation.getLongitude() + ")");
        } else {
            Log.d(TAG, "Não foi possível identificar a sua localização. Verificar se o GPS está ligado!");
        }
    }

    public Location getLastLocation() {
        return mLocation;
    }

    public class LocalBinder extends Binder {
        /**
         * @return Retorna o objeto do serviço.
         */
        public LocationService getService() {
            return LocationService.this;
        }
    }
}
