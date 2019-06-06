package com.iterika.marvel.utils

import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView


private const val VISIBLE_THRESHOLD = 5

class EndlessRecyclerOnScrollListener(private val listener: () -> Unit) : RecyclerView.OnScrollListener() {

    private var previousTotalItemCount = 0
    private var loadingMode = true

    override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
        val layoutManager = recyclerView.layoutManager as GridLayoutManager?
        val visibleItemCount = layoutManager?.childCount ?: 0
        val totalItemCount = layoutManager?.itemCount ?: 0
        val firstVisibleItemPosition = layoutManager?.findFirstVisibleItemPosition() ?: 0
        if (newState == RecyclerView.SCROLL_STATE_SETTLING && visibleItemCount != 0) {
            if (loadingMode) {
                if (totalItemCount > previousTotalItemCount) {
                    loadingMode = false
                    previousTotalItemCount = totalItemCount
                }
            }
            if (!loadingMode && visibleItemCount + firstVisibleItemPosition >= totalItemCount - VISIBLE_THRESHOLD) {
                loadingMode = true
                listener.invoke()
            }
        }
    }
}