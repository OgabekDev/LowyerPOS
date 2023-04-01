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
import dagger.hilt.android.AndroidEntryPoint
import uz.loyver.loyver.R
import uz.loyver.loyver.databinding.FragmentCreateProductBinding
import uz.loyver.loyver.manager.SharedPref
import uz.loyver.loyver.model.Category
import uz.loyver.loyver.model.Product
import uz.loyver.loyver.model.ProductCreateUpdate
import uz.loyver.loyver.ui.activity.MainActivity
import uz.loyver.loyver.utils.*
import uz.loyver.loyver.viewmodel.main.products.create_product.CreateProductViewModel

@AndroidEntryPoint
class CreateProductFragment : Fragment(R.layout.fragment_create_product) {

    lateinit var binding: FragmentCreateProductBinding

    private val viewModel: CreateProductViewModel by viewModels()

    private val categories: ArrayList<Category> = ArrayList()

    private var categoryId: Int? = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentCreateProductBinding.inflate(inflater, container, false)

        initViews()

        setToolbar()

        setUpObservers()

        return binding.root

    }

    private fun setUpObservers() {

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.create.collect {
                when(it) {
                    is UiStateObject.LOADING -> {
                        showLoading()
                    }
                    is UiStateObject.SUCCESS -> {
                        dismissLoading()
                        val product = Product(it.data.id!!, it.data.name, it.data.type, it.data.categoryName, it.data.price)
                        viewModel.saveProduct(product)
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
            viewModel.product.collect {
                when(it) {
                    is UiStateObject.LOADING -> {
                        showLoading()
                    }
                    is UiStateObject.SUCCESS -> {
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

    private fun getCategoriesObserver() {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.categories.collect {
                when(it) {
                    is UiStateList.LOADING -> {
                        showLoading()
                    }
                    is UiStateList.SUCCESS -> {
                        dismissLoading()
                        categories.clear()
                        categories.addAll(it.data)
                        setSpinner(categories)
                        setSpinnerCategory()
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

    private fun initViews() {

        viewModel.getCategories()

        getCategoriesObserver()

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    showWarningDialog(
                        getString(R.string.str_unsaved_changes),
                        getString(R.string.str_unsaved_changes_msg),
                        {
                            // When ПРОДАЛЖИТЬ clicked
                            Navigation.findNavController(view).navigateUp()
                        },
                        {
                            // When ОТМЕНА clicked
                        })
                }
            })

    }

    private fun showWarningDialog(
        title: String,
        msg: String,
        ok: (() -> Unit),
        cancel: (() -> Unit)
    ) {
        val dialog = AlertDialog.Builder(requireContext())
        dialog.setTitle(title)
        dialog.setMessage(msg)
        dialog.setCancelable(true)
        dialog.setPositiveButton(getString(R.string.str_continue)) { _, _ ->
            ok.invoke()
        }
        dialog.setNegativeButton(getString(R.string.str_cancel)) { _, _ ->
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
                                findNavController().navigate(R.id.action_createProductFragment_to_createCategoryFragment)
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
            toolbar.title = getString(R.string.str_create_product)
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
                        hideKeyboard()
                        save()
                        return@setOnMenuItemClickListener true
                    }
                }
                return@setOnMenuItemClickListener false
            }
        }
    }

    private fun save() {

        if (binding.etProductName.text.isNullOrEmpty() || binding.etProductName.text.length < 2) {
            binding.etProductName.error = getString(R.string.str_fill_fields)
        } else if (binding.etPrice.text.isNullOrEmpty() || binding.etPrice.text.toString().length <= 2) {
            binding.etPrice.error = getString(R.string.str_fill_fields)
        } else {

            val name = binding.etProductName.text.toString()
            val type = if (binding.rbByWeight.isChecked) Constants.WEIGHT else Constants.EACH
            val price = binding.etPrice.text.toString().toInt()
            val category = if (categoryId == 0) null else categoryId.toString()

            val product = ProductCreateUpdate(name, type, price, category, null, null)
            viewModel.createProduct(product)
        }

    }

    private fun showLoading() {
        binding.llLoading.visibility = View.VISIBLE
    }

    private fun dismissLoading() {
        binding.llLoading.visibility = View.GONE
    }

}