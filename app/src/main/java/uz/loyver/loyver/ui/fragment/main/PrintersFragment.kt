package uz.loyver.loyver.ui.fragment.main

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import uz.loyver.loyver.R
import uz.loyver.loyver.adapter.PrinterAdapter
import uz.loyver.loyver.databinding.FragmentPrintersBinding
import uz.loyver.loyver.manager.SharedPref
import uz.loyver.loyver.model.Printer
import uz.loyver.loyver.ui.activity.MainActivity
import uz.loyver.loyver.utils.UiStateList
import uz.loyver.loyver.utils.hideKeyboard
import uz.loyver.loyver.utils.toast
import uz.loyver.loyver.viewmodel.main.printer.PrintersViewModel

@AndroidEntryPoint
class PrintersFragment : Fragment(R.layout.fragment_printers) {

    private lateinit var binding: FragmentPrintersBinding

    private val viewModel: PrintersViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = FragmentPrintersBinding.inflate(inflater, container, false)

        initViews()

        (requireActivity() as MainActivity).binding.navView.menu.getItem(4).isChecked = true

        return binding.root

    }

    private fun initViews() {
        setToolbar()

        viewModel.getAllPrinters()

        setUpObservers()

        binding.btnAddPrinter.setOnClickListener {
            hideKeyboard()
            val action = PrintersFragmentDirections.actionPrintersFragmentToCreatePrinterFragment(null)
            findNavController().navigate(action)
        }

    }

    private fun setUpObservers() {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.printers.collect {
                when (it) {
                    is UiStateList.LOADING -> {
                        showLoading()
                    }
                    is UiStateList.SUCCESS -> {
                        dismissLoading()
                        setData(it.data)
                    }
                    is UiStateList.ERROR -> {
                        dismissLoading()
                        toast("ERROR FROM DATABASE: ${it.message}")
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun setData(data: List<Printer>) {
        if (data.isEmpty()) {
            binding.llList.visibility = View.GONE
            binding.llNonList.visibility = View.VISIBLE
        } else {
            binding.llList.visibility = View.VISIBLE
            binding.llNonList.visibility = View.GONE

            val adapter = PrinterAdapter(data as ArrayList)

            adapter.onClick = {
                hideKeyboard()
                val action = PrintersFragmentDirections.actionPrintersFragmentToCreatePrinterFragment(it.name)
                findNavController().navigate(action)
            }

            adapter.onClickMain = {
                hideKeyboard()
                viewModel.updatePrinter(it)
            }

            binding.rvPrinters.adapter = adapter

        }
    }

    private fun setToolbar() {
        (requireActivity() as MainActivity).binding.apply {
            toolbar.title = getString(R.string.str_all_printers)
            toolbar.menu.clear()
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

        }
    }

    private fun showLoading() {
        binding.llLoading.visibility = View.VISIBLE
    }

    private fun dismissLoading() {
        binding.llLoading.visibility = View.GONE
    }

}