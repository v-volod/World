package com.vojkovladimir.world.ui;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.vojkovladimir.world.R;
import com.vojkovladimir.world.adapter.CursorRecyclerViewAdapter;
import com.vojkovladimir.world.provider.CityQuery;
import com.vojkovladimir.world.ui.widget.SpaceItemDecoration;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import jp.wasabeef.glide.transformations.RoundedCornersTransformation;

public class MainActivity extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor> {

    @Bind(R.id.recycler_view)
    RecyclerView mRecyclerView;

    CitiesAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setUpRecyclerView();

        getLoaderManager().initLoader(0, null, this);
    }

    private void setUpRecyclerView() {
        mAdapter = new CitiesAdapter();

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new SpaceItemDecoration(this, true, true));
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(this, CityQuery.URI, CityQuery.PROJECTION, null, null,
                CityQuery.SORT_ORDER);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.changeCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.changeCursor(null);
    }

    static class CityVH extends RecyclerView.ViewHolder {

        @Bind(R.id.image)
        ImageView image;
        @Bind(R.id.title)
        TextView title;

        @Bind(R.id.map_button)
        View mapButton;
        @Bind(R.id.map_image)
        ImageView mapImage;

        OnCardInteractionListener mListener;

        public CityVH(View itemView, OnCardInteractionListener listener) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            mListener = listener;
        }

        @OnClick(R.id.card)
        void onCardClick() {
            mListener.onCardClicked(getAdapterPosition());
        }

        @OnLongClick(R.id.card)
        boolean onLongClick() {
            mListener.onCardLongClick(getAdapterPosition());
            return true;
        }

        interface OnCardInteractionListener {
            void onCardClicked(int position);

            void onCardLongClick(int position);
        }
    }

    private class CitiesAdapter extends CursorRecyclerViewAdapter<CityVH>
            implements CityVH.OnCardInteractionListener {

        final int GREEN = ContextCompat.getColor(getApplicationContext(), R.color.md_green_500);
        final int RED = ContextCompat.getColor(getApplicationContext(), R.color.md_red_500);

        LayoutInflater mInflater = LayoutInflater.from(getApplicationContext());
        RequestManager mRequestManager = Glide.with(getApplicationContext());
        RoundedCornersTransformation mTransformation = new RoundedCornersTransformation(
                getApplicationContext(),
                getResources().getDimensionPixelOffset(R.dimen.cardview_default_radius),
                0,
                RoundedCornersTransformation.CornerType.TOP
        );

        @Override
        public void onBindViewHolder(CityVH viewHolder, Cursor cursor) {
            viewHolder.title.setText(cursor.getString(CityQuery.ColumnID.NAME));
            if (cursor.isNull(CityQuery.ColumnID.IMAGE_URL)) {
                viewHolder.image.setColorFilter(RED);
                viewHolder.image.setImageResource(R.drawable.ic_image_black_48dp);
            } else {
                viewHolder.image.clearColorFilter();
                mRequestManager.load(cursor.getString(CityQuery.ColumnID.IMAGE_URL))
                        .diskCacheStrategy(DiskCacheStrategy.SOURCE)
                        .thumbnail(0.5f)
                        .centerCrop()
                        .bitmapTransform(mTransformation)
                        .into(viewHolder.image);
            }

            boolean isEnabled = !(cursor.isNull(CityQuery.ColumnID.LATITUDE)
                    && cursor.isNull(CityQuery.ColumnID.LONGITUDE));
            viewHolder.mapImage.setColorFilter(isEnabled ? GREEN : RED);
        }

        @Override
        public CityVH onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = mInflater.inflate(R.layout.card_city, parent, false);
            return new CityVH(itemView, this);
        }

        @Override
        public void onCardClicked(int position) {
            /*
            * ToDo: open details
            * */
        }

        @Override
        public void onCardLongClick(int position) {
            Cursor cursor = getCursor();
            cursor.moveToPosition(position);
            String name = cursor.getString(CityQuery.ColumnID.NAME);
            Toast.makeText(getApplicationContext(), name, Toast.LENGTH_SHORT).show();
        }
    }
}
