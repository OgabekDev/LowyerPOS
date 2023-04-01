package uz.loyver.loyver.ui.fragment

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import uz.loyver.loyver.R
import uz.loyver.loyver.adapter.ChequeDetailsAdapter
import uz.loyver.loyver.databinding.FragmentChequeDetailsBinding
import uz.loyver.loyver.model.Cheque
import uz.loyver.loyver.ui.activity.MainActivity
import uz.loyver.loyver.utils.UiStateObject
import uz.loyver.loyver.utils.hideKeyboard
import uz.loyver.loyver.utils.setAsPrice
import uz.loyver.loyver.utils.toast
import uz.loyver.loyver.viewmodel.cheque.details.ChequeDetailsViewModel

@AndroidEntryPoint
class ChequeDetailsFragment : Fragment(R.layout.fragment_cheque_details) {

    private lateinit var binding: FragmentChequeDetailsBinding

    private val args: ChequeDetailsFragmentArgs by navArgs()

    private val viewModel: ChequeDetailsViewModel by viewModels()

    private lateinit var cheque: Cheque

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = FragmentChequeDetailsBinding.inflate(inflater, container, false)

        initViews()

        return binding.root

    }

    private fun initViews() {
        setToolbar()
        hideKeyboard()
        viewModel.getCheque(args.id)

        setUpObserver()

    }

    private fun setUpObserver() {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.cheque.collect {
                when (it) {
                    is UiStateObject.LOADING -> {
                        showLoading()
                    }
                    is UiStateObject.SUCCESS -> {
                        dismissLoading()
                        cheque = it.data
                        (requireActivity() as MainActivity).binding.toolbar.title = cheque.cart_number
                        setData(cheque)
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

    @SuppressLint("SetTextI18n")
    private fun setData(cheque: Cheque) {
        binding.apply {
            tvPrice.text = cheque.total_summa.toInt().toString().setAsPrice()
            tvChequeNumber.text = cheque.cart_number
            tvTime.text = cheque.create_date + " " + cheque.time.substring(0, 5)
            tvTotalSecond.text = cheque.total_summa.toInt().toString().setAsPrice()

            tvCustomer.text = "${cheque.user.name}${if (cheque.user.phone_number.isNullOrEmpty()) "" else " (${cheque.user.phone_number})"}"
            tvCustomerNote.text = cheque.user.comments

            rvChequeProducts.adapter = ChequeDetailsAdapter(cheque.items)

        }
    }

    private fun setToolbar() {
        (requireActivity() as MainActivity).binding.apply {
            toolbar.menu.clear()
            toolbar.inflateMenu(R.menu.toolbar_cheque_details)
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
                when(it.itemId) {
                    R.id.navPrintCheque -> {
                        hideKeyboard()
                        printCheque()
                    }
                }
                return@setOnMenuItemClickListener false
            }

        }
    }

    private fun printCheque() {
        (requireActivity() as MainActivity).printCheque(cheque)
    }

    private fun showLoading() {
        binding.llLoading.visibility = View.VISIBLE
    }

    private fun dismissLoading() {
        binding.llLoading.visibility = View.GONE
    }

}