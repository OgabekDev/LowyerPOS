package uz.loyver.loyver.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uz.loyver.loyver.databinding.ItemActiveChequeBinding
import uz.loyver.loyver.model.ChequeProduct
import uz.loyver.loyver.utils.Constants
import uz.loyver.loyver.utils.setAsPrice

class ActiveChequesAdapter(private val products: ArrayList<ChequeProduct>): RecyclerView.Adapter<ActiveChequesAdapter.ViewHolder>() {

    var onClick: ((ChequeProduct) -> Unit)? = null

    inner class ViewHolder(private val binding: ItemActiveChequeBinding): RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(product: ChequeProduct) {
            binding.apply {
                tvProductAndQuantity.text = product.name + " x " + (if (product.type == Constants.EACH) product.quantity.toInt() else product.quantity)
                tvPrice.text = (product.price * product.quantity).toInt().toString().setAsPrice()
                root.setOnClickListener {
                    onClick?.invoke(product)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemActiveChequeBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount() = products.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(products[position])
    }

}