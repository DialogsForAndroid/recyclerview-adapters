package io.github.dialogsforandroid.recyclerviewadapters.library;

import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * A proxy adapter used to partition the original data into sections and provide an optional
 * header and footer for each section.
 *
 * Use this class when the sections are dynamically generated from the data set (e.g. initial
 * letters of names).
 *
 * Performance considerations: Only use this class when the sections are dynamically generated out
 * of the given data set. Otherwise use StaticSectionsAdapter.
 * To calculate final positions, {@link #getSectionId(int) getSectionId} is called for every
 * position in the original data set in advance, so ensure your implementation is very fast.
 * All design decisions assume that the number of sections is relatively small compared to the
 * number of items.
 *
 * @param <ItemViewHolder>
 * @param <SectionHeaderViewHolder>
 * @param <SectionFooterViewHolder>
 */
@SuppressWarnings({"WeakerAccess", "UnusedParameters", "unused"})
abstract public class DynamicSectionsAdapter<
        ItemViewHolder extends RecyclerView.ViewHolder,
        SectionHeaderViewHolder extends RecyclerView.ViewHolder,
        SectionFooterViewHolder extends RecyclerView.ViewHolder
        > extends RecyclerView.Adapter<WrapperViewHolder<ItemViewHolder, SectionHeaderViewHolder, SectionFooterViewHolder>> {

    private static final String TAG = "DynamicSectionsAdapter";

    private RecyclerView.Adapter<ItemViewHolder> mItemsAdapter;

    /**
     * Integer values in storage
     *   -2: element is a header
     *   -1: element is a footer
     * >= 0: element is an item from itemsAdapter
     *
     * When element is an item from itemsAdapter, storage stores the position in the itemsAdapter.
     * When element is a header of footer, supplementaryElementSectionIds stores the corresponding
     * section id.
     */
    private static final int ELEMENT_IS_HEADER = -2;
    private static final int ELEMENT_IS_FOOTER = -1;
    private ArrayList<Pair<Integer, Long>> mIndex = new ArrayList<>();

    // leave non-negative view type ids for the original adapter
    private static final int VIEW_TYPE_HEADER = -1;
    private static final int VIEW_TYPE_FOOTER = -2;

    @SuppressWarnings("WeakerAccess")
    public DynamicSectionsAdapter(RecyclerView.Adapter<ItemViewHolder> itemsAdapter) {
        mItemsAdapter = itemsAdapter;
        rebuildIndex();
        connectAdapter();
    }

    private void rebuildIndex() {
        Long lastSection = null;
        int itemsAdapterItemsCount = mItemsAdapter.getItemCount();
        for (int itemsAdapterPos = 0; itemsAdapterPos < itemsAdapterItemsCount; ++itemsAdapterPos) {
            Long itemSection = getSectionId(itemsAdapterPos);

            if (!itemSection.equals(lastSection)) {
                if (lastSection != null && sectionHasFooter(lastSection)) {
                    mIndex.add(new Pair<>(ELEMENT_IS_FOOTER, lastSection));
                }

                if (sectionHasHeader(itemSection)) {
                    mIndex.add(new Pair<>(ELEMENT_IS_HEADER, itemSection));
                }

                lastSection = itemSection;
            }

            mIndex.add(new Pair<>(itemsAdapterPos, itemSection));

            boolean isLastElement = (itemsAdapterPos == (itemsAdapterItemsCount-1));
            if (isLastElement) {
                if (sectionHasFooter(itemSection)) {
                    mIndex.add(new Pair<>(ELEMENT_IS_FOOTER, itemSection));
                }
            }
        }
    }

    /**
     * A sectionId must uniquely identify a section.
     *
     * Ids need not be contiguous or sorted.
     *
     * This is called for every element in the list whenever data changes, so make sure your
     * implementation is fast
     */
    protected abstract long getSectionId(int originalAdapterPosition);
    protected boolean sectionHasHeader(long sectionId) { return false; }
    protected boolean sectionHasFooter(long sectionId) { return false; }
    protected SectionHeaderViewHolder onCreateSectionHeaderViewHolder(ViewGroup parent) { return null; }
    protected SectionFooterViewHolder onCreateSectionFooterViewHolder(ViewGroup parent) { return null; }
    protected void onBindHeaderViewHolder(SectionHeaderViewHolder vh, long sectionId) {}
    protected void onBindFooterViewHolder(SectionFooterViewHolder vh, long sectionId) {}

    public int getOriginalPositionForPosition(int position) {
        return mIndex.get(position).first;
    }

    public int getOriginalPositionForFirstItemInSection(long sectionId) {
        for (Pair<Integer, Long> element : mIndex) {
            if (element.first != ELEMENT_IS_HEADER
                && element.first != ELEMENT_IS_FOOTER
                && element.second == sectionId) {
                return element.first;
            }
        }
        return -1;
    }

    @Override
    final public int getItemViewType(int position) {
        int content = mIndex.get(position).first;
        switch (content) {
            case ELEMENT_IS_HEADER:
                return VIEW_TYPE_HEADER;
            case ELEMENT_IS_FOOTER:
                return VIEW_TYPE_FOOTER;
            default:
                int viewType = mItemsAdapter.getItemViewType(content /* = position in items adapter */);
                if (viewType < 0) {
                    throw new AssertionError("getItemViewType() must return a value >= 0");
                }
                return viewType;
        }
    }

    @Override
    final public WrapperViewHolder<ItemViewHolder, SectionHeaderViewHolder, SectionFooterViewHolder>
    onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case VIEW_TYPE_HEADER: {
                SectionHeaderViewHolder viewHolder = onCreateSectionHeaderViewHolder(parent);
                return new WrapperViewHolder<>(WrapperViewHolder.Type.Header, viewHolder);
            }
            case VIEW_TYPE_FOOTER: {
                SectionFooterViewHolder viewHolder = onCreateSectionFooterViewHolder(parent);
                return new WrapperViewHolder<>(WrapperViewHolder.Type.Footer, viewHolder);
            }
            default: {
                ItemViewHolder viewHolder = mItemsAdapter.onCreateViewHolder(parent, viewType);
                return new WrapperViewHolder<>(WrapperViewHolder.Type.Item, viewHolder);
            }
        }
    }

    @Override
    final public void onBindViewHolder(WrapperViewHolder<ItemViewHolder, SectionHeaderViewHolder, SectionFooterViewHolder> holder, int position) {
        Pair<Integer, Long> element = mIndex.get(position);
        int originalAdapterPosition = element.first;
        long sectionId = element.second;
        switch (originalAdapterPosition) {
            case ELEMENT_IS_HEADER: {
                onBindHeaderViewHolder(holder.headerViewHolder, sectionId);
            }
            break;
            case ELEMENT_IS_FOOTER: {
                onBindFooterViewHolder(holder.footerViewHolder, sectionId);
            }
            break;
            default: {
                mItemsAdapter.onBindViewHolder(
                    holder.itemViewHolder,
                    originalAdapterPosition);
            }
        }
    }

    @Override
    final public int getItemCount() {
        return mIndex.size();
    }

    private void connectAdapter() {
        mItemsAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
                @Override
                public void onChanged() {
                    rebuildIndex();
                    DynamicSectionsAdapter.this.notifyDataSetChanged();
                }

                @Override
                public void onItemRangeChanged(int positionStart, int itemCount) {
                    int targetAdapterPositionStart = calculatePosition(positionStart);
                    DynamicSectionsAdapter.this.notifyItemRangeChanged(targetAdapterPositionStart, itemCount);
                }

                @Override
                public void onItemRangeInserted(int positionStart, int itemCount) {
                    int targetAdapterPositionStart = calculatePosition(positionStart);
                    DynamicSectionsAdapter.this.notifyItemRangeInserted(targetAdapterPositionStart, itemCount);
                }

                @Override
                public void onItemRangeRemoved(int positionStart, int itemCount) {
                    int targetAdapterPositionStart = calculatePosition(positionStart);
                    DynamicSectionsAdapter.this.notifyItemRangeRemoved(targetAdapterPositionStart, itemCount);
                }

                @Override
                public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
                    if (itemCount != 1) {
                        throw new AssertionError(
                            "onItemRangeMoved with itemCount other than 1 is not supported. " +
                                "This is because RecyclerView.Adapter.notifyItemMove does not have a count parameter. " +
                                "No idea how to implement that. Feel free to submit a PR.");
                    }
                    int targetAdapterFromPosition = calculatePosition(fromPosition);
                    int targetAdapterToPosition = calculatePosition(toPosition);
                    DynamicSectionsAdapter.this.notifyItemMoved(
                        targetAdapterFromPosition, targetAdapterToPosition);
                }
            });
    }

    private int calculatePosition(int originalAdapterPosition) {
        // this could be implemented using binary search
        Pair<Integer, Long> element;
        for (int key = 0; key < mIndex.size(); ++key) {
            element = mIndex.get(key);
            if (element.first == originalAdapterPosition) return key;
        }
        return -1;
    }

    public boolean isHeader(int pos) {
        Pair<Integer, Long> element = mIndex.get(pos);
        return element.first == ELEMENT_IS_HEADER;
    }

    public boolean isFooter(int pos) {
        Pair<Integer, Long> element = mIndex.get(pos);
        return element.first == ELEMENT_IS_FOOTER;
    }
}
