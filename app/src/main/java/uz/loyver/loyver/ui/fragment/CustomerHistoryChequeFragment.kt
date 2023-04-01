package uz.loyver.loyver.ui.fragment

import android.app.DatePickerDialog
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
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import uz.loyver.loyver.R
import uz.loyver.loyver.adapter.ChequeHistoryAdapter
import uz.loyver.loyver.databinding.FragmentCustomerHistoryChequeBinding
import uz.loyver.loyver.model.Cart
import uz.loyver.loyver.model.Cheque
import uz.loyver.loyver.ui.activity.MainActivity
import uz.loyver.loyver.utils.UiStateList
import uz.loyver.loyver.utils.UiStateObject
import uz.loyver.loyver.utils.hideKeyboard
import uz.loyver.loyver.utils.toast
import uz.loyver.loyver.viewmodel.add_customer.customer_cheques.CustomerChequeHistoryViewModel
import java.util.*
import kotlin.collections.ArrayList

@AndroidEntryPoint
class CustomerHistoryChequeFragment : Fragment(R.layout.fragment_customer_history_cheque) {

    private lateinit var binding: FragmentCustomerHistoryChequeBinding

    private val args: CustomerHistoryChequeFragmentArgs by navArgs()

    private val viewModel: CustomerChequeHistoryViewModel by viewModels()

    private var year: Int = 0
    private var month: Int = 0
    private var day: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCustomerHistoryChequeBinding.inflate(inflater)

        val c = Calendar.getInstance()
        year = c.get(Calendar.YEAR)
        month = c.get(Calendar.MONTH)
        day = c.get(Calendar.DAY_OF_MONTH)

        initViews()

        return binding.root
    }

    private fun initViews() {
        setToolbar()

        getCheques(args.customerId)

        binding.ivCalendar.setOnClickListener {
            DatePickerDialog(requireContext(), { _, mYear, mMonth, mDayOfMonth ->
                val data = "$mYear-${mMonth + 1}-$mDayOfMonth"
                year = mYear
                month = mMonth
                day = mDayOfMonth
                viewModel.getCheques(args.customerId, data, data)
            }, year, month, day).show()
        }

    }

    private fun getCheques(customerId: Int) {
        viewModel.getCheques(customerId, "", "")

        setUpObserver()
    }

    private fun setUpObserver() {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.cheque.collect {
                when(it) {
                    is UiStateList.LOADING -> {
                        showLoading()
                    }
                    is UiStateList.SUCCESS -> {
                        dismissLoading()
                        setChequesHistory(it.data)
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

    private fun setChequesHistory(data: List<Cart>) {
        val adapter = ChequeHistoryAdapter(data as ArrayList)

        adapter.onClick = {
            hideKeyboard()
            if (it.is_saved) {
              val action = CustomerHistoryChequeFragmentDirections.actionCustomerHistoryChequeFragmentToSavedChequeDetailsFragment(it.id)
              findNavController().navigate(action)
            } else {
                val action = CustomerHistoryChequeFragmentDirections.actionCustomerHistoryChequeFragmentToChequeDetailsFragment(it.id)
                findNavController().navigate(action)
            }
        }

        binding.rvCheques.adapter = adapter
    }

    private fun setToolbar() {
        (requireActivity() as MainActivity).binding.apply {
            toolbar.title = getString(R.string.str_cheque_history)
            toolbar.setBackgroundColor(Color.WHITE)

            toolbar.setTitleTextAppearance(requireContext(), R.style.titleBlack)

            toolbar.menu.clear()

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

            toggle.setHomeAsUpIndicator(R.drawable.ic_back_black)
        }
    }

    private fun showLoading() {
        binding.llLoading.visibility = View.VISIBLE
    }

    private fun dismissLoading() {
        binding.llLoading.visibility = View.GONE
    }

}