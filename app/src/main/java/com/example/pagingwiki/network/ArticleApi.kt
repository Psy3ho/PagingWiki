package com.example.wikiappsearch11.network

import com.example.wikiappsearch11.model.Data
import io.reactivex.Single
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

interface ArticleApi {
    @GET("api.php?action=query&list=search&utf8=&format=json")
    fun getArticles(@Query("sroffset") sroffset: Long,@Query("srlimit") srlimit: Int,@Query("srsearch") srsearch: String): Single<Data>

    companion object {
        fun getService(): ArticleApi {
            val retrofit = Retrofit.Builder()
                .baseUrl("https://en.wikipedia.org/w/")
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return retrofit.create(ArticleApi::class.java)
        }
    }
}