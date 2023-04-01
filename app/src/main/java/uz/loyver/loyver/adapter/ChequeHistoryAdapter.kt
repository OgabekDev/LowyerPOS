package uz.loyver.loyver.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uz.loyver.loyver.databinding.ItemChequeHistoryBinding
import uz.loyver.loyver.model.Cart
import uz.loyver.loyver.utils.setAsPrice

class ChequeHistoryAdapter(private val cheques: ArrayList<Cart>): RecyclerView.Adapter<ChequeHistoryAdapter.ViewHolder>() {

    var onClick: ((Cart) -> Unit)? = null

    inner class ViewHolder(private val binding: ItemChequeHistoryBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(oldCheque: Cart, cheque: Cart) {
            binding.apply {
                if (oldCheque.create_date == cheque.create_date) {
                    llDate.visibility = View.GONE
                } else {
                    llDate.visibility = View.VISIBLE
                    tvDate.text = cheque.create_date
                }

                tvPrice.text = cheque.total_summa.toInt().toString().setAsPrice()
                tvTime.text = cheque.time.substring(1, 5)
                tvNumber.text = cheque.cart_number
                if (cheque.is_saved) {
                    tvSaved.visibility = View.VISIBLE
                } else {
                    tvSaved.visibility = View.GONE
                }

                llItem.setOnClickListener {
                    onClick?.invoke(cheque)
                }

            }
        }

        fun bind(cheque: Cart) {
            binding.apply {
                llDate.visibility = View.VISIBLE
                tvDate.text = cheque.create_date

                tvPrice.text = cheque.total_summa.toInt().toString().setAsPrice()
                tvTime.text = cheque.time.substring(1, 5)
                tvNumber.text = cheque.cart_number
                if (cheque.is_saved) {
                    tvSaved.visibility = View.VISIBLE
                } else {
                    tvSaved.visibility = View.GONE
                }

                llItem.setOnClickListener {
                    onClick?.invoke(cheque)
                }

            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemChequeHistoryBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount() = cheques.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (position == 0) {
            holder.bind(cheques[position])
        } else {
            holder.bind(cheques[position - 1], cheques[position])
        }
    }

}