package uz.loyver.loyver.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uz.loyver.loyver.databinding.ItemCustomerBinding
import uz.loyver.loyver.databinding.ItemProductBinding
import uz.loyver.loyver.model.Customer
import uz.loyver.loyver.model.Product
import uz.loyver.loyver.utils.setAsPrice

class ProductsAdapter(private val products: ArrayList<Product>): RecyclerView.Adapter<ProductsAdapter.ViewHolder>() {

    var onClick: ((Product) -> Unit)? = null

    inner class ViewHolder(private val binding: ItemProductBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(product: Product) {
            binding.apply {
                tvName.text = product.name
                tvPrice.text = product.price.toInt().toString().setAsPrice()
                llItem.setOnClickListener {
                    onClick?.invoke(product)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount() = products.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(products[position])
    }

}