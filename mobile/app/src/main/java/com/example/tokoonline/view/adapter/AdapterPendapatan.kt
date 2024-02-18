package com.example.tokoonline.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.tokoonline.R
import com.example.tokoonline.core.util.OnItemClick
import com.example.tokoonline.data.model.firebase.Transaction
import com.example.tokoonline.data.repository.firebase.ProdukTransactionRepository
import com.example.tokoonline.databinding.ItemTableBinding
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone

class AdapterPendapatan(
    private val onClickListener: OnItemClick
) : RecyclerView.Adapter<AdapterPendapatan.ViewHolder>() {
    private val produkTransactionRepository = ProdukTransactionRepository.getInstance()

    private val list: MutableList<Transaction> = mutableListOf()

    inner class ViewHolder(val binding: ItemTableBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(transaction: Transaction, position: Int) {
            if (position % 2 == 0) {
                binding.root.setBackgroundColor(
                    ContextCompat.getColor(
                        binding.root.context,
                        R.color.white2
                    )
                )
            } else {
                binding.root.setBackgroundColor(
                    ContextCompat.getColor(
                        binding.root.context,
                        R.color.white
                    )
                )
            }

            //TODO NEED LOOP
            produkTransactionRepository.getProdukById(transaction.produkId) {
                binding.nama.text =
                    it.joinToString(separator = ",") { produk -> produk?.nama.toString() }
            }
            binding.nama.text = transaction.produkId
            binding.status.text = transaction.status
            binding.tanggal.text = formatString(transaction.createdAt)

            binding.btnAksi.setOnClickListener {
                onClickListener.onClick(transaction, position)
            }
        }

        private fun formatString(createdAt: String): String {
            val df = SimpleDateFormat("yyyy-MM-dd HH:mm:ss Z", Locale.getDefault())
            df.timeZone = TimeZone.getTimeZone("Asia/Jakarta")
            val date = df.parse(createdAt)

            val outputDateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())
            return outputDateFormat.format(date)
        }
    }

    fun submitList(newList: List<Transaction>) {
        list.addAll(newList)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val binding = ItemTableBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(list[position], position)
    }
}