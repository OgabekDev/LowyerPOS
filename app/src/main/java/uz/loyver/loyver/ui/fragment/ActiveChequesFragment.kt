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
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import uz.loyver.loyver.R
import uz.loyver.loyver.adapter.ActiveChequesAdapter
import uz.loyver.loyver.databinding.FragmentActiveChequesBinding
import uz.loyver.loyver.manager.SharedPref
import uz.loyver.loyver.model.ChequeCreate
import uz.loyver.loyver.model.ChequeProduct
import uz.loyver.loyver.model.ChequeProductCreate
import uz.loyver.loyver.ui.activity.MainActivity
import uz.loyver.loyver.utils.*
import uz.loyver.loyver.viewmodel.active_cheque.ActiveChequeViewModel

@AndroidEntryPoint
class ActiveChequesFragment : Fragment(R.layout.fragment_active_cheques) {

    private lateinit var binding: FragmentActiveChequesBinding

    private val viewModel: ActiveChequeViewModel by viewModels()

    private lateinit var chequeProducts: ArrayList<ChequeProduct>

    private var shouldPrint: Boolean = false

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = FragmentActiveChequesBinding.inflate(inflater, container, false)

        initViews()

        return binding.root

    }

    private fun initViews() {

        setToolbar()

        getCheques()

        setDatabaseObserver()

        binding.btnPrintCheque.setOnClickListener {
            hideKeyboard()
            val user = SharedPref(requireContext()).getInt("customer")
            if (user == 0) {
                toast("Mijoz qoshishni unitdingiz")
            } else {
                shouldPrint = true
                val products = ArrayList<ChequeProductCreate>().apply {
                    for (i in chequeProducts) {
                        add(ChequeProductCreate(i.id, i.quantity.toFloat()))
                    }
                }
                val cheque = ChequeCreate(user, false, products)

                viewModel.createCheque(cheque)

                setUpServerObserver()
            }
        }

        binding.btnSaveCheque.setOnClickListener {
            hideKeyboard()
            val user = SharedPref(requireContext()).getInt("customer")
            if (user == 0) {
                toast("Добавьте клиента")
            } else {
                shouldPrint = false
                val products = ArrayList<ChequeProductCreate>().apply {
                    for (i in chequeProducts) {
                        add(ChequeProductCreate(i.id, i.quantity.toFloat()))
                    }
                }
                val cheque =
                    ChequeCreate(user, true, products)
                viewModel.createCheque(cheque)
                setUpServerObserver()
            }
        }

    }

    private fun setUpServerObserver() {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.create.collect {
                when (it) {
                    is UiStateObject.LOADING -> {
                        showLoading()
                    }
                    is UiStateObject.SUCCESS -> {
                        if (shouldPrint) {
                            (requireActivity() as MainActivity).printCheque(it.data)
                        } else {
                            toast(getString(R.string.str_saved))
                        }
                        SharedPref(requireContext()).saveInt("customer", 0)
                        viewModel.deleteCheques()

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

    private fun setDatabaseObserver() {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.cheques.collect {
                when (it) {
                    is UiStateList.LOADING -> {
                        showLoading()
                    }
                    is UiStateList.SUCCESS -> {
                        dismissLoading()
                        setPrice(it.data)
                        chequeProducts = it.data as ArrayList
                        (requireActivity() as MainActivity).binding.tvCountCheques.text = it.data.size.toString()
                        if (it.data.isEmpty()) requireActivity().onBackPressed() else setActiveCheques(it.data)
                    }
                    is UiStateList.ERROR -> {
                        dismissLoading()
                        toast("ERROR FROM DATABASE: ${it.message}")
                    }
                    else -> Unit
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.deleteCheques.collect {
                when (it) {
                    is UiStateObject.LOADING -> {
                        showLoading()
                    }
                    is UiStateObject.SUCCESS -> {
                        dismissLoading()
                        requireActivity().onBackPressed()
                    }
                    is UiStateObject.ERROR -> {
                        dismissLoading()
                        toast("ERROR FROM DATABASE: ${it.message}")
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

    private fun getCheques() {
        viewModel.getAllCheques()
    }

    private fun setActiveCheques(data: ArrayList<ChequeProduct>) {
        val adapter = ActiveChequesAdapter(data)

        adapter.onClick = {
            hideKeyboard()
            val action = ActiveChequesFragmentDirections.actionActiveChequesFragmentToChangeAmountFragment(it)
            findNavController().navigate(action)
        }

        binding.rvProducts.adapter = adapter
    }

    private fun setToolbar() {
        (requireActivity() as MainActivity).binding.apply {
            llOnHome.visibility = View.VISIBLE

            llOnHome.setOnClickListener {
            }

            toolbar.title = ""
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
            drawerLayout.addDrawerListener(toggle)
            toggle.syncState()

            ivAddPerson.setImageResource(if (SharedPref(requireContext()).getInt("customer") != 0) R.drawable.img_done_add_person else R.drawable.img_add_person)

            llOnHome.visibility = View.VISIBLE
            toolbar.inflateMenu(R.menu.toolbar_active_cheque)

            toggle.isDrawerIndicatorEnabled = false

            toggle.setToolbarNavigationClickListener {
                requireActivity().onBackPressed()
            }

            toolbar.setOnMenuItemClickListener {
                when (it?.itemId) {
                    R.id.navRefresh -> {
                        viewModel.getAllCheques()
                        return@setOnMenuItemClickListener true
                    }
                }
                return@setOnMenuItemClickListener false
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