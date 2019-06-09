package com.example.pagingwiki.data.datasource

import androidx.lifecycle.MutableLiveData
import androidx.paging.DataSource
import com.example.wikiappsearch11.model.Article
import com.example.wikiappsearch11.network.ArticleApi
import io.reactivex.disposables.CompositeDisposable

class ArticlesDataSourceFactory(private val compositeDisposable: CompositeDisposable,
                                private val articleApi: ArticleApi,
                                private val srsearch: String)
    : DataSource.Factory<Long, Article>() {

    val usersDataSourceLiveData = MutableLiveData<ArticlesDataSource>()

    override fun create(): DataSource<Long, Article> {
        val articlesDataSource =
            ArticlesDataSource(articleApi, compositeDisposable,srsearch)
        usersDataSourceLiveData.postValue(articlesDataSource)
        return articlesDataSource
    }
}