package com.codepath.apps.restclienttemplate.models

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Tweet(
    @SerializedName("text")
    val body: String,

    @SerializedName("created_at")
    val createdAt: String,

    @SerializedName("id")
    val id: Long,

    @SerializedName("user")
    val user: User
) : Parcelable
