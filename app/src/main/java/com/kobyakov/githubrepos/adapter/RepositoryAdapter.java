package com.kobyakov.githubrepos.adapter;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.kobyakov.githubrepos.ILoadMore;
import com.kobyakov.githubrepos.R;
import com.kobyakov.githubrepos.model.Repository;

import java.util.List;

public class RepositoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private ILoadMore loadMore;
    private boolean isLoading;
    private Activity activity;
    private List<Repository> repositories;
    private int visibleThreshold = 5;
    private int lastVisibleItem, totalItemCount;

    public RepositoryAdapter(RecyclerView recyclerView, Activity activity) {
        this.activity = activity;

        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                totalItemCount = linearLayoutManager.getItemCount();
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();

                if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                    if (loadMore != null) {
                        loadMore.onLoadMore();
                        isLoading = true;
                    }
                }
            }
        });
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
    }

    public void setData(List<Repository> repositories) {
        this.repositories = repositories;
        notifyDataSetChanged();
    }

    @Override
    public int getItemViewType(int position) {
        return repositories.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    public void setLoadMore(ILoadMore loadMore) {
        this.loadMore = loadMore;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (i == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(activity).inflate(R.layout.repository_list_row, viewGroup, false);
            return new ItemViewHolder(view);
        } else {
            View view = LayoutInflater.from(activity).inflate(R.layout.item_loading, viewGroup, false);
            return new LoadingViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int i) {
        if (holder instanceof ItemViewHolder) {
            Repository repository = repositories.get(i);
            String repositoryDesc = repository.getDescription() != null ? repository.getDescription() : activity.getString(R.string.no_desc);

            ItemViewHolder viewHolder = (ItemViewHolder) holder;
            viewHolder.name.setText(repository.getName());
            viewHolder.length.setText(repositoryDesc);
        } else if (holder instanceof LoadingViewHolder) {
            LoadingViewHolder loadingViewHolder = (LoadingViewHolder) holder;
            loadingViewHolder.progressBar.setIndeterminate(true);
        }
    }

    @Override
    public int getItemCount() {
        if (repositories != null) {
            return repositories.size();
        }
        return 0;
    }

    public void setLoaded() {
        isLoading = false;
    }

    class LoadingViewHolder extends RecyclerView.ViewHolder {

        ProgressBar progressBar;

        LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.progress_bar);
        }
    }

    class ItemViewHolder extends RecyclerView.ViewHolder {

        TextView name, length;

        ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.repository_name);
            length = itemView.findViewById(R.id.repository_desc);
        }
    }
}