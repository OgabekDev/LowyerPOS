package uz.loyver.loyver.ui.fragment

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import uz.loyver.loyver.R
import uz.loyver.loyver.databinding.FragmentCreatePrinterBinding
import uz.loyver.loyver.manager.SharedPref
import uz.loyver.loyver.model.Printer
import uz.loyver.loyver.ui.activity.MainActivity
import uz.loyver.loyver.utils.*
import uz.loyver.loyver.viewmodel.main.printer.PrintersCreateViewModel

@AndroidEntryPoint
class CreatePrinterFragment : Fragment(R.layout.fragment_create_printer) {

    lateinit var binding: FragmentCreatePrinterBinding

    private val args: CreatePrinterFragmentArgs by navArgs()

    private val viewModel: PrintersCreateViewModel by viewModels()

    private var printer: Printer? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentCreatePrinterBinding.inflate(inflater, container, false)

        initViews()

        setToolbar()

        if (args.name != null) {
            binding.etPrinterName.isEnabled = false
            setData(args.name!!)
        } else {
            binding.llDeletePrinter.visibility = View.GONE
        }

        setObservers()

        return binding.root

    }

    private fun setObservers() {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.create.collect {
                when(it) {
                    is UiStateObject.LOADING -> {
                        showLoading()
                    }
                    is UiStateObject.SUCCESS -> {
                        dismissLoading()
                        toast(getString(R.string.str_all_done))
                        Navigation.findNavController(requireView()).navigateUp()
                    }
                    is UiStateObject.ERROR -> {
                        dismissLoading()
                        if (it.message.startsWith("UNIQUE constraint failed")) {
                            binding.etPrinterName.error = getString(R.string.str_already_have)
                        } else toast("ERROR FROM DATABASE ${it.message}")
                    }
                    else -> Unit
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.update.collect {
                when(it) {
                    is UiStateObject.LOADING -> {
                        showLoading()
                    }
                    is UiStateObject.SUCCESS -> {
                        dismissLoading()
                        toast(getString(R.string.str_all_done))
                        Navigation.findNavController(requireView()).navigateUp()
                    }
                    is UiStateObject.ERROR -> {
                        dismissLoading()
                        if (it.message.startsWith("UNIQUE constraint failed")) {
                            binding.etPrinterName.error = getString(R.string.str_already_have)
                        } else toast("ERROR FROM DATABASE ${it.message}")
                    }
                    else -> Unit
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.printer.collect {
                when(it) {
                    is UiStateObject.LOADING -> {
                        showLoading()
                    }
                    is UiStateObject.SUCCESS -> {
                        dismissLoading()
                        printer = it.data
                        setInfo(it.data)
                    }
                    is UiStateObject.ERROR -> {
                        dismissLoading()
                        toast("ERROR FROM DATABASE ${it.message}")
                    }
                    else -> Unit
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.delete.collect {
                when(it) {
                    is UiStateObject.LOADING -> {
                        showLoading()
                    }
                    is UiStateObject.SUCCESS -> {
                        dismissLoading()
                        SharedPref(requireContext()).saveInt("defaultPrinter", 0)
                        toast(getString(R.string.str_all_done))
                        Navigation.findNavController(requireView()).navigateUp()
                    }
                    is UiStateObject.ERROR -> {
                        dismissLoading()
                        toast("ERROR FROM DATABASE ${it.message}")
                    }
                    else -> Unit
                }
            }
        }

    }

    private fun setInfo(data: Printer) {
        binding.apply {
            etPrinterName.setText(data.name)
            if (data.isBluetooth) {
                sType.setSelection(1)
            } else {
                sType.setSelection(0)
            }
            etPrinterAddress.setText(data.address)
        }
    }

    private fun setData(name: String) {

        binding.llDeletePrinter.visibility = View.VISIBLE

        viewModel.getPrinter(name)

    }

    private fun initViews() {

        hideKeyboard()

        setSpinner()

        binding.apply {
            llDeletePrinter.setOnClickListener {
                hideKeyboard()
                viewModel.deletePrinter(args.name!!)
            }

            llTestPrint.setOnClickListener {
                hideKeyboard()
                testPrint(binding.sType.selectedItemPosition == 1, binding.etPrinterAddress.text.toString())
            }

        }

    }

    private fun testPrint(isBluetooth: Boolean, ipAddress: String) {

        if (ipAddress.isEmpty()) toast(getString(R.string.str_fill_fields)) else (requireActivity() as MainActivity).printTest(isBluetooth, ipAddress)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    if (printer == null) {
                        showWarningDialog(
                            getString(R.string.str_unsaved_changes),
                            getString(R.string.str_unsaved_changes_msg),
                            {
                                // When ПРОДАЛЖИТЬ clicked
                                Navigation.findNavController(view).navigateUp()
                            },
                            {
                                // When ОТМЕНА clicked
                            })
                    } else if (printer!!.name == binding.etPrinterName.text.toString() && printer!!.isBluetooth == (binding.sType.selectedItemPosition == 1) && printer!!.address == binding.etPrinterAddress.text.toString()) {
                        Navigation.findNavController(requireView()).navigateUp()
                    } else {
                        showWarningDialog(
                            getString(R.string.str_unsaved_changes),
                            getString(R.string.str_unsaved_changes_msg),
                            {
                                // When ПРОДАЛЖИТЬ clicked
                                Navigation.findNavController(view).navigateUp()
                            },
                            {
                                // When ОТМЕНА clicked
                            })
                    }
                }
            })

    }

    private fun showWarningDialog(
        title: String,
        msg: String,
        ok: (() -> Unit),
        cancel: (() -> Unit)
    ) {
        val dialog = AlertDialog.Builder(requireContext())
        dialog.setTitle(title)
        dialog.setMessage(msg)
        dialog.setCancelable(true)
        dialog.setPositiveButton(getString(R.string.str_continue)) { _, _ ->
            ok.invoke()
        }
        dialog.setNegativeButton(getString(R.string.str_cancel)) { _, _ ->
            cancel.invoke()
        }
        dialog.create()
        dialog.show()
    }

    private fun setSpinner() {

        val categories = ArrayList<String>()

        categories.add("Ethernet")
        categories.add("Bluetooth")

        binding.apply {

            val adapter: ArrayAdapter<String> =
                ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            sType.adapter = adapter

            sType.onItemSelectedListener = object : OnItemSelectedListener {
                override fun onItemSelected(
                    p0: AdapterView<*>?,
                    p1: View?,
                    position: Int,
                    p3: Long
                ) {
                    when (position) {
                        0 -> {
                            sType.post {
                                binding.etPrinterAddress.hint = getString(R.string.str_ip_address_printer)
                            }
                        }
                        1 -> {
                            sType.post {
                                binding.etPrinterAddress.hint = getString(R.string.str_printer_name)
                            }
                        }
                    }
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {}

            }

        }

    }

    private fun setToolbar() {
        (requireActivity() as MainActivity).binding.apply {
            toolbar.title = if (args.name == null) getString(R.string.str_create_printer) else getString(R.string.str_edit___)
            toolbar.menu.clear()
            toolbar.inflateMenu(R.menu.toolbar_create_product)
            toolbar.setBackgroundColor(Color.parseColor("#0071DC"))

            toolbar.setTitleTextAppearance(requireContext(), R.style.titleWhite)

            val toggle = ActionBarDrawerToggle(
                requireActivity(),
                drawerLayout,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
            )
            drawerLayout.addDrawerListener(toggle);
            toggle.syncState()

            llOnHome.visibility = View.GONE

            toggle.isDrawerIndicatorEnabled = false

            toggle.setToolbarNavigationClickListener {
                requireActivity().onBackPressed()
            }

            toggle.setHomeAsUpIndicator(R.drawable.ic_back)

            toolbar.setOnMenuItemClickListener {
                when (it?.itemId) {
                    R.id.navSave -> {
                        save()
                        return@setOnMenuItemClickListener true
                    }
                }
                return@setOnMenuItemClickListener false
            }
        }
    }

    private fun save() {

        binding.apply {
            val name = etPrinterName.text.toString()
            val printerAddress = etPrinterAddress.text.toString()

            val isBluetooth = sType.selectedItemId.toInt() == 1

            if (name.isEmpty()) {
                etPrinterName.error = getString(R.string.str_fill_fields)
            } else if (printerAddress.isEmpty()) {
                etPrinterAddress.error = getString(R.string.str_fill_fields)
            } else {
                val printer = Printer(name, isBluetooth, false, printerAddress)
                if (args.name == null) viewModel.createPrinter(printer) else viewModel.updatePrinter(printer)
            }
        }

    }

    private fun showLoading() {
        binding.llLoading.visibility = View.VISIBLE
    }

    private fun dismissLoading() {
        binding.llLoading.visibility = View.GONE
    }

}