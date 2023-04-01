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
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import uz.loyver.loyver.R
import uz.loyver.loyver.databinding.FragmentCreateCategoryBinding
import uz.loyver.loyver.manager.SharedPref
import uz.loyver.loyver.model.CategoryHttp
import uz.loyver.loyver.model.Product
import uz.loyver.loyver.ui.activity.MainActivity
import uz.loyver.loyver.utils.UiStateObject
import uz.loyver.loyver.utils.hideKeyboard
import uz.loyver.loyver.utils.toast
import uz.loyver.loyver.viewmodel.main.category.create_category.CreateCategoryViewModel

@AndroidEntryPoint
class CreateCategoryFragment : Fragment(R.layout.fragment_create_category) {

    private lateinit var binding: FragmentCreateCategoryBinding

    private val viewModel: CreateCategoryViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentCreateCategoryBinding.inflate(inflater, container, false)

        initViews()

        return binding.root

    }

    private fun initViews() {
        hideKeyboard()
        setToolbar()

        setUpObservers()

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
                        if (SharedPref(requireContext()).getBoolean("isActiveCategory")) {
                            SharedPref(requireContext()).saveInt("activeCategory", it.data.id)
                        }
                        viewModel.saveProduct(it.data)
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
            viewModel.save.collect {
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

    }

    private fun setToolbar() {
        (requireActivity() as MainActivity).binding.apply {
            toolbar.title = getString(R.string.str_create_category)
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
                SharedPref(requireContext()).saveBoolean("isActiveCategory", false)
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

        if (binding.etCategoryName.text.isNullOrEmpty() || binding.etCategoryName.text.length <= 2) {
            binding.etCategoryName.error = getString(R.string.str_fill_fields)
        } else {

            val title = binding.etCategoryName.text.toString()

            val category = CategoryHttp(title)
            viewModel.createProduct(category)
        }

    }

    private fun showLoading() {
        binding.llLoading.visibility = View.VISIBLE
    }

    private fun dismissLoading() {
        binding.llLoading.visibility = View.GONE
    }

}