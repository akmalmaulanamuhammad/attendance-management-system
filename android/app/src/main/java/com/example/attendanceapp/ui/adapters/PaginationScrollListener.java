package com.example.attendanceapp.ui.adapters;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public abstract class PaginationScrollListener extends RecyclerView.OnScrollListener {
    private final LinearLayoutManager layoutManager;
    private static final int PAGE_SIZE = 10;
    private static final int VISIBLE_THRESHOLD = 5;

    protected PaginationScrollListener(LinearLayoutManager layoutManager) {
        this.layoutManager = layoutManager;
    }

    @Override
    public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        int totalItemCount = layoutManager.getItemCount();
        int lastVisibleItem = layoutManager.findLastVisibleItemPosition();

        if (!isLoading() && !isLastPage() && 
            lastVisibleItem + VISIBLE_THRESHOLD >= totalItemCount) {
            loadMoreItems();
        }
    }

    protected abstract void loadMoreItems();
    public abstract boolean isLastPage();
    public abstract boolean isLoading();
}
