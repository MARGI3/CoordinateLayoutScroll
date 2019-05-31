package com.demo.project;

import android.content.Context;
import android.support.annotation.Keep;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import java.util.ArrayList;
import java.util.List;

@Keep
public class StickyBottomScrollingBehavior extends AppBarLayout.ScrollingViewBehavior {

    ArrayList<View> mPinnedViewList = new ArrayList<>();

    public StickyBottomScrollingBehavior() {
    }

    public StickyBottomScrollingBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, View child, View dependency) {
        super.onDependentViewChanged(parent, child, dependency);
        updatePinnedOffset(parent, child, dependency);
        return false;
    }

    @Override
    public boolean onLayoutChild(CoordinatorLayout parent, View child, int layoutDirection) {
        super.onLayoutChild(parent, child, layoutDirection);
        final List<View> dependencies = parent.getDependencies(child);
        for (int i = 0, z = dependencies.size(); i < z; i++) {
            if (updatePinnedOffset(parent, child, dependencies.get(i))) {
                // If we updated the offset, break out of the loop now
                break;
            }
        }
        return true;
    }

    private boolean updatePinnedOffset(CoordinatorLayout parent, View child, View dependency) {
        CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) dependency.getLayoutParams();
        CoordinatorLayout.Behavior behavior = params.getBehavior();
        int offset = 0;
        boolean isAppBar = false;
        if (behavior instanceof AppBarLayout.Behavior) {
            offset = ((AppBarLayout.Behavior) behavior).getTopAndBottomOffset();
            isAppBar = true;
        }
        int scrollRange = 0;
        if (dependency instanceof AppBarLayout) {
            scrollRange = ((AppBarLayout) dependency).getTotalScrollRange();
        }
        for (View view : mPinnedViewList) {
            if (!layoutDependsOn(parent, child, view)
                    && ViewCompat.isLaidOut(view)
                    && isViewInParent(parent, view, child)) {
                View directParent = (View) view.getParent();
                if (directParent != null) {
                    ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
                    view.offsetTopAndBottom(offset);
                    int bottom = directParent.getHeight() - (scrollRange + offset) - lp.bottomMargin;
                    int top = bottom - view.getHeight() - lp.topMargin;
                    view.layout(view.getLeft(), top, view.getRight(), bottom);
                }
            }
        }
        return isAppBar;
    }

    private boolean isViewInParent(CoordinatorLayout root, View view, View parent) {
        ViewParent p = view.getParent();
        if (p == parent)
            return true;
        while (p != root && p != null) {
            if (p instanceof View) {
                p = p.getParent();
            }
            if (p == parent)
                return true;
        }
        return false;
    }


    public void addPinnedView(View view) {
        if (view != null && !mPinnedViewList.contains(view)) {
            mPinnedViewList.add(view);
        }
    }

    public void removePinnedView(View view) {
        if (view != null && mPinnedViewList.contains(view)) {
            mPinnedViewList.remove(view);
        }
    }

    public static <V extends View> StickyBottomScrollingBehavior from(V view) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        if (!(params instanceof CoordinatorLayout.LayoutParams)) {
            throw new IllegalArgumentException("The view is not a child of CoordinatorLayout");
        }
        CoordinatorLayout.Behavior behavior = ((CoordinatorLayout.LayoutParams) params)
                .getBehavior();
        if (!(behavior instanceof StickyBottomScrollingBehavior)) {
            throw new IllegalArgumentException(
                    "The view is not associated with ScrollingViewBehavior");
        }
        return (StickyBottomScrollingBehavior) behavior;
    }
}
