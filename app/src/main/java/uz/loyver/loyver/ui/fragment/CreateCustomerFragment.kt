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
import dagger.hilt.android.AndroidEntryPoint
import uz.loyver.loyver.R
import uz.loyver.loyver.databinding.FragmentCreateCustomerBinding
import uz.loyver.loyver.model.Customer
import uz.loyver.loyver.ui.activity.MainActivity
import uz.loyver.loyver.utils.UiStateObject
import uz.loyver.loyver.utils.hideKeyboard
import uz.loyver.loyver.utils.toast
import uz.loyver.loyver.viewmodel.add_customer.create_customer.CreateCustomerViewModel

@AndroidEntryPoint
class CreateCustomerFragment : Fragment(R.layout.fragment_create_customer) {

    private lateinit var binding: FragmentCreateCustomerBinding

    private val viewModel: CreateCustomerViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        binding = FragmentCreateCustomerBinding.inflate(layoutInflater)

        initViews()

        return binding.root
    }

    private fun initViews() {
        hideKeyboard()
        setToolbar()

        setUpObserver()

    }

    private fun setUpObserver() {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.create.collect {
                when(it) {
                    is UiStateObject.LOADING -> {
                        showLoading()
                    }
                    is UiStateObject.SUCCESS -> {
                        dismissLoading()
                        requireActivity().onBackPressed()
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

    private fun setToolbar() {
        (requireActivity() as MainActivity).binding.apply {
            toolbar.title = getString(R.string.str_save_customer)
            toolbar.menu.clear()
            toolbar.setBackgroundColor(Color.WHITE)

            toolbar.setTitleTextAppearance(requireContext(), R.style.titleBlack)

            toolbar.inflateMenu(R.menu.toolbar_create_customer)

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

            requireActivity().findViewById<TextView>(R.id.navSave).setTextColor(Color.parseColor("#4190DA"))

            toolbar.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.navSave -> {
                        saveCustomer()
                    }
                }
                return@setOnMenuItemClickListener false
            }

        }
    }

    private fun saveCustomer() {
        binding.apply {
            if (etName.text.isEmpty() && etName.text.length < 3) {
                etName.error = getString(R.string.str_fill_fields)
            } else {
                val name = etName.text.toString()
                val phoneNumber = etPhone.text.toString()
                val comments = etNote.text.toString()
                val customer = Customer(0, name, phoneNumber, comments, 0, null)
                viewModel.createCustomer(customer)
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