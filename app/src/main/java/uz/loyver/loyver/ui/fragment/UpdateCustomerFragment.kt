package uz.loyver.loyver.ui.fragment

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import uz.loyver.loyver.R
import uz.loyver.loyver.databinding.FragmentCreateCustomerBinding
import uz.loyver.loyver.model.Customer
import uz.loyver.loyver.ui.activity.MainActivity
import uz.loyver.loyver.utils.Constants
import uz.loyver.loyver.utils.UiStateObject
import uz.loyver.loyver.utils.hideKeyboard
import uz.loyver.loyver.utils.toast
import uz.loyver.loyver.viewmodel.add_customer.update_customer.UpdateCustomerViewModel

@AndroidEntryPoint
class UpdateCustomerFragment : Fragment(R.layout.fragment_update_customer) {

    private lateinit var binding: FragmentCreateCustomerBinding

    private val viewModel: UpdateCustomerViewModel by viewModels()

    private val args: UpdateCustomerFragmentArgs by navArgs()

    private lateinit var customer: Customer

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = FragmentCreateCustomerBinding.inflate(layoutInflater)

        initViews()

        return binding.root
    }

    private fun initViews() {
        hideKeyboard()
        setToolbar()

        viewModel.getCustomer(args.id)

        setUpObservers()

    }

    private fun setUpObservers() {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.customer.collect {
                when (it) {
                    is UiStateObject.LOADING -> {
                        showLoading()
                    }
                    is UiStateObject.SUCCESS -> {
                        dismissLoading()
                        customer = it.data
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

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.update.collect {
                when (it) {
                    is UiStateObject.LOADING -> {
                        showLoading()
                    }
                    is UiStateObject.SUCCESS -> {
                        dismissLoading()
                        Navigation.findNavController(requireView()).navigateUp()
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
            etName.setText(data.name)
            etPhone.setText(data.phone_number)
            etNote.setText(data.comments)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (customer.name != binding.etName.text.toString() || customer.phone_number != binding.etPhone.text.toString() || customer.comments != binding.etNote.text.toString()) {
                    showWarningDialog(
                        getString(R.string.str_unsaved_changes),
                        getString(R.string.str_unsaved_changes_msg),
                        getString(R.string.str_continue),
                        {
                            Navigation.findNavController(view).navigateUp()
                        },
                        getString(R.string.str_cancel),
                        {})
                } else {
                    Navigation.findNavController(view).navigateUp()
                }
            }
        })

    }

    private fun showWarningDialog(
        title: String,
        msg: String,
        ok_text: String,
        ok: (() -> Unit),
        cancel_text: String,
        cancel: (() -> Unit)
    ) {
        val dialog = AlertDialog.Builder(requireContext())
        dialog.setTitle(title)
        dialog.setMessage(msg)
        dialog.setCancelable(true)
        dialog.setPositiveButton(ok_text) { _, _ ->
            ok.invoke()
        }
        dialog.setNegativeButton(cancel_text) { _, _ ->
            cancel.invoke()
        }
        dialog.create()
        dialog.show()
    }

    private fun setToolbar() {
        (requireActivity() as MainActivity).binding.apply {
            toolbar.title = getString(R.string.str_edit___)
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
                viewModel.updateCustomer(args.id, customer)
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