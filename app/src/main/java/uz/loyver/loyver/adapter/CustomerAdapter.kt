package uz.loyver.loyver.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uz.loyver.loyver.databinding.ItemCustomerBinding
import uz.loyver.loyver.model.Customer

class CustomerAdapter(private val customers: ArrayList<Customer>): RecyclerView.Adapter<CustomerAdapter.ViewHolder>() {

    var onClick: ((Customer) -> Unit)? = null

    inner class ViewHolder(private val binding: ItemCustomerBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(customer: Customer) {
            binding.apply {
                tvName.text = customer.name
                llPerson.setOnClickListener {
                    onClick?.invoke(customer)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemCustomerBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount() = customers.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(customers[position])
    }

}