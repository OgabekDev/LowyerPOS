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
import uz.loyver.loyver.adapter.ActiveChequesAdapter
import uz.loyver.loyver.databinding.FragmentSavedChequeDetailsBinding
import uz.loyver.loyver.model.Cheque
import uz.loyver.loyver.model.ChequeProduct
import uz.loyver.loyver.ui.activity.MainActivity
import uz.loyver.loyver.utils.UiStateObject
import uz.loyver.loyver.utils.hideKeyboard
import uz.loyver.loyver.utils.setAsPrice
import uz.loyver.loyver.utils.toast
import uz.loyver.loyver.viewmodel.cheque.saved.SavedChequeDetailsViewModel

@AndroidEntryPoint
class SavedChequeDetailsFragment : Fragment(R.layout.fragment_saved_cheque_details) {

    private lateinit var binding: FragmentSavedChequeDetailsBinding

    private val viewModel: SavedChequeDetailsViewModel by viewModels()

    private val args: SavedChequeDetailsFragmentArgs by navArgs()

    private lateinit var cheque: Cheque

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = FragmentSavedChequeDetailsBinding.inflate(inflater, container, false)

        initViews()

        return binding.root

    }

    private fun initViews() {
        setToolbar()

        viewModel.getCheque(args.id)

        binding.btnPrintCheque.setOnClickListener {
            hideKeyboard()
            viewModel.update(args.id)
        }

        setUpServerObserver()

    }

    private fun setData(cheque: Cheque) {
        setActiveCheques(cheque.items)
        setPrice(cheque.items)
    }

    private fun setUpServerObserver() {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.update.collect {
                when (it) {
                    is UiStateObject.LOADING -> {
                        showLoading()
                    }
                    is UiStateObject.SUCCESS -> {
                        (requireActivity() as MainActivity).printCheque(cheque)
                        dismissLoading()
                        requireActivity().onBackPressed()
                    }
                    is UiStateObject.ERROR -> {
                        dismissLoading()
                        toast("ERROR FROM SERVER ${it.message}")
                    }
                    else -> Unit
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.cheque.collect {
                when (it) {
                    is UiStateObject.LOADING -> {
                        showLoading()
                    }
                    is UiStateObject.SUCCESS -> {
                        dismissLoading()
                        cheque = it.data
                        setData(cheque)
                    }
                    is UiStateObject.ERROR -> {
                        dismissLoading()
                        toast("ERROR FROM SERVER ${it.message}")
                    }
                    else -> Unit
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun setPrice(data: List<ChequeProduct>) {
        var price = 0.0
        data.forEach {
            price += it.price * it.quantity
        }
        binding.tvPrice.text = price.toInt().toString().setAsPrice()
        binding.btnPrintCheque.text = getString(R.string.str_print_cheque) + price.toInt().toString().setAsPrice()
    }

    private fun setActiveCheques(data: ArrayList<ChequeProduct>) {
        val adapter = ActiveChequesAdapter(data)

        binding.rvProducts.adapter = adapter
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

            toggle.isDrawerIndicatorEnabled = false

            toggle.setToolbarNavigationClickListener {
                requireActivity().onBackPressed()
            }

            toggle.setHomeAsUpIndicator(R.drawable.ic_back)

        }
    }

    private fun showLoading() {
        binding.llLoading.visibility = View.VISIBLE
    }

    private fun dismissLoading() {
        binding.llLoading.visibility = View.GONE
    }

}