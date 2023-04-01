package uz.loyver.loyver.ui.fragment

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
import uz.loyver.loyver.databinding.FragmentChangeAmountBinding
import uz.loyver.loyver.model.ChequeProduct
import uz.loyver.loyver.ui.activity.MainActivity
import uz.loyver.loyver.utils.Constants
import uz.loyver.loyver.utils.UiStateObject
import uz.loyver.loyver.utils.hideKeyboard
import uz.loyver.loyver.utils.toast
import uz.loyver.loyver.viewmodel.change_amount.ChangeAmountViewModel
import kotlin.time.Duration.Companion.days

@AndroidEntryPoint
class ChangeAmountFragment : Fragment(R.layout.fragment_change_amount) {

    private lateinit var binding: FragmentChangeAmountBinding

    private val args: ChangeAmountFragmentArgs by navArgs()

    private val viewModel: ChangeAmountViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChangeAmountBinding.inflate(inflater, container, false)

        dismissLoading()

        initViews()

        setUpObservers()

        return binding.root
    }

    private fun setUpObservers() {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.add.collect {
                when(it) {
                    is UiStateObject.LOADING -> {
                        showLoading()
                    }
                    is UiStateObject.SUCCESS -> {
                        dismissLoading()
                        toast("Добавлен")
                        requireActivity().onBackPressed()
                    }
                    is UiStateObject.ERROR -> {
                        dismissLoading()
                        toast("ERROR: ${it.message}")
                    }
                    else -> Unit
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.amount.collect {
                when(it) {
                    is UiStateObject.LOADING -> {
                        showLoading()
                    }
                    is UiStateObject.SUCCESS -> {
                        dismissLoading()

                        if (it.data == null) binding.tvAmount.text = "0"
                        else binding.tvAmount.text = if (it.data.type == Constants.WEIGHT) it.data.quantity.toString() else it.data.quantity.toInt().toString()
                    }
                    is UiStateObject.ERROR -> {
                        dismissLoading()
                        toast("ERROR FROM DATABASE: ${it.message}")
                    }
                    else -> Unit
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.delete.collect {
                when(it) {
                    is UiStateObject.LOADING -> {
                        showLoading()
                    }
                    is UiStateObject.SUCCESS -> {
                        dismissLoading()
                        toast("Удалено")
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

    private fun initViews() {
        setToolbar()

        binding.apply {

            tv1.setOnClickListener {
                onClickNumber(1)
            }

            tv2.setOnClickListener {
                onClickNumber(2)
            }

            tv3.setOnClickListener {
                onClickNumber(3)
            }

            tv4.setOnClickListener {
                onClickNumber(4)
            }

            tv5.setOnClickListener {
                onClickNumber(5)
            }

            tv6.setOnClickListener {
                onClickNumber(6)
            }

            tv7.setOnClickListener {
                onClickNumber(7)
            }

            tv8.setOnClickListener {
                onClickNumber(8)
            }

            tv9.setOnClickListener {
                onClickNumber(9)
            }

            tv0.setOnClickListener {
                onClickNumber(0)
            }

            tvDot.setOnClickListener {
                onDotClick()
            }

            ivBackspace.setOnClickListener {
                onBackspaceClick()
            }

            tvOK.setOnClickListener {
                if (tvAmount.text.toString().toDouble() == 0.0) {
                    viewModel.delete(args.product.id)
                } else {
                    val product = args.product
                    onOKClick(ChequeProduct(product.id, product.name, product.price, product.type, binding.tvAmount.text.toString().toDouble()))
                }
            }
        }

        viewModel.getAmount(args.product.id)

        binding.tvDot.isEnabled = args.product.type == Constants.WEIGHT

    }

    private fun setToolbar() {
        (requireActivity() as MainActivity).binding.apply {
            toolbar.title = args.product.name
            toolbar.menu.clear()
            toolbar.setBackgroundColor(Color.WHITE)

            toolbar.setTitleTextAppearance(requireContext(), R.style.titleBlack)

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

            toggle.setHomeAsUpIndicator(R.drawable.ic_close)
        }
    }

    private fun onClickNumber(number: Int) {
        hideKeyboard()
        binding.apply {
            var text = tvAmount.text
            text = if (text == "0") number.toString() else "${text}${number}"
            tvAmount.text = text
        }
    }

    private fun onDotClick() {
        hideKeyboard()
        binding.apply {
            var text = tvAmount.text
            if (!text.contains('.')) text = "$text."
            tvAmount.text = text
        }
    }

    private fun onBackspaceClick() {
        hideKeyboard()
        binding.apply {
            var text = tvAmount.text
            if (text.length == 1){
                if (text != "0") {
                    text = "0"
                }
            } else {
                text = text.substring(0, text.length - 1)
            }
            tvAmount.text = text
        }
    }

    private fun onOKClick(chequeProduct: ChequeProduct) {
        hideKeyboard()
        viewModel.add(chequeProduct)
    }

    private fun showLoading() {
        binding.llLoading.visibility = View.VISIBLE
    }

    private fun dismissLoading() {
        binding.llLoading.visibility = View.GONE
    }

}