package com.example.pagingwiki.data.datasource

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.paging.ItemKeyedDataSource
import com.example.pagingwiki.data.NetworkState
import com.example.wikiappsearch11.model.Article
import com.example.wikiappsearch11.network.ArticleApi
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Action
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class ArticlesDataSource(
    private val articleApi: ArticleApi,
    private val compositeDisposable: CompositeDisposable,
    private val srsearch: String

)
    : ItemKeyedDataSource<Long, Article>() {


    val networkState = MutableLiveData<NetworkState>()

    val initialLoad = MutableLiveData<NetworkState>()

    var page: Long = 1



    private var retryCompletable: Completable? = null

    fun retry() {
        if (retryCompletable != null) {
            compositeDisposable.add(retryCompletable!!
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({ }, { throwable -> Timber.e(throwable.message) }))
        }
    }

    override fun loadInitial(
        params: LoadInitialParams<Long>,
        callback: LoadInitialCallback<Article>) {

        networkState.postValue(NetworkState.LOADING)
        initialLoad.postValue(NetworkState.LOADING)

        compositeDisposable.add(articleApi.getArticles(0,params.requestedLoadSize, srsearch).subscribe({ articles ->

            Log.d("chyba = ", articles.toString())
            setRetry(null)
            networkState.postValue(NetworkState.LOADED)
            initialLoad.postValue(NetworkState.LOADED)
            callback.onResult(articles.query.search)// tu dostanem JSON data a potrebujem z toho dostat DATA.query.search
        }, { throwable ->
            Log.d("chyba = ", throwable.toString())
            setRetry(Action { loadInitial(params, callback) })
            val error = NetworkState.error(throwable.message)
            // publish the error
            networkState.postValue(error)
            initialLoad.postValue(error)
        }))
    }


    override fun loadAfter(params: LoadParams<Long>, callback: LoadCallback<Article>) {


        networkState.postValue(NetworkState.LOADING)
        page +=15

        compositeDisposable.add(articleApi.getArticles(page, params.requestedLoadSize, srsearch).subscribe({ articles ->
            Log.d("kluc = ", page.toString())
            setRetry(null)
            networkState.postValue(NetworkState.LOADED)
            callback.onResult(articles.query.search)
        }, { throwable ->
            setRetry(Action { loadAfter(params, callback) })
            // publish the error
            networkState.postValue(NetworkState.error(throwable.message))
        }))
    }

    override fun loadBefore(params: LoadParams<Long>, callback: LoadCallback<Article>) {

    }

    override fun getKey(item: Article): Long {
        return item.id
    }

    private fun setRetry(action: Action?) {
        if (action == null) {
            this.retryCompletable = null
        } else {
            this.retryCompletable = Completable.fromAction(action)
        }
    }

}