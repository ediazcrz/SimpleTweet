package com.codepath.apps.restclienttemplate.models

import com.google.gson.annotations.SerializedName

data class Tweet(
    @SerializedName("text")
    val body: String,

    @SerializedName("created_at")
    val createdAt: String,

    @SerializedName("id")
    val id: Long,

    @SerializedName("user")
    val user: User
)
