package br.com.tonholosolutions.clattermapping;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.TileOverlayOptions;
import com.google.maps.android.heatmaps.Gradient;
import com.google.maps.android.heatmaps.HeatmapTileProvider;
import com.raizlabs.android.dbflow.sql.QueryBuilder;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.util.ArrayList;
import java.util.List;

import br.com.tonholosolutions.clattermapping.model.Mapping;
import br.com.tonholosolutions.clattermapping.model.Mapping$Table;
import br.com.tonholosolutions.clattermapping.service.ClatterMappingService;
import br.com.tonholosolutions.clattermapping.util.SharedPreferenceManager;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {
    private SharedPreferenceManager mSharedPreferenceManager;
    private boolean mServiceStarted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Obtain the MapFragment and get notified when the map is ready to be used.
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mSharedPreferenceManager = SharedPreferenceManager.getInstance(MainActivity.this);
        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        mServiceStarted = mSharedPreferenceManager.sharedPrefsRead(ClatterMappingService.KEY_PREFERENCE, false);

        fab.setImageResource(mServiceStarted ? android.R.drawable.ic_media_pause : android.R.drawable.ic_media_play);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mServiceStarted = mSharedPreferenceManager.sharedPrefsRead(ClatterMappingService.KEY_PREFERENCE, false);
                Intent it = new Intent(MainActivity.this, ClatterMappingService.class);
                if (mServiceStarted) {
                    stopService(it);
                    mSharedPreferenceManager.sharedPrefsWrite(ClatterMappingService.KEY_PREFERENCE, false);
                    fab.setImageResource(android.R.drawable.ic_media_play);
                } else {
                    startService(it);
                    mSharedPreferenceManager.sharedPrefsWrite(ClatterMappingService.KEY_PREFERENCE, true);
                    fab.setImageResource(android.R.drawable.ic_media_pause);
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_settings:
                Intent it = new Intent(this, SettingsActivity.class);
                startActivity(it);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.getUiSettings().setMyLocationButtonEnabled(true);
        googleMap.getUiSettings().setAllGesturesEnabled(false);
        googleMap.getUiSettings().setZoomGesturesEnabled(true);
        googleMap.getUiSettings().setScrollGesturesEnabled(true);
        googleMap.setMyLocationEnabled(true);

        List<Mapping> mappingList = new Select()
                .rawColumns("AVG(`decibel`) as decibel", Mapping$Table.LATITUDE, Mapping$Table.LONGITUDE)
                .from(Mapping.class).where()
                .groupBy(new QueryBuilder().appendQuotedArray(Mapping$Table.LATITUDE, Mapping$Table.LONGITUDE))
                .queryList();

        List<LatLng> latLngList = new ArrayList<>();
//        List<WeightedLatLng> latLngList = new ArrayList<>();
        for (Mapping mapping : mappingList) {
            LatLng latLng = new LatLng(mapping.getLatitude(), mapping.getLongitude());
//            latLngList.add(new WeightedLatLng(latLng, Math.round(mapping.getDecibel()) / 160));
            latLngList.add(latLng);
            googleMap.addMarker(
                    new MarkerOptions()
                            .position(latLng)
                            .title(String.format("Média de Decibel cadastrado: %d", Math.round(mapping.getDecibel())))
            );
        }

        int[] color = {
                Color.rgb(96, 146, 95), // Pin Falling
                Color.rgb(143, 177, 80), // Rustling Leaves
                Color.rgb(201, 193, 46), // Refrigerator Hum
                Color.rgb(243, 185, 15), // Floor Fan
                Color.rgb(249, 161, 28), // Conversation
                Color.rgb(245, 121, 33), // Vacuum Cleaner
                Color.rgb(231, 98, 33), // Lawn Mower
                Color.rgb(220, 74, 35), // Motorcycle
                Color.rgb(209, 46, 37), // Chainsaw
                Color.rgb(148, 17, 22), // Jat Takeoff
                Color.rgb(110, 4, 6), // Gauge Shotgun
        };

        float[] startPoint = {
                0.1f,
                0.1875f, // Rustling Leaves
                0.25f, // Refrigerator Hum
                0.3125f, // Floor Fan
                0.40625f, // Conversation
                0.5f, // Vacuum Cleaner
                0.5625f, // Lawn Mower
                0.625f, // Motorcycle
                0.6875f, // Chainsaw
                0.875f, // Jat Takeoff
                1, // Gauge Shotgun
        };

        Gradient gradient = new Gradient(color, startPoint);

        HeatmapTileProvider mProvider = new HeatmapTileProvider.Builder()
                .data(latLngList)
                .gradient(gradient)
                .radius(50)
                .build();

        googleMap.addTileOverlay(new TileOverlayOptions().tileProvider(mProvider));
    }
}
