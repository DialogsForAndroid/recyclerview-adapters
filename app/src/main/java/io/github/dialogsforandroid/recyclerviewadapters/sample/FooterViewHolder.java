package io.github.dialogsforandroid.recyclerviewadapters.sample;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import io.github.dialogsforandroid.recyclerviewadapters.R;

class FooterViewHolder extends RecyclerView.ViewHolder {
    private static final String TAG = "FooterViewHolder";

    private final TextView mTextView;

    FooterViewHolder(View itemView) {
        super(itemView);
        mTextView = (TextView) itemView.findViewById(R.id.footer_text);
    }

    void setValue(final String value) {
        Log.d(TAG, value);
        mTextView.setText(value);
    }
}
