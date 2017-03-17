package io.github.dialogsforandroid.recyclerviewadapters.sample;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import io.github.dialogsforandroid.recyclerviewadapters.R;

class HeaderViewHolder extends RecyclerView.ViewHolder {
    private static final String TAG = "HeaderViewHolder";

    private final TextView mTextView;

    HeaderViewHolder(View itemView) {
        super(itemView);
        mTextView = (TextView) itemView.findViewById(R.id.header_text);
    }

    void setValue(final String value) {
        Log.d(TAG, value);
        mTextView.setText(value);
    }
}
