package com.app.azovatask.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import io.realm.RealmObject
import io.realm.annotations.Ignore
import io.realm.annotations.PrimaryKey
import java.util.*

open class NewsDataModel : RealmObject() {
    @PrimaryKey
    @SerializedName("title")
    @Expose
    var header: String? = null
    var author: String? = null
    var description: String? = null
    var urlToImage: String? = null

    @Ignore
    var publishedAt: String? = null
    var pub_date: Date? = null
}