package uz.loyver.loyver.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uz.loyver.loyver.databinding.ItemHomeProductBinding
import uz.loyver.loyver.model.Product
import uz.loyver.loyver.utils.setAsPrice

class HomeAdapter(private val products: ArrayList<Product>) : RecyclerView.Adapter<HomeAdapter.ViewHolder>() {

    var onClick: ((Product) -> Unit)? = null

    inner class ViewHolder(private val binding: ItemHomeProductBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(product: Product?) {
            binding.apply {
                product?.let {
                    tvName.text = product.name
                    tvPrice.text = product.price.toString().setAsPrice()
                    llItem.setOnClickListener {
                        onClick?.invoke(product)
                    }
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            ItemHomeProductBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(products[position])
    }

    override fun getItemCount() = products.size

}