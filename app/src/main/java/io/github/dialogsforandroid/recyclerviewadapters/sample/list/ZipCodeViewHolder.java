package io.github.dialogsforandroid.recyclerviewadapters.sample.list;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.util.Locale;

import io.github.dialogsforandroid.recyclerviewadapters.R;

public class ZipCodeViewHolder extends RecyclerView.ViewHolder {
    private static final String TAG = "ZipCodeViewHolder";

    private final TextView mTextView;

    public ZipCodeViewHolder(View itemView) {
        super(itemView);
        mTextView = (TextView) itemView.findViewById(R.id.row_text);
    }

    void setValue(int value) {
        String valueAsString = String.format(Locale.US, "%05d", value);
        Log.d(TAG, valueAsString);
        mTextView.setText(valueAsString);
    }

}
