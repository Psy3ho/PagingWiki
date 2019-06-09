package com.example.wikiappsearch11.model

    data class Article(
        val id: Long,
        val title: String,
        val snippet: String,
        val information: String,
        val language: String
    )

    data class Data(
        val query: Children
    )

    data class Children(
        val search: List<Article>
    )
