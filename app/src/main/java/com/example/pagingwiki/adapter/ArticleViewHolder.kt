package com.example.pagingwiki.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.pagingwiki.R
import com.example.wikiappsearch11.model.Article
import kotlinx.android.synthetic.main.article_item.view.*

class ArticleViewHolder(view: View) : RecyclerView.ViewHolder(view) {

    fun bindTo(article: Article?) {
        itemView.title.text = article?.title
        itemView.snippet.text = article?.snippet
    }

    companion object {
        fun create(parent: ViewGroup): ArticleViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)
            val view = layoutInflater.inflate(R.layout.article_item, parent, false)
            return ArticleViewHolder(view)
        }
    }

}