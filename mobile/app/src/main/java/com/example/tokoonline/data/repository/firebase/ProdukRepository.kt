package com.example.tokoonline.data.repository.firebase

import com.example.tokoonline.core.constanst.Constant
import com.example.tokoonline.core.util.multiValueListenerFlow
import com.example.tokoonline.core.util.toProdukDomain
import com.example.tokoonline.data.model.firebase.Alamat
import com.example.tokoonline.data.model.firebase.Produk
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ProdukRepository {
    private val databaseReference: DatabaseReference =
        FirebaseDatabase.getInstance().getReference(Constant.REFERENCE_PRODUK)

    companion object {
        @Volatile
        private var INSTANCE: ProdukRepository? = null

        fun getInstance(): ProdukRepository {
            return INSTANCE ?: synchronized(this) {

                val instance = ProdukRepository()
                INSTANCE = instance
                instance
            }
        }
    }

    fun loadProduk(): Flow<List<Produk?>> {
        return databaseReference.multiValueListenerFlow(Produk::class.java)
    }


    fun updateProduk(produk: Produk, onComplete: (isSuccess: Boolean) -> Unit) {
        val produkRef = databaseReference.child(produk.id)

        produkRef.updateChildren(produk.toMap())
            .addOnCompleteListener { task ->
                onComplete(task.isSuccessful)
            }
    }



    fun addProduk(produk: Produk, onComplete: (isSuccess: Boolean) -> Unit) {
        val produkRef = databaseReference.push()
        produkRef.setValue(produk.copy(id = produkRef.key!!))
            .addOnCompleteListener { task ->
                onComplete(task.isSuccessful)
            }
    }

    fun updateProdukStok(produkId: String, newStok: Int, onComplete: (isSuccess: Boolean) -> Unit) {
        databaseReference.child(produkId)
            .child("stok")
            .setValue(newStok)
            .addOnCompleteListener {
                onComplete(it.isSuccessful)
            }
    }

    fun updateProdukTerjual(produkId: String, terjual: Int, onComplete: (isSuccess: Boolean) -> Unit) {
        databaseReference.child(produkId)
            .child("terjual")
            .setValue(terjual)
            .addOnCompleteListener {
                onComplete(it.isSuccessful)
            }
    }

    fun searchProduct(query: String, onComplete: (isSuccess: Boolean, data: List<Produk?>?) -> Unit) {
        databaseReference.orderByChild("keyword")
            .startAt(query)
            .endAt(query + "\uf8ff")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val value = dataSnapshot.children.map { snapshot ->
                        snapshot.getValue(Produk::class.java)
                    }
                    onComplete(true, value)
                }

                override fun onCancelled(error: DatabaseError) {
                    onComplete(false, null)
                }
            })
    }


    fun getProdukByTokoId(tokoID: String, onComplete: (data: List<Produk?>) -> Unit) {
        databaseReference.orderByChild("idToko")
            .equalTo(tokoID)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val value = dataSnapshot.children.map { snapshot ->
                        snapshot.getValue(Produk::class.java)
                    }
                    onComplete(value)
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle the error here if needed
                    onComplete(emptyList())
                }
            })
    }

    fun getProdukByKategori(kategoriID: String, onComplete: (data: List<Produk?>) -> Unit){
        databaseReference.orderByChild("kategori")
            .equalTo(kategoriID)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val value = snapshot.children.map { snapshot ->
                        snapshot.getValue(Produk::class.java)
                    }
                    onComplete(value)
                }

                override fun onCancelled(error: DatabaseError) {
                    onComplete(emptyList())
                }
            })
    }


    fun getProdukDetail(
        namaProduk: String,
        onComplete: (data: Produk) -> Unit,
        onError: (error: Exception) -> Unit
    ) {
        databaseReference.child(namaProduk)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    try {
                        onComplete(snapshot.toProdukDomain())
                    } catch (e: Exception) {
                        onError(e)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    onError(error.toException())
                }
            })
    }

    fun getProdukById(produkId: String, onComplete: (data: Produk?) -> Unit) {
        databaseReference.child(produkId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val produk = snapshot.getValue(Produk::class.java)
                    onComplete(produk)
                }

                override fun onCancelled(error: DatabaseError) {
                    onComplete(null)
                }
            })
    }


    fun removeProdukById(id: String, onComplete: (isSuccess: Boolean) -> Unit) {
        databaseReference.child(id).removeValue { error, _ ->
            onComplete(error == null)
        }
    }
}