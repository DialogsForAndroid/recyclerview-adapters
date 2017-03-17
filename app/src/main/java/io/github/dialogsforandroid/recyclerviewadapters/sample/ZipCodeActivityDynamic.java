package io.github.dialogsforandroid.recyclerviewadapters.sample;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Locale;

import io.github.dialogsforandroid.recyclerviewadapters.R;
import io.github.dialogsforandroid.recyclerviewadapters.library.DynamicSectionsAdapter;
import io.github.dialogsforandroid.recyclerviewadapters.sample.list.FooterViewHolder;
import io.github.dialogsforandroid.recyclerviewadapters.sample.list.HeaderViewHolder;
import io.github.dialogsforandroid.recyclerviewadapters.sample.list.ListAdapter;
import io.github.dialogsforandroid.recyclerviewadapters.sample.list.ZipCodeViewHolder;

public class ZipCodeActivityDynamic extends AppCompatActivity {

    private static final String TAG = "ZipCodeActivityDynamic";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_zipcodes);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        RecyclerView recycler = (RecyclerView) findViewById(R.id.recycler);
        //recycler.setNestedScrollingEnabled(false);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        recycler.setLayoutManager(llm);

        long startTime, endTime;

        startTime = System.currentTimeMillis();

        final ListAdapter itemsAdapter = new ListAdapter(1000, 99998);

        endTime = System.currentTimeMillis();
        Log.d(TAG, "Total execution time: " + (endTime-startTime) + "ms");

        startTime = System.currentTimeMillis();

        recycler.setAdapter(new DynamicSectionsAdapter<ZipCodeViewHolder, HeaderViewHolder, FooterViewHolder>(itemsAdapter) {
            @Override
            protected long getSectionId(int itemAdapterPosition) {
                int value = itemsAdapter.getValue(itemAdapterPosition);
                // e.g. zip code 65192 -> 65
                return value/1000;
            }

            @Override
            protected boolean sectionHasHeader(long sectionId) {
                return true;
            }

            @Override
            protected boolean sectionHasFooter(long sectionId) {
                return true;
            }

            @Override
            protected HeaderViewHolder onCreateSectionHeaderViewHolder(ViewGroup parent) {
                LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                View contactView = inflater.inflate(R.layout.header, parent, false);
                return new HeaderViewHolder(contactView);
            }

            @Override
            protected FooterViewHolder onCreateSectionFooterViewHolder(ViewGroup parent) {
                LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                View contactView = inflater.inflate(R.layout.footer, parent, false);
                return new FooterViewHolder(contactView);
            }

            @Override
            protected void onBindHeaderViewHolder(HeaderViewHolder vh, long sectionId) {
                String valueAsString = String.format(Locale.US, "%02d", sectionId);
                vh.setValue("Begin of " + valueAsString);
            }

            @Override
            protected void onBindFooterViewHolder(FooterViewHolder vh, long sectionId) {
                String valueAsString = String.format(Locale.US, "%02d", sectionId);
                vh.setValue("End of " + valueAsString);
            }
        });

        endTime = System.currentTimeMillis();
        Log.d(TAG, "Total execution time: " + (endTime-startTime) + "ms");
    }
}
