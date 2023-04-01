package uz.loyver.loyver.ui.fragment

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
import uz.loyver.loyver.adapter.CustomerAdapter
import uz.loyver.loyver.databinding.FragmentAddPersonBinding
import uz.loyver.loyver.model.Customer
import uz.loyver.loyver.ui.activity.MainActivity
import uz.loyver.loyver.utils.UiStateList
import uz.loyver.loyver.utils.hideKeyboard
import uz.loyver.loyver.utils.toast
import uz.loyver.loyver.viewmodel.add_customer.AddCustomerViewModel

@AndroidEntryPoint
class AddPersonFragment : Fragment(R.layout.fragment_add_person) {

    private lateinit var binding: FragmentAddPersonBinding

    private val viewModel: AddCustomerViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = FragmentAddPersonBinding.inflate(inflater, container, false)

        initViews()

        return binding.root

    }

    private fun initViews() {
        setToolbar()

        binding.apply {
            llCreateCustomer.setOnClickListener {
                hideKeyboard()
                val action = AddPersonFragmentDirections.actionAddPersonFragmentToCreateCustomerFragment()
                findNavController().navigate(action)
            }

            etSearch.addTextChangedListener(object: TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    viewModel.searchCustomers(s.toString())
                }

                override fun afterTextChanged(s: Editable?) {}

            })

        }

        getCustomers()

    }

    private fun getCustomers() {
        viewModel.getCustomers()

        setUpObserver()
    }

    private fun setUpObserver() {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.customer.collect {
                when (it) {
                    is UiStateList.LOADING -> {
                        showLoading()
                    }
                    is UiStateList.SUCCESS -> {
                        dismissLoading()
                        setCustomers(it.data as ArrayList)
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
                        setCustomers(it.data as ArrayList)
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

    private fun setCustomers(data: ArrayList<Customer>) {
        if (data.isEmpty()) {
            binding.llNonList.visibility = View.VISIBLE
            binding.llList.visibility = View.GONE
        } else {
            binding.llNonList.visibility = View.GONE
            binding.llList.visibility = View.VISIBLE
            val adapter = CustomerAdapter(data)
            adapter.onClick = {
                hideKeyboard()
                val action = AddPersonFragmentDirections.actionAddPersonFragmentToCustomerDetailsFragment(it.id)
                findNavController().navigate(action)
            }

            binding.rvCustomers.adapter = adapter
        }

    }

    private fun setToolbar() {
        (requireActivity() as MainActivity).binding.apply {
            toolbar.title = getString(R.string.str_add_person_to_cheque)
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

            toolbar.setBackgroundColor(Color.WHITE)

            toolbar.setTitleTextAppearance(requireContext(), R.style.titleBlack)

            llOnHome.visibility = View.GONE

            toggle.isDrawerIndicatorEnabled = false

            toggle.setToolbarNavigationClickListener {
                requireActivity().onBackPressed()
            }

            toggle.setHomeAsUpIndicator(R.drawable.ic_close)
        }
    }

    private fun showLoading() {
        binding.llLoading.visibility = View.VISIBLE
    }

    private fun dismissLoading() {
        binding.llLoading.visibility = View.GONE
    }

}