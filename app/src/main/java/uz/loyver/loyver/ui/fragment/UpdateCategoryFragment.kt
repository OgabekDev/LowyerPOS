package uz.loyver.loyver.ui.fragment

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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
import uz.loyver.loyver.databinding.FragmentCreateCategoryBinding
import uz.loyver.loyver.databinding.FragmentUpdateCategoryBinding
import uz.loyver.loyver.model.Category
import uz.loyver.loyver.model.CategoryHttp
import uz.loyver.loyver.model.Product
import uz.loyver.loyver.ui.activity.MainActivity
import uz.loyver.loyver.utils.*
import uz.loyver.loyver.viewmodel.main.category.update_category.UpdateCategoryViewModel

@AndroidEntryPoint
class UpdateCategoryFragment : Fragment(R.layout.fragment_update_category) {

    private lateinit var binding: FragmentUpdateCategoryBinding

    private val viewModel: UpdateCategoryViewModel by viewModels()

    private val args: UpdateCategoryFragmentArgs by navArgs()

    private lateinit var category: Category

    private lateinit var newCategory: Category

    private lateinit var categoryProducts: ArrayList<Product>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentUpdateCategoryBinding.inflate(inflater, container, false)

        initViews()

        return binding.root

    }

    private fun initViews() {
        setToolbar()

        viewModel.getCategory(args.id)

        setUpObservers()

        binding.llProductDelete.setOnClickListener {
            hideKeyboard()
            showWarningDialog(getString(R.string.str_delete_title_category), getString(R.string.str_delete_category_msg), getString(R.string.str_delete), { viewModel.deleteCategory(args.id) }, getString(R.string.str_cancel_), {})

        }

        binding.btnAddProductToCategory.setOnClickListener {
            hideKeyboard()
            val action = UpdateCategoryFragmentDirections.actionUpdateCategoryFragmentToAddProductToCategoryFragment(category)
            findNavController().navigate(action)
        }

        binding.btnCreateProductToCategory.setOnClickListener {
            hideKeyboard()
            findNavController().navigate(R.id.action_updateCategoryFragment_to_createProductFragment)
        }

    }

    private fun setUpObservers() {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.getCategory.collect {
                when(it) {
                    is UiStateObject.LOADING -> {
                        showLoading()
                    }
                    is UiStateObject.SUCCESS -> {
                        dismissLoading()
                        category = it.data
                        viewModel.getCategoryProducts(it.data.title)
                        binding.etCategoryName.setText(it.data.title)
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
            viewModel.updateCategory.collect {
                when(it) {
                    is UiStateObject.LOADING -> {
                        showLoading()
                    }
                    is UiStateObject.SUCCESS -> {
                        dismissLoading()
                        newCategory = it.data
                        viewModel.updateCategoryDB(it.data)
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
            viewModel.deleteCategory.collect {
                when(it) {
                    is UiStateObject.LOADING -> {
                        showLoading()
                    }
                    is UiStateObject.SUCCESS -> {
                        dismissLoading()
                        viewModel.deleteCategoryDB(args.id)
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
            viewModel.updateCategoryDB.collect {
                when(it) {
                    is UiStateObject.LOADING -> {
                        showLoading()
                    }
                    is UiStateObject.SUCCESS -> {
                        updateCategoryProducts(newCategory)
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
            viewModel.deleteCategoryDB.collect {
                when(it) {
                    is UiStateObject.LOADING -> {
                        showLoading()
                    }
                    is UiStateObject.SUCCESS -> {
                        dismissLoading()
                        toast(getString(R.string.str_all_done))
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

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.getCategoryProducts.collect {
                when(it) {
                    is UiStateList.LOADING -> {
                        showLoading()
                    }
                    is UiStateList.SUCCESS -> {
                        dismissLoading()
                        categoryProducts = it.data as ArrayList
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (category.title != binding.etCategoryName.text.toString()) {
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

    private fun updateCategoryProducts(category: Category) {
        for (i in categoryProducts) {
            i.category = category.title
            viewModel.updateProductDB(i)
        }
        dismissLoading()
        toast(getString(R.string.str_all_done))
        Navigation.findNavController(requireView()).navigateUp();
    }

    private fun setToolbar() {
        (requireActivity() as MainActivity).binding.apply {
            toolbar.title = getString(R.string.str_edit_category)
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
                when (it.itemId) {
                    R.id.navSave -> {
                        save()
                        return@setOnMenuItemClickListener true
                    }
                }
                return@setOnMenuItemClickListener false
            }
        }
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

    private fun save() {
        hideKeyboard()
        if (binding.etCategoryName.text.isNullOrEmpty() || binding.etCategoryName.text.length <= 2) {
            binding.etCategoryName.error = getString(R.string.str_fill_fields)
        } else {
            val title = binding.etCategoryName.text.toString()

            val category = CategoryHttp(title)
            viewModel.updateCategory(args.id, category)
        }
    }

    private fun showLoading() {
        binding.llLoading.visibility = View.VISIBLE
    }

    private fun dismissLoading() {
        binding.llLoading.visibility = View.GONE
    }

}