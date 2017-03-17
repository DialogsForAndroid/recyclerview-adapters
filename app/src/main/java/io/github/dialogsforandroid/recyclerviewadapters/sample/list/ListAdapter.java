package io.github.dialogsforandroid.recyclerviewadapters.sample.list;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

import io.github.dialogsforandroid.recyclerviewadapters.R;

public class ListAdapter extends RecyclerView.Adapter<ZipCodeViewHolder> {

    private List<Integer> content = new ArrayList<>(10000);

    public ListAdapter(int from, int to) {
        for (int value = from; value <= to; ++value) {
            content.add(value);
        }
    }

    @Override
    public ZipCodeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View contactView = inflater.inflate(R.layout.row_zipcode, parent, false);
        return new ZipCodeViewHolder(contactView);
    }

    @Override
    public void onBindViewHolder(ZipCodeViewHolder holder, int position) {
        int value = content.get(position);
        holder.setValue(value);
    }

    @Override
    public int getItemCount() {
        return content.size();
    }

    public int getValue(int position) {
        return content.get(position);
    }
}
