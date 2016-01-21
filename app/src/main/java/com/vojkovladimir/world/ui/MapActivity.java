package com.vojkovladimir.world.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.vojkovladimir.world.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback {

    public static final String EXTRA_CITY_NAME = "city_name";
    public static final String EXTRA_LATITUDE = "latitude";
    public static final String EXTRA_LONGITUDE = "longitude";

    private String cityName;
    private LatLng latLng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        ButterKnife.bind(this);

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        cityName = getIntent().getStringExtra(EXTRA_CITY_NAME);
        latLng = new LatLng(
                getIntent().getDoubleExtra(EXTRA_LATITUDE, 0.0f),
                getIntent().getDoubleExtra(EXTRA_LONGITUDE, 0.0f)
        );
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBack();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @OnClick(R.id.back)
    void onBack() {
        finish();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 8.0f));
        googleMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title(cityName)).showInfoWindow();
    }

}
