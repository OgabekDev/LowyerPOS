package uz.loyver.loyver.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uz.loyver.loyver.databinding.ItemChequeProductsBinding
import uz.loyver.loyver.model.ChequeProduct
import uz.loyver.loyver.utils.setAsPrice

class ChequeDetailsAdapter(private val products: ArrayList<ChequeProduct>): RecyclerView.Adapter<ChequeDetailsAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemChequeProductsBinding): RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(product: ChequeProduct) {
            binding.apply {
                tvName.text = product.name
                tvQuantity.text = "${product.quantity} x ${product.price}"
                tvTotal.text = (product.quantity * product.price).toInt().toString().setAsPrice()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemChequeProductsBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount() = products.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(products[position])
    }

}