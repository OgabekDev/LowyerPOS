package uz.loyver.loyver.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uz.loyver.loyver.databinding.ItemAddToCategoryBinding
import uz.loyver.loyver.databinding.ItemCustomerBinding
import uz.loyver.loyver.databinding.ItemProductBinding
import uz.loyver.loyver.model.CategoryProduct
import uz.loyver.loyver.model.Customer
import uz.loyver.loyver.model.Product

class AddProductsToCategoryAdapter(private val products: ArrayList<CategoryProduct>): RecyclerView.Adapter<AddProductsToCategoryAdapter.ViewHolder>() {

    var onClick: ((Int, Boolean) -> Unit)? = null

    inner class ViewHolder(private val binding: ItemAddToCategoryBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(product: CategoryProduct) {
            binding.apply {
                tvName.text = product.name
                tvCategory.text = product.category
                cbItem.isChecked = product.isChecked
                cbItem.setOnCheckedChangeListener { _, isChecked ->
                    onClick?.invoke(product.id, isChecked)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemAddToCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount() = products.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(products[position])
    }

}