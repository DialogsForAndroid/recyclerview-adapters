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

import java.util.ArrayList;
import java.util.Locale;

import io.github.dialogsforandroid.recyclerviewadapters.R;
import io.github.dialogsforandroid.recyclerviewadapters.library.StaticSectionsAdapter;

public class ZipCodeActivity2 extends AppCompatActivity {

    private static final String TAG = "ZipCodeActivity";

    @SuppressWarnings("PointlessArithmeticExpression")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scrolling);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        RecyclerView recycler = (RecyclerView) findViewById(R.id.recycler);
        //recycler.setNestedScrollingEnabled(false);
        LinearLayoutManager llm = new LinearLayoutManager(this);
        recycler.setLayoutManager(llm);

        long startTime, endTime;

        startTime = System.currentTimeMillis();

        ArrayList<ListAdapter> itemsAdapters = new ArrayList<>();
        for (int prefix = 1; prefix <= 99; ++prefix) {
            itemsAdapters.add(new ListAdapter(prefix * 1000, prefix * 1000 + 999));
        }

        endTime = System.currentTimeMillis();
        Log.d(TAG, "Total execution time: " + (endTime-startTime) + "ms");

        startTime = System.currentTimeMillis();

        recycler.setAdapter(new StaticSectionsAdapter<ZipCodeViewHolder, HeaderViewHolder, FooterViewHolder>(itemsAdapters) {

            @Override
            protected boolean sectionHasHeader(int sectionIndex) {
                return true;
            }

            @Override
            protected boolean sectionHasFooter(int sectionIndex) {
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
            protected void onBindHeaderViewHolder(HeaderViewHolder vh, int sectionIndex) {
                String valueAsString = String.format(Locale.US, "%02d", sectionIndex);
                vh.setValue("Begin of " + valueAsString);
            }

            @Override
            protected void onBindFooterViewHolder(FooterViewHolder vh, int sectionIndex) {
                String valueAsString = String.format(Locale.US, "%02d", sectionIndex);
                vh.setValue("End of " + valueAsString);
            }
        });

        endTime = System.currentTimeMillis();
        Log.d(TAG, "Total execution time: " + (endTime-startTime) + "ms");
    }
}
