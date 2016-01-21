package com.vojkovladimir.world.ui;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.vojkovladimir.world.R;
import com.vojkovladimir.world.provider.CityQuery;
import com.vojkovladimir.world.ui.request.LoadRequestListener;

import butterknife.Bind;
import butterknife.ButterKnife;

import static com.vojkovladimir.world.provider.CitiesContract.City;

public class CityActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String EXTRA_CITY_ID = "city_id";

    private long cityId;
    private double latitude = -0.0f;
    private double longitude = -0.0f;

    @Bind(R.id.toolbar)
    Toolbar mToolbar;
    @Bind(R.id.collapsing_toolbar_layout)
    CollapsingToolbarLayout mCollapsingToolbarLayout;
    @Bind(R.id.image)
    ImageView mImage;
    @Bind(R.id.description)
    TextView mDescription;

    RequestManager mRequestManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_city);
        ButterKnife.bind(this);

        setUpToolbar();

        cityId = getIntent().getLongExtra(EXTRA_CITY_ID, -1);

        mRequestManager = Glide.with(this);

        getLoaderManager().initLoader(0, null, this);
    }

    private void setUpToolbar() {
        setSupportActionBar(mToolbar);
        //noinspection ConstantConditions
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ButterKnife.findById(this, R.id.status_bar_background).setVisibility(View.VISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.city, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.map).setEnabled(!(latitude == -0.0f && longitude == -0.0f));
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.map:
                Intent intent = new Intent(this, MapActivity.class);
                intent.putExtra(MapActivity.EXTRA_CITY_NAME, mCollapsingToolbarLayout.getTitle());
                intent.putExtra(MapActivity.EXTRA_LATITUDE, latitude);
                intent.putExtra(MapActivity.EXTRA_LONGITUDE, longitude);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(getApplicationContext(), City.buildItemUri(cityId),
                CityQuery.PROJECTION, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()) {
            mCollapsingToolbarLayout.setTitle(data.getString(CityQuery.ColumnID.NAME));
            mDescription.setText(data.getString(CityQuery.ColumnID.DESCRIPTION));

            if (data.isNull(CityQuery.ColumnID.IMAGE_URL)) {
                ButterKnife.findById(this, R.id.image_layout).setVisibility(View.INVISIBLE);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    int primaryDark = ContextCompat.getColor(this, R.color.colorPrimaryDark);
                    getWindow().setStatusBarColor(primaryDark);
                }

            } else {
                int color = ContextCompat.getColor(this, R.color.md_red_500);
                mImage.clearColorFilter();
                mRequestManager.load(data.getString(CityQuery.ColumnID.IMAGE_URL))
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .thumbnail(0.5f)
                        .centerCrop()
                        .listener(new LoadRequestListener(mImage, color))
                        .into(mImage);
            }
            if (!(data.isNull(CityQuery.ColumnID.LATITUDE)
                    && data.isNull(CityQuery.ColumnID.LONGITUDE))) {
                latitude = data.getDouble(CityQuery.ColumnID.LATITUDE);
                longitude = data.getDouble(CityQuery.ColumnID.LONGITUDE);
            }
        }
        invalidateOptionsMenu();
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }
}
