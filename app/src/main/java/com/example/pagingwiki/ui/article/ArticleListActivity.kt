package com.example.wikiappsearch11.ui.article

import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.paging.PagedList
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pagingwiki.R
import com.example.pagingwiki.adapter.ArticleListAdapter
import com.example.pagingwiki.data.NetworkState
import com.example.pagingwiki.data.Status
import com.example.pagingwiki.ui.article.ArticlesViewModel
import com.example.wikiappsearch11.model.Article
import kotlinx.android.synthetic.main.article_list.*
import kotlinx.android.synthetic.main.network_state_item.*

class ArticleListActivity: AppCompatActivity() {

    private lateinit var articlesViewModel: ArticlesViewModel

    private lateinit var articleListAdapter: ArticleListAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.article_list)
        articlesViewModel = ViewModelProviders.of(this).get(ArticlesViewModel::class.java)

        initSearch()
    }




    private fun initAdapter() {
        val linearLayoutManager = LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        articleListAdapter = ArticleListAdapter {
            articlesViewModel.retry()
        }
        articlesRecyclerView.layoutManager = linearLayoutManager
        articlesRecyclerView.adapter = articleListAdapter
        articlesViewModel.articlesList.observe(this, Observer<PagedList<Article>> { articleListAdapter.submitList(it)})
        articlesViewModel.getNetworkState().observe(this, Observer<NetworkState> { articleListAdapter.setNetworkState(it) })

    }

    private fun initSwipeToRefresh() {
        articlesViewModel.getRefreshState().observe(this, Observer { networkState ->
            if (articleListAdapter.currentList != null) {
                if (articleListAdapter.currentList!!.size > 0) {
                    articlesSwipeRefreshLayout.isRefreshing = networkState?.status == NetworkState.LOADING.status
                } else {
                    setInitialLoadingState(networkState)
                }
            } else {
                setInitialLoadingState(networkState)
            }
        })
        articlesSwipeRefreshLayout.setOnRefreshListener({ articlesViewModel.refresh() })
    }

    private fun setInitialLoadingState(networkState: NetworkState?) {
        //error message
        error_msg.visibility = if (networkState?.message != null) View.VISIBLE else View.GONE
        if (networkState?.message != null) {
            error_msg.text = networkState.message
        }

        //loading and retry
        retry_button.visibility = if (networkState?.status == Status.FAILED) View.VISIBLE else View.GONE
        progress_bar.visibility = if (networkState?.status == Status.RUNNING) View.VISIBLE else View.GONE

        articlesSwipeRefreshLayout.isEnabled = networkState?.status == Status.SUCCESS
        retry_button.setOnClickListener { articlesViewModel.retry() }
    }

    private fun initSearch() {
        input.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_GO) {
                updatedSubredditFromInput()
                true
            } else {
                false
            }
        }
        input.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                updatedSubredditFromInput()
                true
            } else {
                false
            }
        }
    }

    private fun updatedSubredditFromInput() {
        input.text.trim().toString().let {
            if (it.isNotEmpty()) {
                articlesViewModel.newinit(input.text.trim().toString())

                Log.d("napisane = ", input.text.trim().toString())
                initAdapter()
                initSwipeToRefresh()
            }
        }
    }





}