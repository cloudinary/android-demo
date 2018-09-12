package com.cloudinary.android.demo.app;

import java.util.List;

import androidx.recyclerview.widget.DiffUtil;

/**
 * Created by Nitzan Jaitman on 18/03/2018.
 */

/**
 * A diff calculator for objects implementing {@link Identifiable}.
 *
 * @param <T>
 */
public class IdentifiableDiff<T extends Identifiable> extends DiffUtil.Callback {

    private List<T> oldItems;
    private List<T> newItems;

    IdentifiableDiff(List<T> oldItems, List<T> newItems) {
        this.oldItems = oldItems;
        this.newItems = newItems;
    }

    @Override
    public int getOldListSize() {
        return oldItems.size();
    }

    @Override
    public int getNewListSize() {
        return newItems.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return oldItems.get(oldItemPosition).getId().equals(newItems.get(newItemPosition).getId());
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return oldItems.get(oldItemPosition).equals(newItems.get(newItemPosition));
    }
}
