package com.example.tokoonline.data.model

import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.io.Serializable

@Parcelize
data class Produk(
    @SerializedName("image")
    val image: String = "",
    @SerializedName("nama")
    var nama: String = "",
    @SerializedName("keyword")
    var keyword: String = "",
    @SerializedName("harga")
    var harga: Long = 0,
    @SerializedName("deskripsi")
    val deskripsi: String = "",
    @SerializedName("id_users")
    val idUser: String? = "",
) : Parcelable, Serializable {
    fun toMap(): Map<String, Any?> {
        val gson = Gson()
        val json = gson.toJson(this)
        return gson.fromJson(json, object : TypeToken<Map<String, Any>>() {}.type)
    }

    fun toProdukKeranjang(): ProdukKeranjang {
        return ProdukKeranjang(
            image = this.image,
            nama = this.nama,
            harga = this.harga,
            deskripsi = this.deskripsi,
            id_users = this.idUser,
        )
    }
}