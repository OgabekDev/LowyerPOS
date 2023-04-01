package uz.loyver.loyver.ui.fragment.main

import android.app.DatePickerDialog
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
import uz.loyver.loyver.adapter.CategoryAdapter
import uz.loyver.loyver.databinding.FragmentCategoriesBinding
import uz.loyver.loyver.model.Category
import uz.loyver.loyver.model.Product
import uz.loyver.loyver.ui.activity.MainActivity
import uz.loyver.loyver.utils.UiStateList
import uz.loyver.loyver.utils.UiStateObject
import uz.loyver.loyver.utils.hideKeyboard
import uz.loyver.loyver.utils.toast
import uz.loyver.loyver.viewmodel.main.category.CategoryViewModel
import java.util.Calendar

@AndroidEntryPoint
class CategoriesFragment : Fragment(R.layout.fragment_categories) {

    private lateinit var binding: FragmentCategoriesBinding

    private val viewModel: CategoryViewModel by viewModels()

    private lateinit var categories: ArrayList<Category>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = FragmentCategoriesBinding.inflate(inflater, container, false)

        initViews()

        (requireActivity() as MainActivity).binding.navView.menu.getItem(3).isChecked = true

        return binding.root

    }

    private fun initViews() {
        setToolbar()

        viewModel.getCategories()

        setUpObservers()

        binding.etSearch.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.searchCategories("%${s.toString()}%")
            }

            override fun afterTextChanged(s: Editable?) {}

        })

        binding.btnAddCategory.setOnClickListener {
            hideKeyboard()
            val action = CategoriesFragmentDirections.actionCategoriesFragmentToCreateCategoryFragment()
            findNavController().navigate(action)
        }

    }

    private fun setUpObservers() {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.categories.collect {
                when(it) {
                    is UiStateList.LOADING -> {
                        showLoading()
                    }
                    is UiStateList.SUCCESS -> {
                        dismissLoading()
                        categories = it.data as ArrayList
                        setData(categories)
                        viewModel.deleteCategories()
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
                        for (i in categories) {
                            viewModel.addCategory(i)
                        }
                        dismissLoading()
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

    private fun setData(data: List<Category>) {

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

        val adapter = CategoryAdapter(data as ArrayList)

        adapter.onClick = {
            hideKeyboard()
            val action = CategoriesFragmentDirections.actionCategoriesFragmentToUpdateCategoryFragment(it.id)
            findNavController().navigate(action)
        }

        binding.rvCategories.adapter = adapter

    }

    private fun setToolbar() {
        (requireActivity() as MainActivity).binding.apply {
            toolbar.title = getString(R.string.str_all_categories)
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

    private fun showLoading() {
        binding.llLoading.visibility = View.VISIBLE
    }

    private fun dismissLoading() {
        binding.llLoading.visibility = View.GONE
    }

}