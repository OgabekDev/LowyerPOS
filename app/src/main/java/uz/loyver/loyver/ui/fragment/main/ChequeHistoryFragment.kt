package uz.loyver.loyver.ui.fragment.main

import android.app.DatePickerDialog
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import uz.loyver.loyver.adapter.ChequeHistoryAdapter
import uz.loyver.loyver.databinding.FragmentChequeHistoryBinding
import uz.loyver.loyver.model.Cart
import uz.loyver.loyver.model.Cheque
import uz.loyver.loyver.ui.activity.MainActivity
import uz.loyver.loyver.utils.UiStateList
import uz.loyver.loyver.utils.hideKeyboard
import uz.loyver.loyver.utils.toast
import uz.loyver.loyver.viewmodel.main.cheque.ChequeHistoryViewModel
import java.util.*
import kotlin.collections.ArrayList

@AndroidEntryPoint
class ChequeHistoryFragment : Fragment(R.layout.fragment_cheque_history) {

    private lateinit var binding: FragmentChequeHistoryBinding

    private val viewModel: ChequeHistoryViewModel by viewModels()

    private var year: Int = 0
    private var month: Int = 0
    private var day: Int = 0

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = FragmentChequeHistoryBinding.inflate(inflater, container, false)

        initViews()

        val c = Calendar.getInstance()
        year = c.get(Calendar.YEAR)
        month = c.get(Calendar.MONTH)
        day = c.get(Calendar.DAY_OF_MONTH)

        (requireActivity() as MainActivity).binding.navView.menu.getItem(1).isChecked = true

        return binding.root

    }

    private fun initViews() {
        setToolbar()

        getChequeHistory()

        binding.etSearch.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.searchCheques(s.toString())
            }

            override fun afterTextChanged(s: Editable?) {}

        })

        binding.ivCalendar.setOnClickListener {

            DatePickerDialog(requireContext(), { _, mYear, mMonth, mDayOfMonth ->
                val data = "$mYear-${mMonth + 1}-$mDayOfMonth"
                year = mYear
                month = mMonth
                day = mDayOfMonth
                viewModel.getAllCheques(data, data)
            }, year, month, day).show()
        }

    }

    private fun getChequeHistory() {
        viewModel.getAllCheques("", "")

        setUpObserver()

    }

    private fun setUpObserver() {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.cheques.collect {
                when (it) {
                    is UiStateList.LOADING -> {
                        showLoading()
                    }
                    is UiStateList.SUCCESS -> {
                        dismissLoading()
                        setChequeHistory(it.data as ArrayList<Cart>)
                    }
                    is UiStateList.ERROR -> {
                        dismissLoading()
                        toast("ERROR FROM SERVER: ${it.message}")
                    }
                    else -> Unit
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.search.collect {
                when (it) {
                    is UiStateList.LOADING -> {
                        showLoading()
                    }
                    is UiStateList.SUCCESS -> {
                        dismissLoading()
                        setChequeHistory(it.data as ArrayList<Cart>)
                    }
                    is UiStateList.ERROR -> {
                        dismissLoading()
                        toast("ERROR FROM SERVER: ${it.message}")
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun setChequeHistory(data: ArrayList<Cart>) {
        if (data.isEmpty()) {
            binding.llNonList.visibility = View.VISIBLE
            binding.llList.visibility = View.GONE
        } else {
            binding.llNonList.visibility = View.GONE
            binding.llList.visibility = View.VISIBLE
            val adapter = ChequeHistoryAdapter(data)

            adapter.onClick = {
                hideKeyboard()
                if (it.is_saved) {
                    val action = ChequeHistoryFragmentDirections.actionChequeHistoryFragmentToSavedChequeDetailsFragment(it.id)
                    findNavController().navigate(action)
                } else {
                    val action = ChequeHistoryFragmentDirections.actionChequeHistoryFragmentToChequeDetailsFragment(it.id)
                    findNavController().navigate(action)
                }
            }

            binding.rvCheques.adapter = adapter
        }
    }

    private fun setToolbar() {
        (requireActivity() as MainActivity).binding.apply {
            toolbar.title = getString(R.string.str_cheques)
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