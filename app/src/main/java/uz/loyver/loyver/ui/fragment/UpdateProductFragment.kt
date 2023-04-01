package uz.loyver.loyver.ui.fragment

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.ArrayAdapter
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import dagger.hilt.android.AndroidEntryPoint
import uz.loyver.loyver.R
import uz.loyver.loyver.databinding.FragmentUpdateProductBinding
import uz.loyver.loyver.manager.SharedPref
import uz.loyver.loyver.model.Category
import uz.loyver.loyver.model.Product
import uz.loyver.loyver.model.ProductCreateUpdate
import uz.loyver.loyver.model.ProductUpdate
import uz.loyver.loyver.ui.activity.MainActivity
import uz.loyver.loyver.utils.*
import uz.loyver.loyver.viewmodel.main.products.update_product.UpdateProductViewModel

@AndroidEntryPoint
class UpdateProductFragment : Fragment(R.layout.fragment_update_product) {

    lateinit var binding: FragmentUpdateProductBinding

    private val categories: ArrayList<Category> = ArrayList()

    private val viewModel: UpdateProductViewModel by viewModels()

    private val args: UpdateProductFragmentArgs by navArgs()

    private var categoryId: Int? = 0

    private lateinit var product: ProductUpdate

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentUpdateProductBinding.inflate(inflater, container, false)

        initViews()

        setToolbar()

