package uz.loyver.loyver.ui.fragment

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import uz.loyver.loyver.R
import uz.loyver.loyver.databinding.FragmentCustomerDetailsBinding
import uz.loyver.loyver.manager.SharedPref
import uz.loyver.loyver.model.Customer
import uz.loyver.loyver.ui.activity.MainActivity
import uz.loyver.loyver.utils.UiStateObject
import uz.loyver.loyver.utils.hideKeyboard
import uz.loyver.loyver.utils.toast
import uz.loyver.loyver.viewmodel.add_customer.details.CustomerDetailsViewModel

@AndroidEntryPoint
class CustomerDetailsFragment : Fragment(R.layout.fragment_customer_details) {

    private lateinit var binding: FragmentCustomerDetailsBinding

    private val args: CustomerDetailsFragmentArgs by navArgs()

    private val viewModel: CustomerDetailsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCustomerDetailsBinding.inflate(inflater, container, false)

        initViews()

        return binding.root
    }

    private fun initViews() {
        setToolbar()

        hideKeyboard()

        binding.apply {
            tvEditProfile.setOnClickListener {
                hideKeyboard()
                val action = CustomerDetailsFragmentDirections.actionCustomerDetailsFragmentToUpdateCustomerFragment(args.id)
                findNavController().navigate(action)
            }
            tvCustomerCheques.setOnClickListener {
                hideKeyboard()
                val action = CustomerDetailsFragmentDirections.actionCustomerDetailsFragmentToCustomerHistoryChequeFragment(args.id)
                findNavController().navigate(action)
            }
        }

        viewModel.getCustomers(args.id)

        setUpObservers()

    }

    private fun setUpObservers() {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.customer.collect {
                when(it) {
                    is UiStateObject.LOADING -> {
                        showLoading()
                    }
                    is UiStateObject.SUCCESS -> {
                        dismissLoading()
                        setData(it.data)
                    }
                    is UiStateObject.ERROR -> {
                        dismissLoading()
                        toast("ERROR FROM SERVER: ${it.message}")
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun setData(data: Customer) {
        binding.apply {
            tvName.text = data.name
            tvCount.text = data.cart_count.toString()
            tvLastTime.text = data.cart_created_at
        }
    }

    private fun setToolbar() {
        (requireActivity() as MainActivity).binding.apply {
            toolbar.title = getString(R.string.str_customer_profile)
            toolbar.menu.clear()
            toolbar.setBackgroundColor(Color.WHITE)

            toolbar.setTitleTextAppearance(requireContext(), R.style.titleBlack)

            toolbar.inflateMenu(R.menu.toolbar_customer_details)

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

            requireActivity().findViewById<TextView>(R.id.navAddCheque).setTextColor(Color.parseColor("#4190DA"))

            toggle.setHomeAsUpIndicator(R.drawable.ic_back_black)

            toolbar.setOnMenuItemClickListener {

                when(it.itemId) {
                    R.id.navAddCheque -> {
                        addCheckToCustomer()
                    }
                }

                return@setOnMenuItemClickListener false
            }

        }
    }

    private fun addCheckToCustomer() {
        SharedPref(requireContext()).saveInt("customer", args.id)
        toast(getString(R.string.str_all_done))
        requireActivity().onBackPressed()
    }

    private fun showLoading() {
        binding.llLoading.visibility = View.VISIBLE
    }

    private fun dismissLoading() {
        binding.llLoading.visibility = View.GONE
    }

}