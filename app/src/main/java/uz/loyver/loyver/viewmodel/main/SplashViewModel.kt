package uz.loyver.loyver.viewmodel.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import uz.loyver.loyver.model.Category
import uz.loyver.loyver.model.Product
import uz.loyver.loyver.utils.UiStateList
import uz.loyver.loyver.utils.UiStateObject
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val repository: SplashRepository
): ViewModel() {

    private val _products = MutableStateFlow<UiStateList<Product>>(UiStateList.EMPTY)
    val products = _products

    fun getProducts() = viewModelScope.launch {
        _products.value = UiStateList.LOADING
        try {
            val response = repository.getProducts()
            if (response.isSuccessful) {
                _products.value = UiStateList.SUCCESS(response.body()!!)
            } else {
                _products.value = UiStateList.ERROR(response.message())
            }
        } catch (e: Exception) {
            _products.value = UiStateList.ERROR(e.localizedMessage ?: "No Connection")
        }
    }

    private val _categories = MutableStateFlow<UiStateList<Category>>(UiStateList.EMPTY)
    val categories = _categories

    fun getCategories() = viewModelScope.launch {
        _categories.value = UiStateList.LOADING
        try {
            val response = repository.getCategories()
            if (response.isSuccessful) {
                _categories.value = UiStateList.SUCCESS(response.body()!!)
            } else {
                _categories.value = UiStateList.ERROR(response.message())
            }
        } catch (e: Exception) {
            _categories.value = UiStateList.ERROR(e.localizedMessage ?: "No Connection")
        }
    }

    private val _saveProduct = MutableStateFlow<UiStateObject<Unit>>(UiStateObject.EMPTY)
    val saveProduct = _saveProduct

    fun saveProduct(product: Product) = viewModelScope.launch {
        _saveProduct.value = UiStateObject.LOADING

        try {
            val response = repository.saveProduct(product)
            _saveProduct.value = UiStateObject.SUCCESS(response)
        } catch (e: Exception) {
            _saveProduct.value = UiStateObject.ERROR(e.localizedMessage ?: "No Connection")
        }
    }

    private val _saveCategory = MutableStateFlow<UiStateObject<Unit>>(UiStateObject.EMPTY)
    val saveCategory = _saveCategory

    fun saveCategory(category: Category) = viewModelScope.launch {
        _saveProduct.value = UiStateObject.LOADING

        try {
            val response = repository.saveCategory(category)
            _saveCategory.value = UiStateObject.SUCCESS(response)
        } catch (e: Exception) {
            _saveCategory .value = UiStateObject.ERROR(e.localizedMessage ?: "No Connection")
        }
    }

    private val _deleteCategory = MutableStateFlow<UiStateObject<Unit>>(UiStateObject.EMPTY)
    val deleteCategory = _deleteCategory

    fun deleteCategory() = viewModelScope.launch {
        _deleteCategory.value = UiStateObject.LOADING
        try {
            val response = repository.deleteCategories()
            _deleteCategory.value = UiStateObject.SUCCESS(response)
        } catch (e: Exception) {
            _deleteCategory.value = UiStateObject.ERROR(e.localizedMessage ?: "No Connection")
        }
    }

    private val _deleteProduct = MutableStateFlow<UiStateObject<Unit>>(UiStateObject.EMPTY)
    val deleteProduct = _deleteProduct

    fun deleteProduct() = viewModelScope.launch {
        _deleteProduct.value = UiStateObject.LOADING
        try {
            val response = repository.deleteProducts()
            _deleteProduct.value = UiStateObject.SUCCESS(response)
        } catch (e: Exception) {
            _deleteProduct.value = UiStateObject.ERROR(e.localizedMessage ?: "No Connection")
        }
    }

}