package uz.loyver.loyver.ui.activity

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import uz.loyver.loyver.R
import uz.loyver.loyver.databinding.ActivitySplashBinding
import uz.loyver.loyver.model.Category
import uz.loyver.loyver.model.Product
import uz.loyver.loyver.utils.UiStateList
import uz.loyver.loyver.utils.UiStateObject
import uz.loyver.loyver.utils.toast
import uz.loyver.loyver.viewmodel.main.SplashViewModel

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    private val viewModel: SplashViewModel by viewModels()

    private var isCategoryFinish = false
    private var isProductFinish = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initViews()

    }

    private fun initViews() {

        showLoading()

        viewModel.deleteCategory()
        viewModel.deleteProduct()

        deleteObservers()
        setUpServerObservers()

    }

    private fun deleteObservers() {
        lifecycleScope.launchWhenCreated {
            viewModel.deleteCategory.collect {
                when(it) {
                    is UiStateObject.LOADING -> {
                    }
                    is UiStateObject.SUCCESS -> {
                        viewModel.getCategories()
                    }
                    is UiStateObject.ERROR -> {
                        dismissLoading()
                        toast(binding.root, "ERROR: ${it.message}")
                    }
                    else -> Unit
                }
            }
        }

        lifecycleScope.launchWhenCreated {
            viewModel.deleteProduct.collect {
                when(it) {
                    is UiStateObject.LOADING -> {
                    }
                    is UiStateObject.SUCCESS -> {
                        viewModel.getProducts()
                    }
                    is UiStateObject.ERROR -> {
                        dismissLoading()
                        toast(binding.root, "ERROR: ${it.message}")
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun setUpServerObservers() {
        lifecycleScope.launchWhenCreated {
            viewModel.categories.collect {
                when(it) {
                    is UiStateList.LOADING -> {
                    }
                    is UiStateList.SUCCESS -> {
                        saveCategoriesToDatabase(it.data)
                    }
                    is UiStateList.ERROR -> {
                        dismissLoading()
                        toast(binding.root, "ERROR: ${it.message}")
                    }
                    else -> Unit
                }
            }
        }

        lifecycleScope.launchWhenCreated {
            viewModel.products.collect {
                when(it) {
                    is UiStateList.LOADING -> {
                    }
                    is UiStateList.SUCCESS -> {
                        saveProductsToDatabase(it.data)
                    }
                    is UiStateList.ERROR -> {
                        dismissLoading()
                        toast(binding.root, "ERROR: ${it.message}")
                    }
                    else -> Unit
                }
            }
        }
    }

    private fun saveCategoriesToDatabase(data: List<Category>) {
        for (i in data) viewModel.saveCategory(i)
        isCategoryFinish = true
        isFinish()
    }

    private fun saveProductsToDatabase(data: List<Product>) {
        for (i in data) viewModel.saveProduct(i)
        isProductFinish = true
        isFinish()
    }

    private fun showLoading() {
        binding.llLoading.visibility = View.VISIBLE
    }

    private fun dismissLoading() {
        binding.llLoading.visibility = View.GONE
    }

    private fun isFinish() {
        if (isCategoryFinish && isProductFinish) {

            object : CountDownTimer(2000, 1000) {
                override fun onTick(l: Long) {}
                override fun onFinish() {
                    startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                    finish()
                }
            }.start()
        }
    }

}