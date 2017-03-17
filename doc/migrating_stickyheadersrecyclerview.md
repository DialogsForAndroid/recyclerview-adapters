Migrating from StickyRecyclerHeaders to DynamicSectionsAdapter
==============================================================

## Why use DynamicSectionsAdapter?

 * Reuse a lot of code from StickyRecyclerHeaders
 * Less calls to getHeaderId (14 vs. ~150 for a 14 element list) in initial load
 * No calls to getHeaderId when scrolling
 * Allows (optional) headers and (optional) footers

## Why not DynamicSectionsAdapter?

 * It is fucking complex (but not complicated), so don't throw this on beginners
 * It is not stable and not well tested
 * It does not support stickyness
 * Minimal SDK version is 16

## Get started

 * Create ViewHolder for your headers, e.g.

```
public class ConversationsSectionHeaderViewHolder
    extends RecyclerView.ViewHolder {

    public ConversationsSectionHeaderViewHolder(View itemView) {
        super(itemView);
    }
}
```

 * Add original adapter and wrapper asl members

Before:

```
private ConversationsAdapter mAdapter;
```

After:

```
private ConversationsAdapter mConversationsAdapter;
private DynamicSectionsAdapter mAdapter;
```

 * Move your original adapter into new mConversationsAdapter

Before:

```
mAdapter = new ConversationsAdapter(this);
mRecyclerView.setAdapter(mAdapter);
```

After:

```
mConversationsAdapter = new ConversationsAdapter(this);
mAdapter = ???
mRecyclerView.setAdapter(mAdapter);
```

 * Create new DynamicSectionsAdapter. The three generics parameters are items view hold, headers view holder, footer view holders. Use type NullViewHolder if you don't use footters (or headers). Pass your original adapter to the constructor and make sure, that the view holder type of the original adapter matches the item view holder type.


```
mAdapter = new DynamicSectionsAdapter<ConversationViewHolder, ConversationsSectionHeaderViewHolder, NullViewHolder>(mConversationsAdapter) {

    @Override
    protected long getSectionId(int itemAdapterPosition) {
        // FIXME: do something useful
        return 0;
    }

    @Override
    protected boolean sectionHasHeader(long sectionId) {
        // FIXME: do something useful
        return false;
    }

    @Override
    protected ConversationsSectionHeaderViewHolder onCreateSectionHeaderViewHolder(ViewGroup parent) {
        // FIXME: do something useful
        return null;
    }

    @Override
    protected void onBindHeaderViewHolder(ConversationsSectionHeaderViewHolder vh, long sectionId) {
        // FIXME: do something useful
    }
};
```

 * Implement methods from above:

`protected long getSectionId(int originalAdapterPosition)`: Use implementation from
`StickyRecyclerHeadersAdapter.getHeaderId`, which is probably in your original adapter

`protected boolean sectionHasHeader(long sectionId)`: return constant true.

`protected ConversationsSectionHeaderViewHolder onCreateSectionHeaderViewHolder(ViewGroup parent)`: Inflate from xml
(see your `StickyRecyclerHeadersAdapter.onCreateHeaderViewHolder`, e.g.

```
@Override
protected ConversationsSectionHeaderViewHolder onCreateSectionHeaderViewHolder(ViewGroup parent) {
    View itemView = LayoutInflater.
        from(parent.getContext()).
        inflate(R.layout.row_messages_list_header, parent, false);
    return new ConversationHeaderViewHolder(itemView);
}
```

`protected void onBindHeaderViewHolder(ConversationsSectionHeaderViewHolder vh, long sectionId)`: Fill data into the
view holer (see your `StickyRecyclerHeadersAdapter.onBindHeaderViewHolder`. This is called once for every section,
not for every item. So you may need to use `getOriginalPositionForFirstItemInSection(sectionId)` to pull data out of
your original adapter. E.g.

```
@Override
protected void onBindHeaderViewHolder(ConversationsSectionHeaderViewHolder vh, long sectionId) {
    TextView textView = (TextView) vh.itemView;
    Long conversationId = mConversationsAdapter.getItem(getOriginalPositionForFirstItemInSection(sectionId));

    DateTime latestMessageTimestamp = SessionConnector.get().getLatestMessageTimestamp(conversationId);

    if (latestMessageTimestamp.getMillis() == SessionConnector.get().emptyConversationTimestamp().getMillis()) {
        textView.setText(R.string.empty_conversation_title);
    }
    else {
        textView.setText(Formatting.getLocalDateText(latestMessageTimestamp));
    }
}
```

* Remove old StickyRecyclerHeadersDecoration

Before:

```
final StickyRecyclerHeadersDecoration headersDecor = new StickyRecyclerHeadersDecoration(mAdapter);
mRecyclerView.addItemDecoration(headersDecor);
```

* For further adapter access from the recycler view you need to translate positions using, e.g.

Before:

```
long conversationId = mAdapter.getItem(position);
```

After:

```
long conversationId = mConversationsAdapter.getItem(mAdapter.getOriginalPositionForPosition(position));
```

#### Cleanup

Remove `implements StickyRecyclerHeadersAdapter<RecyclerView.ViewHolder>` from your original adapter.
This will make `getHeaderId(int)`, `onCreateHeaderViewHolder(ViewGroup)` and
`onBindHeaderViewHolder(RecyclerView.ViewHolder, int)` obsolete.

## Use of decorators (optional)

When using dividers, you can query `isHeader(int position)` and `isFooter(int position)`. In addition with
a isDecorated method, you can switch dividers on and off.

```
mRecyclerView.addItemDecoration(new DividerDecoration(this, dividerLeftMargin) {
    @Override
    protected boolean isDecorated(View view, RecyclerView parent) {
        int pos = mRecyclerView.getChildAdapterPosition(view);
        if (mAdapter.isHeader(pos)) {
            return true;
        }
        return false;
    }
});
```
