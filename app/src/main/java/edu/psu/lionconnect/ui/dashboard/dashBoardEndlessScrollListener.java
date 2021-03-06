package edu.psu.lionconnect.ui.dashboard;

import android.util.Log;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public abstract class dashBoardEndlessScrollListener extends RecyclerView.OnScrollListener {
    private int visibleThreshold = 1;

    private int currentPage = 0;

    private int previousTotalItemCount = 0;

    private boolean loading = false;

    private int startingPageIndex = 0;

    private DashboardViewModel model;

    RecyclerView.LayoutManager mLayoutManager;

    public dashBoardEndlessScrollListener(LinearLayoutManager layoutManager) {
        this.mLayoutManager = layoutManager;
    }

    public dashBoardEndlessScrollListener(DashboardViewModel model) {
        this.model = model;
        this.mLayoutManager = model.llm;
    }

    @Override
    public void onScrolled(RecyclerView view, int dx, int dy) {
        synchronized(this){
            int lastVisibleItemPosition = 0;
            int totalItemCount = mLayoutManager.getItemCount();

            lastVisibleItemPosition = ((LinearLayoutManager) mLayoutManager).findLastVisibleItemPosition();

            if (totalItemCount < previousTotalItemCount) {
                this.currentPage = this.startingPageIndex;
                this.previousTotalItemCount = totalItemCount;
                if (totalItemCount == 0 && !model.done) {
                    this.loading = true;
                }
            }

            if (loading && (totalItemCount > previousTotalItemCount)) {
                loading = false;
                previousTotalItemCount = totalItemCount;
            }

            Log.i("lasVisibleItemPositon ", lastVisibleItemPosition+"");
            Log.i("Loading ", loading+"");
            Log.i("Done? ", model.done+"");
            Log.i("totalItemCount ", totalItemCount+"");
            if (!loading && (lastVisibleItemPosition + visibleThreshold) >= totalItemCount && !model.done && lastVisibleItemPosition != 0) {
                model.recList.post(new Runnable() {
                    public void run() {
                        onLoadMore(model);
                    }
                });
                loading = true;
            }
        }
    }

    public void resetState() {
        this.currentPage = this.startingPageIndex;
        this.previousTotalItemCount = 0;
        this.loading = true;
    }

    public abstract void onLoadMore(DashboardViewModel model);
}
