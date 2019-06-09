package com.example.pagingwiki.ui.article

import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.paging.LivePagedListBuilder
import androidx.paging.PagedList
import com.example.pagingwiki.data.NetworkState
import com.example.pagingwiki.data.datasource.ArticlesDataSource
import com.example.pagingwiki.data.datasource.ArticlesDataSourceFactory
import com.example.wikiappsearch11.model.Article
import com.example.wikiappsearch11.network.ArticleApi
import io.reactivex.disposables.CompositeDisposable

class ArticlesViewModel : ViewModel() {

    var articlesList: LiveData<PagedList<Article>>

    private val compositeDisposable = CompositeDisposable()

    private val pageSize = 15

    private var sourceFactory: ArticlesDataSourceFactory

    var inputSearch: String = "Ahoj"


    init {
        sourceFactory = ArticlesDataSourceFactory(
            compositeDisposable,
            ArticleApi.getService(),
            inputSearch

        )

        val config = PagedList.Config.Builder()
            .setPageSize(pageSize)
            .setInitialLoadSizeHint(pageSize * 2)
            .setEnablePlaceholders(false)
            .build()
        articlesList = LivePagedListBuilder<Long, Article>(sourceFactory, config).build()
    }

    fun newinit(srsearch: String) {
        sourceFactory = ArticlesDataSourceFactory(
            compositeDisposable,
            ArticleApi.getService(),
            srsearch

        )
        val config = PagedList.Config.Builder()
            .setPageSize(pageSize)
            .setInitialLoadSizeHint(pageSize * 2)
            .setEnablePlaceholders(false)
            .build()
        articlesList = LivePagedListBuilder<Long, Article>(sourceFactory, config).build()
    }


    fun retry() {
        sourceFactory.usersDataSourceLiveData.value!!.retry()
    }
    fun refresh() {
        sourceFactory.usersDataSourceLiveData.value!!.invalidate()
    }

    fun getNetworkState(): LiveData<NetworkState> = Transformations.switchMap<ArticlesDataSource, NetworkState>(
        sourceFactory.usersDataSourceLiveData, { it.networkState })

    fun getRefreshState(): LiveData<NetworkState> = Transformations.switchMap<ArticlesDataSource, NetworkState>(
        sourceFactory.usersDataSourceLiveData, { it.initialLoad })

    override fun onCleared() {
        super.onCleared()
        compositeDisposable.dispose()
    }
}