        return binding.root

    }

    private fun initViews() {

        viewModel.getCategories()

        setUpCategoryObserver()

        viewModel.getProduct(args.id)

        setUpServerObservers()

        setUpDatabaseObservers()

        binding.llDeleteProduct.setOnClickListener {
            hideKeyboard()
            showWarningDialog(getString(R.string.str_delete_title), getString(R.string.str_delete_msg), getString(R.string.str_delete), { viewModel.deleteProduct(args.id) }, getString(R.string.str_cancel_), {})
        }

    }

    private fun setUpDatabaseObservers() {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.updateProductDB.collect {
                when(it) {
                    is UiStateObject.LOADING -> {
                        showLoading()
                    }
                    is  UiStateObject.SUCCESS -> {
                        dismissLoading()
//                        toast(getString(R.string.str_all_done))
                        Navigation.findNavController(binding.root).navigateUp()
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
            viewModel.deleteProductDB.collect {
                when(it) {
                    is UiStateObject.LOADING -> {
                        showLoading()
                    }
                    is  UiStateObject.SUCCESS -> {
                        dismissLoading()
                        toast(getString(R.string.str_all_done))
                        Navigation.findNavController(binding.root).navigateUp()
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

    private fun setUpServerObservers() {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.getProduct.collect {
                when(it) {
                    is UiStateObject.LOADING -> {
                        showLoading()
                    }
                    is  UiStateObject.SUCCESS -> {
                        dismissLoading()
                        product = it.data
                        setData(product)
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
            viewModel.updateProduct.collect {
                when(it) {
                    is UiStateObject.LOADING -> {
                        showLoading()
                    }
                    is  UiStateObject.SUCCESS -> {
                        dismissLoading()
                        updateDatabase(it.data)
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
            viewModel.deleteProduct.collect {
                when(it) {
                    is UiStateObject.LOADING -> {
                        showLoading()
                    }
                    is  UiStateObject.SUCCESS -> {
                        dismissLoading()
                        deleteProduct()
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

    private fun deleteProduct() {
        viewModel.deleteProductDB(product.id!!)
    }

    private fun updateDatabase(data: ProductUpdate) {
        val product = Product(data.id!!, data.name, data.type, data.categoryName, data.price)
        viewModel.updateProductDB(product)
    }

    private fun setData(product: ProductUpdate) {
        binding.apply {
            etProductName.setText(product.name)
            etPrice.setText(product.price.toString())
            rbByPiece.isChecked = product.type == Constants.EACH
            rbByWeight.isChecked = product.type == Constants.WEIGHT

            var category = 0
            for (i in categories.indices) {
                if (categories[i].id == product.category) {
                    category = i + 2
                    break
                }
            }

            categoryId = product.category

            sCategory.setSelection(category)

            setSpinnerCategory()

        }
    }

    private fun setUpCategoryObserver() {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.getCategories.collect {
                when(it) {
                    is UiStateList.LOADING -> {
                        showLoading()
                    }
                    is  UiStateList.SUCCESS -> {
                        dismissLoading()
                        categories.clear()
                        categories.addAll(it.data)
                        setSpinner(categories)
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

    private fun setSpinnerCategory() {
        if (SharedPref(requireContext()).getBoolean("isActiveCategory")) {
            binding.etProductName.setText(SharedPref(requireContext()).getString("tempCategoryName"))
            binding.etPrice.setText(SharedPref(requireContext()).getString("tempCategoryPrice"))
            val categoryId = SharedPref(requireContext()).getInt("activeCategory")
            for (i in categories.indices) {
                if (categories[i].id == categoryId) {
                    binding.sCategory.setSelection(i + 2)
                    SharedPref(requireContext()).saveBoolean("isActiveCategory", false)
                    break
                }
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    val type = if (binding.rbByWeight.isChecked) Constants.WEIGHT else Constants.EACH
                    if (product.category != categoryId || product.name != binding.etProductName.text.toString() || product.price != binding.etPrice.text.toString().toInt() || product.type != type) {
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

    private fun setSpinner(categoryArrayList: ArrayList<Category>) {

        val categories = ArrayList<String>()

        categories.add(getString(R.string.str_non_category))
        categories.add(getString(R.string.str_create_category))

        for (i in categoryArrayList) {
            categories.add(i.title)
        }

        binding.apply {

            val adapter: ArrayAdapter<String> =
                ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)

            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

            sCategory.adapter = adapter

            sCategory.onItemSelectedListener = object : OnItemSelectedListener {
                override fun onItemSelected(
                    p0: AdapterView<*>?,
                    p1: View?,
                    position: Int,
                    p3: Long
                ) {
                    hideKeyboard()
                    categoryId = if (position >= 2) categoryArrayList[position - 2].id else 0
                    when (position) {
                        1 -> {
                            sCategory.post {
                                sCategory.setSelection(0)
                                SharedPref(requireContext()).saveBoolean("isActiveCategory", true)
                                SharedPref(requireContext()).saveString("tempCategoryName", binding.etProductName.text.toString())
                                SharedPref(requireContext()).saveString("tempCategoryPrice", binding.etPrice.text.toString())
                                findNavController().navigate(R.id.action_updateProductFragment_to_createCategoryFragment)
                            }
                        }
                    }
                }

                override fun onNothingSelected(p0: AdapterView<*>?) {}

            }

        }

    }

    private fun setToolbar() {
        (requireActivity() as MainActivity).binding.apply {
            toolbar.title = getString(R.string.str_edit___)
            toolbar.menu.clear()
            toolbar.inflateMenu(R.menu.toolbar_create_product)
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
                when (it?.itemId) {
                    R.id.navSave -> {
                        save()
                        return@setOnMenuItemClickListener true
                    }
                }
                return@setOnMenuItemClickListener false
            }
        }
    }

    private fun save() {
        hideKeyboard()
        binding.apply {
            if (binding.etProductName.text.isNullOrEmpty() || binding.etProductName.text.length < 2) {
                binding.etProductName.error = getString(R.string.str_fill_fields)
            } else if (binding.etPrice.text.isNullOrEmpty() || binding.etPrice.text.toString().length <= 2) {
                binding.etPrice.error = getString(R.string.str_fill_fields)
            } else {
                val name = binding.etProductName.text.toString()
                val type = if (rbByPiece.isChecked) Constants.EACH else Constants.WEIGHT
                val price = etPrice.text.toString()
                if (product.category == 0 && categoryId == 0) {
                    val category = null
                    val product = ProductCreateUpdate(name, type, price.toInt(), category, null, null)
                    viewModel.updateProduct(args.id, product)
                } else if (categoryId == 0 && product.category != 0){
                    val category = ""
                    val product = ProductCreateUpdate(name, type, price.toInt(), category, null, null)
                    viewModel.updateProduct(args.id, product)
                } else {
                    val category = categoryId
                    val product = ProductUpdate(name, type, price.toInt(), category, null, null)
                    viewModel.updateProduct(args.id, product)
                }
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