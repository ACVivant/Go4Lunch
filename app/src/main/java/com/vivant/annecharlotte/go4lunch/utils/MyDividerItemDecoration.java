package com.vivant.annecharlotte.go4lunch.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Add a line between item in RecyclerView
 */
public class MyDividerItemDecoration extends RecyclerView.ItemDecoration {

    private static final int[] ATTRS = new int[]{
            android.R.attr.listDivider
    };

    private Drawable mDivider;

    public MyDividerItemDecoration(Context context) {
        // get the attributes style
        final TypedArray a = context.obtainStyledAttributes(ATTRS);
        // get drawable in style
        mDivider = a.getDrawable(0);
        a.recycle();
    }

    @Override
    public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        // get padding of parents (Left and right)
        final int left = parent.getPaddingLeft()+225;
        final int right = parent.getWidth() - parent.getPaddingRight();

        // get the count of child
        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            // create layoutParams
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child
                    .getLayoutParams();
            // calculate new padding child
            final int top = child.getBottom() + params.bottomMargin;
            final int bottom = top + mDivider.getIntrinsicHeight();
            // set bounds
            mDivider.setBounds(left, top, right, bottom);
            // draw in canvas
            mDivider.draw(c);
        }
    }

    // Return the dimension outRect for itemPosition and parent (Offset)
    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        outRect.set(0, 0, 0, mDivider.getIntrinsicHeight());
    }
}
