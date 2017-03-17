package io.github.dialogsforandroid.recyclerviewadapters.sample.list;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import io.github.dialogsforandroid.recyclerviewadapters.R;

public class HeaderViewHolder extends RecyclerView.ViewHolder {
    private static final String TAG = "HeaderViewHolder";

    private final TextView mTextView;

    public HeaderViewHolder(View itemView) {
        super(itemView);
        mTextView = (TextView) itemView.findViewById(R.id.header_text);
    }

    public void setValue(final String value) {
        Log.d(TAG, value);
        mTextView.setText(value);
    }
}
