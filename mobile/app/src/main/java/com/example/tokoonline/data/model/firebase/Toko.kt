package com.example.tokoonline.data.model.firebase

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.io.Serializable


@Parcelize
data class Toko (
    var id: String? = null,
    val nama: String = "",
    val id_alamat: String = "",
    val alamat : Alamat,
    val id_users : String = "",
) : Parcelable, Serializable {
    constructor() : this("", "", "", Alamat(), "")
    fun toMap(): Map<String, Any?> {
        val gson = Gson()
        val json = gson.toJson(this)
        return gson.fromJson(json, object : TypeToken<Map<String, Any>>() {}.type)
    }
}