package uz.loyver.loyver.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uz.loyver.loyver.databinding.ItemCategoryBinding
import uz.loyver.loyver.model.Category

class CategoryAdapter(private val categories: ArrayList<Category>): RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {

    var onClick: ((Category) -> Unit)? = null

    inner class ViewHolder(private val binding: ItemCategoryBinding): RecyclerView.ViewHolder(binding.root) {

        @SuppressLint("SetTextI18n")
        fun bind(category: Category) {
            binding.apply {
                tvName.text = category.title
                tvQuantity.text = category.quantity.toString() + " товаров"
                llItem.setOnClickListener {
                    onClick?.invoke(category)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemCategoryBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount() = categories.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(categories[position])
    }

}