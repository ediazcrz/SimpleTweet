package com.codepath.apps.restclienttemplate.models

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("name")
    val name: String,

    @SerializedName("screen_name")
    val screenName: String,

    @SerializedName("profile_image_url_https")
    val publicImageUrl: String
)
