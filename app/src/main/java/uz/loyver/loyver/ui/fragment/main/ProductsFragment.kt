package uz.loyver.loyver.ui.fragment.main

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
import uz.loyver.loyver.adapter.ProductsAdapter
import uz.loyver.loyver.databinding.FragmentProductsBinding
import uz.loyver.loyver.model.Product
import uz.loyver.loyver.ui.activity.MainActivity
import uz.loyver.loyver.utils.UiStateList
import uz.loyver.loyver.utils.hideKeyboard
import uz.loyver.loyver.utils.toast
import uz.loyver.loyver.viewmodel.main.products.ProductsViewModel

@AndroidEntryPoint
class ProductsFragment : Fragment(R.layout.fragment_products) {

    private lateinit var binding: FragmentProductsBinding

    private val viewModel: ProductsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentProductsBinding.inflate(inflater, container, false)

        setUpObservers()

        (requireActivity() as MainActivity).binding.navView.menu.getItem(2).isChecked = true

        return binding.root

    }

    private fun initViews() {


        binding.btnAddProduct.setOnClickListener {
            hideKeyboard()
            val action = ProductsFragmentDirections.actionProductsFragmentToCreateProductFragment()
            findNavController().navigate(action)
        }

    }

    private fun setToolbar() {
        (requireActivity() as MainActivity).binding.apply {
            toolbar.title = getString(R.string.str_all_products)
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.getProducts()

        initViews()

        setToolbar()

        binding.etSearch.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.searchProducts("%${s.toString()}%")
            }

            override fun afterTextChanged(s: Editable?) {}

        })

    }

    private fun setUpObservers() {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.products.collect {
                when(it) {
                    is UiStateList.LOADING -> {
                        showLoading()
                    }
                    is UiStateList.SUCCESS -> {
                        dismissLoading()
                        setData(it.data)
                    }
                    is UiStateList.ERROR -> {
                        dismissLoading()
                        toast("ERROR: ${it.message}")
                    }
                    else -> Unit
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.search.collect {
                when(it) {
                    is UiStateList.LOADING -> {
                        showLoading()
                    }
                    is UiStateList.SUCCESS -> {
                        dismissLoading()
                        setData(it.data)
                    }
                    is UiStateList.ERROR -> {
                        dismissLoading()
                        toast("ERROR: ${it.message}")
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun setData(data: List<Product>) {

        binding.llList.visibility = View.GONE
        binding.llNonList.visibility = View.GONE

        if (data.isEmpty()) {
            binding.llLoading.visibility = View.GONE
            binding.llList.visibility = View.GONE
            binding.llNonList.visibility = View.VISIBLE
        } else {
            binding.llLoading.visibility = View.GONE
            binding.llList.visibility = View.VISIBLE
            binding.llNonList.visibility = View.GONE
        }

        val adapter = ProductsAdapter(data as ArrayList<Product>)

        adapter.onClick = {
            hideKeyboard()
            val action = ProductsFragmentDirections.actionProductsFragmentToUpdateProductFragment(it.id)
            findNavController().navigate(action)
        }

        binding.rvProducts.adapter = adapter

    }

    private fun showLoading() {
        binding.llLoading.visibility = View.VISIBLE
    }

    private fun dismissLoading() {
        binding.llLoading.visibility = View.GONE
    }

}