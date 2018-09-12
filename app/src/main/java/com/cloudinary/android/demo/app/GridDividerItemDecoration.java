package com.cloudinary.android.demo.app;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

/**
 * Created by Nitzan Jaitman on 08/02/2018.
 */

public class GridDividerItemDecoration extends RecyclerView.ItemDecoration {
    private final int dividerWidth;
    private final int dividerHeight;

    public GridDividerItemDecoration(int dividerWidth, int dividerHeight) {
        this.dividerWidth = dividerWidth;
        this.dividerHeight = dividerHeight;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.top = dividerHeight;
        outRect.bottom = dividerHeight;
        outRect.right = dividerWidth;
        outRect.left = dividerWidth;
    }
}

