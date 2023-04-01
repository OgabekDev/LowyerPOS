package uz.loyver.loyver.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import uz.loyver.loyver.databinding.ItemCustomerBinding
import uz.loyver.loyver.databinding.ItemPrinterBinding
import uz.loyver.loyver.model.Printer

class PrinterAdapter(private val printers: ArrayList<Printer>): RecyclerView.Adapter<PrinterAdapter.ViewHolder>() {

    var onClick: ((Printer) -> Unit)? = null

    var onClickMain: ((Printer) -> Unit)? = null

    var main = 0

    inner class ViewHolder(private val binding: ItemPrinterBinding): RecyclerView.ViewHolder(binding.root) {

        fun bind(printer: Printer) {
            binding.apply {
                rbPrinter.isChecked = printer.isMain
                if (printer.isMain) {
                    main = adapterPosition
                }
                rbPrinter.setOnClickListener {
                    printers[main].isMain = false
                    onClickMain?.invoke(printers[main])
                    notifyItemChanged(main)

                    main = adapterPosition

                    printers[main].isMain = true
                    notifyItemChanged(main)
                    onClickMain?.invoke(printers[main])

                }
                tvName.text = printer.name
                llPrinter.setOnClickListener {
                    onClick?.invoke(printer)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemPrinterBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun getItemCount() = printers.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(printers[position])
    }

}