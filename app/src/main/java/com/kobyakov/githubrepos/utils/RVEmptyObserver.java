package com.kobyakov.githubrepos.utils;

import android.support.v7.widget.RecyclerView;
import android.view.View;

public class RVEmptyObserver extends RecyclerView.AdapterDataObserver {
    private View emptyView;
    private View filledView;
    private RecyclerView recyclerView;

    public RVEmptyObserver(RecyclerView recyclerView, View emptyView, View filledView) {
        this.recyclerView = recyclerView;
        this.emptyView = emptyView;
        this.filledView = filledView;
        checkIfEmpty();
    }

    private void checkIfEmpty() {
        if (emptyView != null && recyclerView.getAdapter() != null) {
            boolean emptyViewVisible = recyclerView.getAdapter().getItemCount() == 0;
            emptyView.setVisibility(emptyViewVisible ? View.VISIBLE : View.GONE);
            filledView.setVisibility(emptyViewVisible ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    public void onChanged() {
        checkIfEmpty();
    }

    @Override
    public void onItemRangeInserted(int positionStart, int itemCount) {
        checkIfEmpty();
    }

    @Override
    public void onItemRangeRemoved(int positionStart, int itemCount) {
        checkIfEmpty();
    }
}