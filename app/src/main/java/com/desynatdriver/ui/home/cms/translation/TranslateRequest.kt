package com.desynatdriver.ui.home.cms.translation

import com.google.gson.annotations.SerializedName

data class TranslateRequest(

    @SerializedName("q")
    val q: String,

    @SerializedName("source")
    val source: String,

    @SerializedName("target")
    val target: String,

    @SerializedName("format")
    val format: String = "text"
)
