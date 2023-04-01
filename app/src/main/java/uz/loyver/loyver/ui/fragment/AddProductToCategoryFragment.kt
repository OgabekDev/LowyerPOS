package uz.loyver.loyver.ui.fragment

import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.cancel
import uz.loyver.loyver.R
import uz.loyver.loyver.adapter.AddProductsToCategoryAdapter
import uz.loyver.loyver.databinding.FragmentAddProductToCategoryBinding
import uz.loyver.loyver.model.CategoryProduct
import uz.loyver.loyver.model.ChangeCategoryProducts
import uz.loyver.loyver.model.Customer
import uz.loyver.loyver.model.Product
import uz.loyver.loyver.ui.activity.MainActivity
import uz.loyver.loyver.utils.UiStateList
import uz.loyver.loyver.utils.UiStateObject
import uz.loyver.loyver.utils.hideKeyboard
import uz.loyver.loyver.utils.toast
import uz.loyver.loyver.viewmodel.add_category.add_product.AddProductToCategoryViewModel

@AndroidEntryPoint
class AddProductToCategoryFragment : Fragment(R.layout.fragment_add_product_to_category) {

    private lateinit var binding: FragmentAddProductToCategoryBinding

    private val viewModel: AddProductToCategoryViewModel by viewModels()

    private val args: AddProductToCategoryFragmentArgs by navArgs()

    private lateinit var products: ArrayList<CategoryProduct>
    private lateinit var allProducts: ArrayList<Product>

    private val checkedProducts: ArrayList<Int> = ArrayList()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = FragmentAddProductToCategoryBinding.inflate(inflater, container, false)

        initViews()

        return binding.root

    }

    private fun initViews() {
        setToolBar()

        binding.apply {
            ivClear.setOnClickListener {
                etSearch.setText("")
            }

            etSearch.addTextChangedListener(object: TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    viewModel.searchProducts("%${s.toString()}%")
                }

                override fun afterTextChanged(s: Editable?) {}

            })

        }

        viewModel.getAllProducts()

        setUpObserver()

    }

    private fun setUpObserver() {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.product.collect {
                when(it) {
                    is UiStateList.LOADING -> {
                        showLoading()
                    }
                    is UiStateList.SUCCESS -> {
                        dismissLoading()
                        allProducts = it.data as ArrayList
                        setDate(it.data)
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
            viewModel.searchProducts.collect {
                when(it) {
                    is UiStateList.LOADING -> {
                        showLoading()
                    }
                    is UiStateList.SUCCESS -> {
                        dismissLoading()
                        setDate(it.data)
                    }
                    is UiStateList.ERROR -> {
                        dismissLoading()
                        toast("ERROR FROM DATABASE: ${it.message}")
                    }
                    else -> Unit
                }
            }
        }

    }

    private fun setDate(data: List<Product>) {

        products = ArrayList<CategoryProduct>().apply {
            for (i in data) {
                if (i.category != args.category.title) {
                    add(CategoryProduct(i.id, i.name, i.category, checkedProducts.contains(i.id)))
                }
            }
        }

        val adapter = AddProductsToCategoryAdapter(products)

        adapter.onClick = { id, value ->
            hideKeyboard()
            if (value) {
                checkedProducts.add(id)
            } else {
                checkedProducts.remove(id)
            }
        }

        binding.rvProducts.adapter = adapter

    }

    private fun setToolBar() {
        (requireActivity() as MainActivity).binding.apply {
            toolbar.title = getString(R.string.str_add_product_to_category_)
            toolbar.menu.clear()
            toolbar.inflateMenu(R.menu.toolbar_create_product)

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

            requireActivity().findViewById<TextView>(R.id.navSave).setTextColor(Color.parseColor("#4190DA"))

            llOnHome.visibility = View.GONE

            toggle.isDrawerIndicatorEnabled = false

            toggle.setToolbarNavigationClickListener {
                requireActivity().onBackPressed()
            }

            toolbar.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.navSave -> {
                        changeCategory()
                    }
                }
                return@setOnMenuItemClickListener false
            }

            toggle.setHomeAsUpIndicator(R.drawable.ic_close)
        }
    }

    private fun changeCategory() {
        viewModel.changeCategory(ChangeCategoryProducts(args.category.id, checkedProducts))

        setUpServerObserver()
    }

    private fun setUpServerObserver() {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.changeCategory.collect {
                when (it) {
                    is UiStateObject.LOADING -> {
                        showLoading()
                    }
                    is UiStateObject.SUCCESS -> {
                        saveToDatabase(it.data)
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

    private fun saveToDatabase(data: ChangeCategoryProducts) {
        for (i in data.products) {
            for (j in allProducts) {
                if (i == j.id) {
                    j.category = args.category.title
                    viewModel.saveProduct(j)
                    break
                }
            }
        }
        dismissLoading()
        requireActivity().onBackPressed()
    }

    private fun showLoading() {
        binding.llLoading.visibility = View.VISIBLE
    }

    private fun dismissLoading() {
        binding.llLoading.visibility = View.GONE
    }

}