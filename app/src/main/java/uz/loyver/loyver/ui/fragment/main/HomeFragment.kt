package uz.loyver.loyver.ui.fragment.main

import android.app.AlertDialog
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.navigation.NavigationView
import dagger.hilt.android.AndroidEntryPoint
import uz.loyver.loyver.R
import uz.loyver.loyver.adapter.HomeAdapter
import uz.loyver.loyver.databinding.FragmentHomeBinding
import uz.loyver.loyver.manager.SharedPref
import uz.loyver.loyver.model.*
import uz.loyver.loyver.ui.activity.MainActivity
import uz.loyver.loyver.utils.*
import uz.loyver.loyver.viewmodel.main.home.HomeViewModel

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var binding: FragmentHomeBinding

    private val viewModel: HomeViewModel by viewModels()

    private lateinit var categoriesDB: List<Category>
    private lateinit var productsDB: List<Product>

    private lateinit var activeCheques: ArrayList<ChequeProduct>

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        binding = FragmentHomeBinding.inflate(inflater, container, false)

        setUpObserves()

        (requireActivity() as MainActivity).binding.navView.menu.getItem(0).isChecked = true

        initViews()

        return binding.root

    }

    private fun initViews() {

        binding.apply {
            ivSearch.setOnClickListener {
                llSearch.visibility = View.VISIBLE
                llCategory.visibility = View.GONE
                binding.etSearch.setText("")
            }
            ivClose.setOnClickListener {
                llCategory.visibility = View.VISIBLE
                llSearch.visibility = View.GONE
                viewModel.getDBProducts()
            }

        }

        binding.btnOpenProducts.setOnClickListener {
            hideKeyboard()
            findNavController().navigate(R.id.action_homeFragment_to_productsFragment)
        }

        binding.etSearch.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                viewModel.search("%${s.toString()}%")
            }

            override fun afterTextChanged(s: Editable?) {}

        })

        binding.llPrint.setOnClickListener {
            viewModel.getChequeSize()
            hideKeyboard()
            val user = SharedPref(requireContext()).getInt("customer")
            if (user == 0) {
                toast("Добавьте клиента")
            } else if (activeCheques.isNotEmpty()) {
                val products = ArrayList<ChequeProductCreate>().apply {
                    for (i in activeCheques) {
                        add(ChequeProductCreate(i.id, i.quantity.toFloat()))
                    }
                }
                val cheque = ChequeCreate(user, false, products)

                viewModel.createCheque(cheque)

                printObserver()

            }
        }

    }

    private fun setProducts(data: ArrayList<Product>) {

        if (data.isEmpty()) {
            binding.llNonList.visibility = View.VISIBLE
            binding.llList.visibility = View.GONE
        } else {
            binding.llNonList.visibility = View.GONE
            binding.llList.visibility = View.VISIBLE
        }

        val adapter = HomeAdapter(data)

        adapter.onClick = {
            hideKeyboard()
            saveProductToActiveCheque(it)
        }

        binding.rvProducts.adapter = adapter

    }

    private fun saveProductToActiveCheque(product: Product) {
        val chequeProduct = ChequeProduct(product.id, product.name, product.price.toDouble(), product.type, 1.0)
        val action = HomeFragmentDirections.actionHomeFragmentToChangeAmountFragment(chequeProduct)
        findNavController().navigate(action)
    }

    private fun setSpinner(data: ArrayList<Category>) {

        val categories = ArrayList<String>()
        categories.add("Все товары")
        for (i in data) categories.add(i.title)

        val adapter: ArrayAdapter<String> =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        binding.sCategory.adapter = adapter

        binding.sCategory.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                p0: AdapterView<*>?,
                p1: View?,
                position: Int,
                p3: Long
            ) {
                hideKeyboard()
                binding.sCategory.post {
                    if (position != 0) {
                        viewModel.getDBCategoryProducts(categories[position])
                    } else {
                        viewModel.getDBProducts()
                    }
                }
            }

            override fun onNothingSelected(p0: AdapterView<*>?) {}

        }

    }

    private fun setToolbar() {
        (requireActivity() as MainActivity).binding.apply {

            val toggle = ActionBarDrawerToggle(
                requireActivity(),
                drawerLayout,
                toolbar,
                R.string.navigation_drawer_open,
                R.string.navigation_drawer_close
            )

            drawerLayout.addDrawerListener(toggle)

            toggle.syncState()

            navView.setNavigationItemSelectedListener(this@HomeFragment)

            llOnHome.visibility = View.VISIBLE

            toolbar.title = ""
            toolbar.menu.clear()
            toolbar.setBackgroundColor(Color.parseColor("#0071DC"))

            toolbar.setTitleTextAppearance(requireContext(), R.style.titleWhite)

            toolbar.inflateMenu(R.menu.toolbar_main)

            llOnHomeCheque.setOnClickListener {
                if (binding.tvCountCheques.text != "0") findNavController().navigate(R.id.activeChequesFragment)
            }

            ivAddPerson.setImageResource(if (SharedPref(requireContext()).getInt("customer") != 0) R.drawable.img_done_add_person else R.drawable.img_add_person)

            ivAddPerson.setOnClickListener {
                findNavController().navigate(R.id.addPersonFragment)
            }

            toolbar.setOnMenuItemClickListener {
                hideKeyboard()
                when (it?.itemId) {
                    R.id.navClearCheque -> {
                        clearCheque()
                        return@setOnMenuItemClickListener true
                    }
                    R.id.navLogOut -> {
                        logOut()
                    }
                }
                return@setOnMenuItemClickListener false
            }

        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.navSell -> {
                if (findNavController().currentDestination?.id != R.id.homeFragment) {
                    findNavController().navigate(R.id.homeFragment)
                }
            }
            R.id.navCheque -> {
                if (findNavController().currentDestination?.id != R.id.chequeHistoryFragment) {
                    findNavController().navigate(R.id.chequeHistoryFragment)
                }
            }
            R.id.navProducts -> {
                if (findNavController().currentDestination?.id != R.id.productsFragment) {
                    findNavController().navigate(R.id.productsFragment)
                }
            }
            R.id.navCategory -> {
                if (findNavController().currentDestination?.id != R.id.categoriesFragment) {
                    findNavController().navigate(R.id.categoriesFragment)
                }
            }
            R.id.navPrinters -> {
                if (findNavController().currentDestination?.id != R.id.printersFragment) {
                    findNavController().navigate(R.id.printersFragment)
                }
            }
        }
        (requireActivity() as MainActivity).binding.drawerLayout.closeDrawer(GravityCompat.START)

        hideKeyboard()

        return true
    }

    private fun clearCheque() {
        if (binding.tvCountCheques.text != "0") {
            showDialogWith2Option(
                getString(R.string.str_receipt_clearing),
                getString(R.string.str_receipt_clearing_info),
                getString(R.string.str_cancel),
                getString(R.string.str_clear),
                {
                    clearCheques()
                },
                {})
        } else {
            toast(getString(R.string.str_no_cheques_to_clear))
        }
    }

    private fun clearCheques() {
        viewModel.clearCheque()
    }

    private fun showDialogWith2Option(title: String, msg: String, negativeButton: String, positiveButton: String, ok: () -> Unit, cancel: () -> Unit) {
        val dialog = AlertDialog.Builder(requireContext())
        dialog.setTitle(title)
        dialog.setMessage(msg)
        dialog.setCancelable(true)
        dialog.setPositiveButton(positiveButton) { _, _ ->
            ok.invoke()
        }
        dialog.setNegativeButton(negativeButton) { _, _ ->
            cancel.invoke()
        }
        dialog.create()
        dialog.show()
    }

    private fun logOut() {
        SharedPref(requireContext()).saveBoolean("isLoggedIn", false)
        findNavController().navigate(R.id.passwordFragment)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    requireActivity().finish()
                }
            })

        setToolbar()

        setProcess()

    }

    override fun onResume() {
        super.onResume()
        viewModel.getChequeSize()
    }

    private fun setProcess() {

        viewModel.getDBCategories()
        viewModel.getDBProducts()

        getDBObservers()

    }

    private fun getDBObservers() {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.dbCategory.collect {
                when(it) {
                    is UiStateList.LOADING -> {
                        showLoading()
                    }
                    is UiStateList.SUCCESS -> {
                        dismissLoading()
                        categoriesDB = it.data
                        setSpinner(categoriesDB as ArrayList)
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
            viewModel.dbProducts.collect {
                when(it) {
                    is UiStateList.LOADING -> {
                        showLoading()
                    }
                    is UiStateList.SUCCESS -> {
                        dismissLoading()
                        productsDB = it.data
                        setProducts(productsDB as ArrayList)
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

    private fun setUpObserves() {

        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.search.collect {
                when(it) {
                    is UiStateList.LOADING -> {
                        showLoading()
                    }
                    is UiStateList.SUCCESS -> {
                        dismissLoading()
                        setProducts(it.data as ArrayList<Product>)
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
            viewModel.chequeSize.collect {
                when(it) {
                    is UiStateList.LOADING -> {
                        showLoading()
                    }
                    is UiStateList.SUCCESS -> {
                        dismissLoading()
                        activeCheques = it.data as ArrayList
                        var summa = 0
                        for (i in it.data) {
                            summa += (i.quantity * i.price).toInt()
                        }
                        binding.tvCountCheques.text = summa.toString().setAsPrice()
                        (requireActivity() as MainActivity).binding.tvCountCheques.text = it.data.size.toString()
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
            viewModel.dbCategoryProducts.collect {
                when(it) {
                    is UiStateList.LOADING -> {
                        showLoading()
                    }
                    is UiStateList.SUCCESS -> {
                        dismissLoading()
                        setProducts(it.data as ArrayList<Product>)
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
            viewModel.clearCheque.collect {
                when(it) {
                    is UiStateObject.LOADING -> {
                        showLoading()
                    }
                    is UiStateObject.SUCCESS -> {
                        dismissLoading()
                        binding.tvCountCheques.text = "0"
                        (requireActivity() as MainActivity).binding.tvCountCheques.text = "0"
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

    private fun printObserver () {
        viewLifecycleOwner.lifecycleScope.launchWhenCreated {
            viewModel.createCheque.collect {
                when(it) {
                    is UiStateObject.LOADING -> {
                        showLoading()
                    }
                    is UiStateObject.SUCCESS -> {
                        (requireActivity() as MainActivity).printCheque(it.data)
                        SharedPref(requireContext()).saveInt("customer", 0)
                        viewModel.clearCheque()
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

    private fun showLoading() {
        binding.llLoading.visibility = View.VISIBLE
    }

    private fun dismissLoading() {
        binding.llLoading.visibility = View.GONE
    }

}