package com.vojkovladimir.world.adapter;

import android.database.Cursor;
import android.database.DataSetObserver;
import android.provider.BaseColumns;
import android.support.v7.widget.RecyclerView;

import static java.lang.Math.max;
import static java.lang.Math.min;

/**
 * Created by skyfishjy on 10/31/14.
 * Updated by vojkovladimir 05/02/15.
 */
public abstract class CursorRecyclerViewAdapter<VH extends RecyclerView.ViewHolder>
        extends RecyclerView.Adapter<VH> {

    private Cursor mCursor;

    private boolean mDataValid;

    private int mRowIdColumn;

    private DataSetObserver mDataSetObserver;

    public CursorRecyclerViewAdapter() {
        this(null);
    }

    public CursorRecyclerViewAdapter(Cursor cursor) {
        mCursor = cursor;
        mDataValid = cursor != null;
        mRowIdColumn = mDataValid ? mCursor.getColumnIndex(BaseColumns._ID) : -1;
        mDataSetObserver = new NotifyingDataSetObserver();
        if (mCursor != null) {
            mCursor.registerDataSetObserver(mDataSetObserver);
        }
    }

    public Cursor getCursor() {
        return mCursor;
    }

    @Override
    public int getItemCount() {
        if (mDataValid && mCursor != null) {
            return mCursor.getCount();
        }
        return 0;
    }

    @Override
    public long getItemId(int position) {
        if (mDataValid && mCursor != null && mCursor.moveToPosition(position)) {
            return mCursor.getLong(mRowIdColumn);
        }
        return 0;
    }

    @Override
    public void setHasStableIds(boolean hasStableIds) {
        super.setHasStableIds(true);
    }

    public abstract void onBindViewHolder(VH viewHolder, Cursor cursor);

    @Override
    public void onBindViewHolder(VH viewHolder, int position) {
        if (!mDataValid) {
            throw new IllegalStateException("this should only be called when the cursor is valid");
        }
        if (!mCursor.moveToPosition(position)) {
            throw new IllegalStateException("couldn't move cursor to position " + position);
        }
        onBindViewHolder(viewHolder, mCursor);
    }

    /**
     * Change the underlying cursor to a new cursor. If there is an existing cursor it will be
     * closed.
     */
    public void changeCursor(Cursor cursor) {
        Cursor old = swapCursor(cursor);
        if (old != null) {
            old.close();
        }
    }

    /**
     * Swap in a new Cursor, returning the old Cursor.  Unlike
     * {@link #changeCursor(Cursor)}, the returned old Cursor is <em>not</em>
     * closed.
     */
    public Cursor swapCursor(Cursor newCursor) {
        if (newCursor == mCursor) {
            return null;
        }

        final Cursor oldCursor = mCursor;

        if (oldCursor != null && mDataSetObserver != null) {
            oldCursor.unregisterDataSetObserver(mDataSetObserver);
        }

        mCursor = newCursor;

        if (mCursor == null) {
            mRowIdColumn = -1;
            mDataValid = false;
            //noinspection ConstantConditions
            if (oldCursor != null) {
                notifyItemRangeRemoved(0, oldCursor.getCount());
            }
            //There is no notifyDataSetInvalidated() method in RecyclerView.Adapter
        } else {
            if (mDataSetObserver != null) {
                mCursor.registerDataSetObserver(mDataSetObserver);
            }
            mRowIdColumn = newCursor.getColumnIndexOrThrow("_id");
            mDataValid = true;

            if (oldCursor == null || oldCursor.getCount() == 0) {
                for (int i = 0; i < mCursor.getCount(); i++) {
                    notifyItemInserted(i);
                }
            } else if (mCursor.getCount() == 0) {
                notifyItemRangeRemoved(0, oldCursor.getCount());
            } else {
                int min = min(oldCursor.getCount(), mCursor.getCount());
                int max = max(oldCursor.getCount(), mCursor.getCount());

                for (int i = 0; i < min; i++) {
                    if (oldCursor.moveToPosition(i) && mCursor.moveToPosition(i)
                            && isChanged(oldCursor, mCursor)) {
                        notifyItemChanged(i);
                    }
                }

                boolean isGrown = oldCursor.getCount() < mCursor.getCount();

                for (int i = min; i < max; i++) {
                    if (isGrown) {
                        notifyItemInserted(i);
                    } else {
                        notifyItemRemoved(i);
                    }
                }
            }
        }

        return oldCursor;
    }

    private boolean isChanged(Cursor oldCursor, Cursor newCursor) {
        return oldCursor.getLong(mRowIdColumn) != newCursor.getLong(mRowIdColumn);
    }

    private class NotifyingDataSetObserver extends DataSetObserver {
        @Override
        public void onChanged() {
            super.onChanged();
            mDataValid = true;
            notifyDataSetChanged();
        }

        @Override
        public void onInvalidated() {
            super.onInvalidated();
            mDataValid = false;
            notifyItemRangeRemoved(0, getItemCount());
            //There is no notifyDataSetInvalidated() method in RecyclerView.Adapter
        }
    }
}