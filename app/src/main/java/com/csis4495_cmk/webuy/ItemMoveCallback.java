package com.csis4495_cmk.webuy;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.csis4495_cmk.webuy.adapters.SellerAddProductImagesAdapter;
import com.csis4495_cmk.webuy.adapters.SellerStyleListAdapter;

public class ItemMoveCallback extends ItemTouchHelper.Callback{
    private final ItemTouchHelperContract mAdapter;

    public ItemMoveCallback(ItemTouchHelperContract adapter) {
        mAdapter = adapter;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int dragFlags;
        if (mAdapter instanceof SellerAddProductImagesAdapter) {
            dragFlags = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT | ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        } else { //mAdapter instanceof SellerStyleListAdapter
            dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        }
        return makeMovementFlags(dragFlags, 0);
    }
    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                          RecyclerView.ViewHolder target) {
        mAdapter.onRowMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder,
                                  int actionState) {

        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            if (viewHolder instanceof SellerAddProductImagesAdapter.AddImagesViewHolder) {
                SellerAddProductImagesAdapter.AddImagesViewHolder myViewHolder =
                        (SellerAddProductImagesAdapter.AddImagesViewHolder) viewHolder;
                mAdapter.onRowSelected(myViewHolder);
            } else if (viewHolder instanceof SellerStyleListAdapter.StyleListViewHolder) {
                SellerStyleListAdapter.StyleListViewHolder myViewHolder =
                        (SellerStyleListAdapter.StyleListViewHolder)  viewHolder;
                mAdapter.onRowSelected(myViewHolder);
            }

        }

        super.onSelectedChanged(viewHolder, actionState);
    }

    @Override
    public void clearView(RecyclerView recyclerView,
                          RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);

        if (viewHolder instanceof SellerAddProductImagesAdapter.AddImagesViewHolder) {
            SellerAddProductImagesAdapter.AddImagesViewHolder myViewHolder=
                    (SellerAddProductImagesAdapter.AddImagesViewHolder) viewHolder;
            mAdapter.onRowClear(myViewHolder);
        }
    }

    public interface ItemTouchHelperContract<T> {
        void onRowMoved(int fromPosition, int toPosition);
        //void onRowSelected(SellerAddProductImagesAdapter.AddImagesViewHolder myViewHolder);
        void onRowSelected(T myViewHolder);
        //void onRowClear(SellerAddProductImagesAdapter.AddImagesViewHolder myViewHolder);
        void onRowClear(T myViewHolder);
    }

}
